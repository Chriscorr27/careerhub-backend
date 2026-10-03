CREATE TABLE notification_preferences (
  id BINARY(16) NOT NULL,
  user_id BINARY(16) NOT NULL,
  in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT pk_notification_preferences PRIMARY KEY (id),

  UNIQUE KEY uk_notification_preferences_user_id (user_id),

  CONSTRAINT fk_notification_preferences_user
      FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);