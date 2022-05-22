CREATE TABLE team_post
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    team_post_id VARCHAR(64)                             NOT NULL,
    sticky       BOOLEAN,
    title        VARCHAR(255),
    body         VARCHAR(255),
    club_user_id BIGINT,
    team_id      BIGINT                                  NOT NULL,
    CONSTRAINT pk_team_post PRIMARY KEY (id)
);

CREATE TABLE team_post_comment
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    comment      VARCHAR(255),
    team_post_id BIGINT                                  NOT NULL,
    club_user_id BIGINT,
    CONSTRAINT pk_team_post_comment PRIMARY KEY (id)
);

ALTER TABLE team
    ADD description VARCHAR(255);

ALTER TABLE team_post
    ADD CONSTRAINT uc_team_post_teampostid UNIQUE (team_post_id);

CREATE INDEX idx_teampost_teampostid ON team_post (team_post_id);

ALTER TABLE team_post_comment
    ADD CONSTRAINT FK_TEAM_POST_COMMENT_ON_CLUBUSER FOREIGN KEY (club_user_id) REFERENCES club_user (id);

ALTER TABLE team_post_comment
    ADD CONSTRAINT FK_TEAM_POST_COMMENT_ON_TEAMPOST FOREIGN KEY (team_post_id) REFERENCES team_post (id);

ALTER TABLE team_post
    ADD CONSTRAINT FK_TEAM_POST_ON_CLUBUSER FOREIGN KEY (club_user_id) REFERENCES club_user (id);

ALTER TABLE team_post
    ADD CONSTRAINT FK_TEAM_POST_ON_TEAM FOREIGN KEY (team_id) REFERENCES team (id);

CREATE INDEX ix_announcement_Id ON announcement (announcement_id);

CREATE INDEX ix_image_token_id ON image_token (image_token_id);

CREATE INDEX ix_users_id ON users (user_id);