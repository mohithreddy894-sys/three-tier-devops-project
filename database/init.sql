CREATE TABLE IF NOT EXISTS releases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version VARCHAR(50) NOT NULL,
    commit_id VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL,
    deployed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO releases (version, commit_id, status) VALUES ('v0.1', 'local', 'SUCCESS');
