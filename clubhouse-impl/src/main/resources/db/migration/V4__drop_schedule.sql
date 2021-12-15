ALTER TABLE training_time ADD team_id BIGINT;

ALTER TABLE training_time ADD training_time_id VARCHAR(255);

ALTER TABLE training_time ALTER COLUMN  training_time_id SET NOT NULL;

ALTER TABLE training_time ADD CONSTRAINT uc_training_time_training_time UNIQUE (training_time_id);

ALTER TABLE training_time ADD CONSTRAINT FK_TRAINING_TIME_ON_TEAM FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE schedule DROP CONSTRAINT FK_SCHEDULE_ON_TEAM;

ALTER TABLE training_time DROP CONSTRAINT FK_TRAINING_TIME_ON_SCHEDULE;

DROP TABLE schedule CASCADE;

ALTER TABLE training_time DROP COLUMN schedule_id;