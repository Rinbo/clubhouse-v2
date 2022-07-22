ALTER TABLE team
    DROP COLUMN max_age;

ALTER TABLE team
    DROP COLUMN min_age;

ALTER TABLE team
    DROP COLUMN description;

ALTER TABLE team
    ADD description TEXT;