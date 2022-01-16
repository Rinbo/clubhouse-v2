CREATE TABLE image_token (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  image_token_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  content_type VARCHAR(255) NOT NULL,
  CONSTRAINT pk_image_token PRIMARY KEY (id)
);

ALTER TABLE club ADD logo_id VARCHAR(255);

ALTER TABLE image_token ADD CONSTRAINT uc_image_token_imagetokenid UNIQUE (image_token_id);

CREATE INDEX ix_image_token_id ON image_token(image_token_id);