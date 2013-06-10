# Creates PARTAKE configuration table.

# --- !Ups

CREATE TABLE Users(
    id              UUID            PRIMARY KEY,
    loginId         VARCHAR(16)     NOT NULL,
    nickname        VARCHAR(16)     NOT NULL,
    email           VARCHAR(256)    NOT NULL,
    hashedPassword  VARCHAR(256)    NOT NULL,
    createdAt       TIMESTAMP       NOT NULL
);

CREATE UNIQUE INDEX NicknameOnUsers ON Users(nickname);
CREATE UNIQUE INDEX EmailOnUsers ON Users(email);

# --- !Downs

DROP TABLE IF EXISTS Users;

