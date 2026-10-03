CREATE TABLE use_profile_file_metadata (
   id BINARY(16) NOT NULL,
   s3_key VARCHAR(1000) NOT NULL,
   original_filename VARCHAR(255) NOT NULL,
   content_type VARCHAR(500) NOT NULL,
   file_size BIGINT NOT NULL,
   uploaded_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

   CONSTRAINT pk_file_metadata PRIMARY KEY (id)
);