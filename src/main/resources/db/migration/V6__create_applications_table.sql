CREATE TABLE applications (
  id BINARY(16) NOT NULL,
  job_id BINARY(16) NOT NULL,
  user_id BINARY(16) NOT NULL,
  hr_user_id BINARY(16) NOT NULL,

  status VARCHAR(50) NOT NULL DEFAULT 'SAVED',

  applied_at TIMESTAMP(6),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT pk_applications PRIMARY KEY (id),

  CONSTRAINT fk_applications_user
      FOREIGN KEY (user_id)
          REFERENCES users(id)
          ON DELETE CASCADE,

  CONSTRAINT fk_applications_hr_user
      FOREIGN KEY (hr_user_id)
          REFERENCES users(id)
          ON DELETE CASCADE,

  CONSTRAINT fk_application_job
      FOREIGN KEY (job_id)
          REFERENCES jobs(id)
          ON DELETE CASCADE
);

CREATE INDEX idx_applications_user_id
    ON applications(user_id);

CREATE INDEX idx_applications_hr_user_id
    ON applications(hr_user_id);

CREATE INDEX idx_applications_user_status
    ON applications(user_id, status);

CREATE INDEX idx_applications_user_created_at
    ON applications(user_id, created_at);