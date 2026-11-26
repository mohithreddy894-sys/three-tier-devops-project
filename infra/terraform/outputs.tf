output "public_ip" {
  value = aws_instance.server.public_ip
}

output "ssh_command" {
  value = "ssh -i ~/.ssh/releaseboard ubuntu@${aws_instance.server.public_ip}"
}
