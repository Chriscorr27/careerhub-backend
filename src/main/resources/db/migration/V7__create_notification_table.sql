CREATE TABLE notifications (
   id BINARY(16) NOT NULL,
   user_id BINARY(16) NOT NULL,
   title VARCHAR(255) NOT NULL,
   message VARCHAR(1000) NOT NULL,
   type VARCHAR(50) NOT NULL,
   is_read BOOLEAN NOT NULL DEFAULT FALSE,
   created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

   CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_user_read ON  notifications(user_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);