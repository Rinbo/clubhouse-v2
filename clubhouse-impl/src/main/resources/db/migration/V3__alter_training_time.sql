ALTER TABLE training_time ADD end_time TIME WITHOUT TIME ZONE;

ALTER TABLE training_time ADD start_time TIME WITHOUT TIME ZONE;

ALTER TABLE training_time ALTER COLUMN  end_time SET NOT NULL;

ALTER TABLE training_time ALTER COLUMN  start_time SET NOT NULL;

ALTER TABLE training_time DROP COLUMN duration;

ALTER TABLE training_time ALTER COLUMN  location SET NOT NULL;