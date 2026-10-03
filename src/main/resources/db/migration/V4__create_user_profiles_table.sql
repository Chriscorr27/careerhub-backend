CREATE TABLE user_profiles (
   id BINARY(16) NOT NULL,
   user_id BINARY(16) NOT NULL,

   name VARCHAR(150),
   phone VARCHAR(30),
   location VARCHAR(150),
   current_role VARCHAR(150),
   experience_years DECIMAL(4,1),

   linkedin_url VARCHAR(500),
   github_url VARCHAR(500),
   portfolio_url VARCHAR(500),

   created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
   updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

   profile_file_id BINARY(16) DEFAULT NULL,

   CONSTRAINT pk_user_profiles
       PRIMARY KEY (id),

   CONSTRAINT uk_user_profiles_user_id
       UNIQUE (user_id),

   CONSTRAINT fk_user_profiles_user
       FOREIGN KEY (user_id)
           REFERENCES users(id)
           ON DELETE CASCADE,

   CONSTRAINT fk_user_profiles_file
       FOREIGN KEY (profile_file_id)
           REFERENCES use_profile_file_metadata(id)
           ON DELETE SET NULL
);

CREATE INDEX idx_user_profiles_user_id
    ON user_profiles(user_id);