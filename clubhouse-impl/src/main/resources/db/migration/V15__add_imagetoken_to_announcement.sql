ALTER TABLE announcement
    ADD image_token_id BIGINT;

ALTER TABLE announcement
    ADD CONSTRAINT FK_ANNOUNCEMENT_ON_IMAGETOKEN FOREIGN KEY (image_token_id) REFERENCES image_token (id);