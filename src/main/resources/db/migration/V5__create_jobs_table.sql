CREATE TABLE jobs (
  id BINARY(16) NOT NULL,
  user_id BINARY(16) NOT NULL,

  company VARCHAR(200) NOT NULL,
  job_title VARCHAR(200) NOT NULL,

  location VARCHAR(200),
  job_url VARCHAR(1000),

  employment_type VARCHAR(50),
  source VARCHAR(100),

  salary_min DECIMAL(12,2),
  salary_max DECIMAL(12,2),
  currency VARCHAR(10),

  description TEXT,
  notes TEXT,

  status VARCHAR(50) NOT NULL DEFAULT 'OPEN',

  closed_at TIMESTAMP(6),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT pk_jobs PRIMARY KEY (id),

  CONSTRAINT fk_jobs_user
      FOREIGN KEY (user_id)
          REFERENCES users(id)
          ON DELETE CASCADE
);

CREATE INDEX idx_jobs_user_id
    ON jobs(user_id);

CREATE INDEX idx_jobs_user_status
    ON jobs(user_id, status);

CREATE INDEX idx_jobs_user_created_at
    ON jobs(user_id, created_at);