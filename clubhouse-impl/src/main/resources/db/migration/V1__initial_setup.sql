CREATE TABLE address (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  address_id VARCHAR(255) NOT NULL,
  city VARCHAR(30) NOT NULL,
  country VARCHAR(30) NOT NULL,
  postal_code VARCHAR(10) NOT NULL,
  street VARCHAR(100) NOT NULL,
  user_id BIGINT,
  CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE club (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  club_id VARCHAR(120) NOT NULL,
  name VARCHAR(120) NOT NULL,
  path VARCHAR(120) NOT NULL,
  type VARCHAR(255) NOT NULL,
  CONSTRAINT pk_club PRIMARY KEY (id)
);

CREATE TABLE club_user (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  club_id BIGINT,
  user_id BIGINT,
  CONSTRAINT pk_club_user PRIMARY KEY (id)
);

CREATE TABLE club_user_managed_teams (
  leaders_id BIGINT NOT NULL,
  managed_teams_id BIGINT NOT NULL,
  CONSTRAINT pk_club_user_managedteams PRIMARY KEY (leaders_id, managed_teams_id)
);

CREATE TABLE club_user_roles (
  club_user_id BIGINT NOT NULL,
  roles_id BIGINT NOT NULL,
  CONSTRAINT pk_club_user_roles PRIMARY KEY (club_user_id, roles_id)
);

CREATE TABLE club_user_teams (
  members_id BIGINT NOT NULL,
  teams_id BIGINT NOT NULL,
  CONSTRAINT pk_club_user_teams PRIMARY KEY (members_id, teams_id)
);

CREATE TABLE role (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE team (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  team_id VARCHAR(255) NOT NULL,
  club_id BIGINT,
  min_age INTEGER,
  max_age INTEGER,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_team PRIMARY KEY (id)
);

CREATE TABLE users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  user_id VARCHAR(255) NOT NULL,
  date_of_birth date NOT NULL,
  email VARCHAR(120) NOT NULL,
  encrypted_password VARCHAR(255) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_login_time TIMESTAMP WITHOUT TIME ZONE,
  last_name VARCHAR(50) NOT NULL,
  managed_account BOOLEAN,
  CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_parents (
  children_id BIGINT NOT NULL,
  parents_id BIGINT NOT NULL,
  CONSTRAINT pk_users_parents PRIMARY KEY (children_id, parents_id)
);

ALTER TABLE club_user ADD CONSTRAINT uc_9625bb9ea2ff71c8113a3e10b UNIQUE (user_id, club_id);

ALTER TABLE address ADD CONSTRAINT uc_address_addressid UNIQUE (address_id);

ALTER TABLE club ADD CONSTRAINT uc_club_clubid UNIQUE (club_id);

ALTER TABLE club ADD CONSTRAINT uc_club_name UNIQUE (name);

ALTER TABLE club ADD CONSTRAINT uc_club_path UNIQUE (path);

ALTER TABLE team ADD CONSTRAINT uc_team_teamid UNIQUE (team_id);

ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users ADD CONSTRAINT uc_users_userid UNIQUE (user_id);

CREATE INDEX ix_club_id ON club(club_id);

CREATE INDEX ix_email ON users(email);

CREATE INDEX ix_users_id ON users(user_id);

ALTER TABLE address ADD CONSTRAINT FK_ADDRESS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE club_user ADD CONSTRAINT FK_CLUB_USER_ON_CLUB FOREIGN KEY (club_id) REFERENCES club (id);

ALTER TABLE club_user ADD CONSTRAINT FK_CLUB_USER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE team ADD CONSTRAINT FK_TEAM_ON_CLUB FOREIGN KEY (club_id) REFERENCES club (id);

ALTER TABLE club_user_managed_teams ADD CONSTRAINT fk_cluusemantea_on_club_user FOREIGN KEY (leaders_id) REFERENCES club_user (id);

ALTER TABLE club_user_managed_teams ADD CONSTRAINT fk_cluusemantea_on_team FOREIGN KEY (managed_teams_id) REFERENCES team (id);

ALTER TABLE club_user_roles ADD CONSTRAINT fk_cluuserol_on_club_user FOREIGN KEY (club_user_id) REFERENCES club_user (id);

ALTER TABLE club_user_roles ADD CONSTRAINT fk_cluuserol_on_role_entity FOREIGN KEY (roles_id) REFERENCES role (id);

ALTER TABLE club_user_teams ADD CONSTRAINT fk_cluusetea_on_club_user FOREIGN KEY (members_id) REFERENCES club_user (id);

ALTER TABLE club_user_teams ADD CONSTRAINT fk_cluusetea_on_team FOREIGN KEY (teams_id) REFERENCES team (id);

ALTER TABLE users_parents ADD CONSTRAINT fk_usepar_on_children FOREIGN KEY (children_id) REFERENCES users (id);

ALTER TABLE users_parents ADD CONSTRAINT fk_usepar_on_parents FOREIGN KEY (parents_id) REFERENCES users (id);