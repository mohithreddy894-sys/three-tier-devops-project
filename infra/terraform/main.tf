terraform {
  required_version = ">= 1.10"
  required_providers {
    aws  = { source = "hashicorp/aws", version = "~> 6.0" }
    http = { source = "hashicorp/http", version = "~> 3.0" }
  }
}

provider "aws" {
  region = var.region
  default_tags {
    tags = { Project = "releaseboard" }
  }
}

# Your current public IP, so only you can reach the server
data "http" "my_ip" {
  url = "https://checkip.amazonaws.com"
}

data "aws_vpc" "default" {
  default = true
}

# Latest Ubuntu 24.04 image
data "aws_ssm_parameter" "ubuntu" {
  name = "/aws/service/canonical/ubuntu/server/24.04/stable/current/amd64/hvm/ebs-gp3/ami-id"
}

resource "aws_key_pair" "server" {
  key_name   = "releaseboard-key"
  public_key = file(pathexpand("~/.ssh/releaseboard.pub"))
}

resource "aws_security_group" "server" {
  name        = "releaseboard-sg"
  description = "ReleaseBoard server"
  vpc_id      = data.aws_vpc.default.id
}

resource "aws_vpc_security_group_ingress_rule" "from_my_ip" {
  for_each          = { ssh = 22, app = 8081, grafana = 3000 }
  security_group_id = aws_security_group.server.id
  cidr_ipv4         = "${chomp(data.http.my_ip.response_body)}/32"
  from_port         = each.value
  to_port           = each.value
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "all" {
  security_group_id = aws_security_group.server.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

resource "aws_instance" "server" {
  ami                         = data.aws_ssm_parameter.ubuntu.value
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.server.key_name
  vpc_security_group_ids      = [aws_security_group.server.id]
  associate_public_ip_address = true

  user_data = <<-SCRIPT
    #!/bin/bash
    set -e
    apt-get update
    apt-get install -y docker.io docker-compose-v2 git
    systemctl enable --now docker
    usermod -aG docker ubuntu
  SCRIPT

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  metadata_options {
    http_tokens = "required"
  }

  # Prevents extra charges for CPU bursting
  credit_specification {
    cpu_credits = "standard"
  }

  lifecycle {
    ignore_changes = [ami]
  }

  tags = { Name = "releaseboard-server" }
}
