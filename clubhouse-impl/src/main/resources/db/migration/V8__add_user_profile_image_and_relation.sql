ALTER TABLE users
    ADD profile_image_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_PROFILEIMAGE FOREIGN KEY (profile_image_id) REFERENCES image_token (id);