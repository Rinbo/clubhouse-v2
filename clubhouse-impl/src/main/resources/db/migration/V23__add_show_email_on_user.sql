ALTER TABLE users
    ADD show_email BOOLEAN DEFAULT FALSE;

ALTER TABLE users
    ALTER COLUMN show_email SET NOT NULL;
