ALTER TABLE training_time
    ADD last_activated TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE training_event
    ADD training_time_id BIGINT;

ALTER TABLE training_event
    ADD CONSTRAINT FK_TRAINING_EVENT_ON_TRAINING_TIME FOREIGN KEY (training_time_id) REFERENCES training_time (id) ON DELETE SET NULL;
