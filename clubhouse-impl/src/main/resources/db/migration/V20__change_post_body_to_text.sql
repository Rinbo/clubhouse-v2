ALTER TABLE team_post
    DROP COLUMN body;

ALTER TABLE team_post
    ADD body TEXT NOT NULL;

ALTER TABLE team_post_comment
    DROP COLUMN comment;

ALTER TABLE team_post_comment
    ADD comment TEXT NOT NULL;