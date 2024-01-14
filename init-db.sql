CREATE TABLE IF NOT EXISTS stats
(
    user_id    INTEGER      NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    chat_id    INTEGER      NOT NULL,
    score      INTEGER      NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
