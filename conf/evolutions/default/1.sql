# Creates PARTAKE configuration table.

# --- !Ups

CREATE TABLE Users(
    id          UUID        PRIMARY KEY,
    screenName  TEXT        NOT NULL,
);
CREATE UNIQUE INDEX ScreenNameOnUsers ON Users(screenName);

CREATE TABLE UserPasswords(
    userId          UUID                PRIMARY KEY,
    hashedPassword  VARCHAR(256)        NOT NULL,
);

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

DROP TABLE Users;
DROP TABLE UserPasswords;
DROP TABLE UserTwitterLinks;

