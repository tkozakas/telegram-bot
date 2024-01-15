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
