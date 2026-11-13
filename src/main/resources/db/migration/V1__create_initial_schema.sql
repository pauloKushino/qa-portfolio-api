-- QA Portfolio API - Initial Schema

CREATE TABLE IF NOT EXISTS project (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    repository_url VARCHAR(500),
    demo_url VARCHAR(500),
    tech_stack TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_suite (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL, -- UNIT, INTEGRATION, CONTRACT, E2E
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_case (
    id BIGSERIAL PRIMARY KEY,
    test_suite_id BIGINT NOT NULL REFERENCES test_suite(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, READY, AUTOMATED, DEPRECATED
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    tags TEXT, -- comma-separated tags
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_run (
    id BIGSERIAL PRIMARY KEY,
    test_suite_id BIGINT NOT NULL REFERENCES test_suite(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, RUNNING, PASSED, FAILED, CANCELLED
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    total_tests INT DEFAULT 0,
    passed_tests INT DEFAULT 0,
    failed_tests INT DEFAULT 0,
    skipped_tests INT DEFAULT 0,
    environment VARCHAR(100), -- LOCAL, CI, STAGING, PROD
    ci_build_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_result (
    id BIGSERIAL PRIMARY KEY,
    test_run_id BIGINT NOT NULL REFERENCES test_run(id) ON DELETE CASCADE,
    test_case_id BIGINT NOT NULL REFERENCES test_case(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL, -- PASSED, FAILED, SKIPPED, ERROR
    duration_ms BIGINT,
    error_message TEXT,
    stack_trace TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (test_run_id, test_case_id)
);

CREATE TABLE IF NOT EXISTS bug (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    test_case_id BIGINT REFERENCES test_case(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status VARCHAR(50) DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, FIXED, VERIFIED, CLOSED, WONT_FIX
    reported_by VARCHAR(100),
    assigned_to VARCHAR(100),
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    closed_at TIMESTAMP
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_test_case_suite ON test_case(test_suite_id);
CREATE INDEX IF NOT EXISTS idx_test_run_suite ON test_run(test_suite_id);
CREATE INDEX IF NOT EXISTS idx_test_result_run ON test_result(test_run_id);
CREATE INDEX IF NOT EXISTS idx_test_result_case ON test_result(test_case_id);
CREATE INDEX IF NOT EXISTS idx_bug_project ON bug(project_id);
CREATE INDEX IF NOT EXISTS idx_bug_status ON bug(status);