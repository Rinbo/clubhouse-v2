ALTER TABLE announcement
    DROP CONSTRAINT FK_ANNOUNCEMENT_ON_AUTHOR;

ALTER TABLE announcement
    ADD CONSTRAINT FK_ANNOUNCEMENT_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES club_user (id) ON DELETE SET NULL;
