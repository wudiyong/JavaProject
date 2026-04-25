DROP TABLE IF EXISTS push_records;

CREATE TABLE push_records (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    local_ref VARCHAR(255),
    local_sha VARCHAR(255),
    remote_ref VARCHAR(255),
    remote_sha VARCHAR(255),
    push_time VARCHAR(255),
    changed_files TEXT,
    old_snapshot TEXT,
    new_snapshot TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS opencode_request_stats;

CREATE TABLE opencode_request_stats (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    git_username VARCHAR(255),
    session_id VARCHAR(255),
    model_id VARCHAR(255),
    agent VARCHAR(255),
    opencode_version VARCHAR(255),
    create_time TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS push_reports;

CREATE TABLE push_reports (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timestamp VARCHAR(255),
    hook_type VARCHAR(50),
    environment TEXT,
    push_summary TEXT,
    commit_infos TEXT,
    oc_parts TEXT,
    create_time TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
