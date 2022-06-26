DROP INDEX idx_team_teamid;

DROP INDEX idx_teampost_teampostid;

DROP INDEX ix_announcement_Id;

DROP INDEX ix_image_token_id;

DROP INDEX ix_training_time_id;

DROP INDEX ix_users_id;

CREATE INDEX idx_team_teamid ON team (team_id);

CREATE INDEX idx_teampost_teampostid ON team_post (team_post_id);

CREATE INDEX ix_announcement_Id ON announcement (announcement_id);

CREATE INDEX ix_image_token_id ON image_token (image_token_id);

CREATE INDEX ix_training_time_id ON training_time (training_time_id);

CREATE INDEX ix_users_id ON users (user_id);