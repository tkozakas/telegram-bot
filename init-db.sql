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


drop table if exists stats;

-- 2021
INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 1672345732, 'Tomas', 23, '2021-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 797448538, 'Maksim', 24, '2021-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 862517371, 'Sino', 23, '2021-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 677430891, 'Egor', 22, '2021-01-14 21:11:39.629221', FALSE);

-- INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
-- VALUES (uuid_generate_v4(), -1001212214703, , 'Oskar', 14, '2021-01-14 21:11:39.629221', FALSE);


-- 2022
INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 1672345732, 'Tomas', 74, '2022-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 797448538, 'Maksim', 76, '2022-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 862517371, 'Sino', 61, '2022-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 677430891, 'Egor', 70, '2022-01-14 21:11:39.629221', FALSE);

-- INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
-- VALUES (UUID(), -1001212214703, , 'Oskar', 81, '2021-01-14 21:11:39.629221', FALSE);


-- 2023
INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 1672345732, 'Tomas', 85, '2023-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 797448538, 'Maksim', 70, '2023-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 862517371, 'Sino', 67, '2023-01-14 21:11:39.629221', FALSE);

INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
VALUES (uuid_generate_v4(), -1001212214703, 677430891, 'Egor', 56, '2023-01-14 21:11:39.629221', FALSE);

-- INSERT INTO stats (stats_id, chat_id, user_id, first_name, score, created_at, is_winner)
-- VALUES (UUID(), -1001212214703, , 'Oskar', 70, '2021-01-14 21:11:39.629221', FALSE);
