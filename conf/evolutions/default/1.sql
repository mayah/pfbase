# Creates PARTAKE configuration table.

# --- !Ups

CREATE TABLE Users(
    id          UUID            PRIMARY KEY,
    name        VARCHAR(16)     NOT NULL
);

-- Since we don't want to have this for long in memory.
CREATE TABLE UserEmailPasswords(
    userId          UUID                PRIMARY KEY,
    email           VARCHAR(256)        NOT NULL,
    hashedPassword  VARCHAR(256)        NOT NULL
);
CREATE UNIQUE INDEX EmailOnUserEmailPasswords ON UserEmailPasswords(email);


CREATE TABLE UserTwitterLinks(
    id                  UUID        PRIMARY KEY,
    userId              UUID        NOT NULL,
    twitterId           BIGINT      NOT NULL,
    screenName          TEXT        NOT NULL,
    name                TEXT        NOT NULL,
    profileImageURL     TEXT        NOT NULL,
    accessToken         TEXT,
    accessTokenSecret   TEXT
);
CREATE INDEX UserIdOnUserTwitterLinks ON UserTwitterLinks(userId);
CREATE UNIQUE INDEX TwitterIdOnUsrTwitterLinks ON UserTwitterLinks(twitterId);

# --- !Downs

DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS UserPasswords;
DROP TABLE IF EXISTS UserTwitterLinks;

