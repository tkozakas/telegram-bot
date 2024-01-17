CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS stats
(
    stats_id    UUID PRIMARY KEY,
    chat_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    first_name  VARCHAR(255) NOT NULL,
    score       INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    is_winner   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS stickers
(

    sticker_id  UUID PRIMARY KEY,
    file_id     VARCHAR(255) NOT NULL,
    set_name    VARCHAR(255) NOT NULL,
    is_animated BOOLEAN      NOT NULL DEFAULT FALSE,
    is_video    BOOLEAN      NOT NULL DEFAULT FALSE,
    emoji       VARCHAR(255) NOT NULL,
    file_size   INTEGER      NOT NULL
);

CREATE TABLE IF NOT EXISTS facts
(
    fact_id UUID PRIMARY KEY,
    comment VARCHAR(2500) NOT NULL,
    is_hate DECIMAL       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS daily_message
(
    daily_message_id UUID PRIMARY KEY,
    key_name         VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS sentence
(
    sentence_id      UUID PRIMARY KEY,
    group_id         UUID NOT NULL,
    daily_message_id UUID NOT NULL,
    text             VARCHAR(1000),
    FOREIGN KEY (daily_message_id) REFERENCES daily_message (daily_message_id)
);

DROP TABLE IF EXISTS sentence;
DROP TABLE IF EXISTS daily_message


DROP TABLE IF EXISTS sentence;
