\set csvfile `echo :DAILY_MESSAGE_CSV_FILE_PATH`

CREATE TEMP TABLE daily_message_temp
(
    key_name TEXT,
    text     TEXT
);

\copy daily_message_temp (key_name, text) FROM :'csvfile' DELIMITER ',' CSV HEADER;

INSERT INTO daily_message (daily_message_id, key_name, text)
SELECT gen_random_uuid(), key_name, text
FROM daily_message_temp;

ALTER TABLE daily_message
    ADD CONSTRAINT daily_message_pkey PRIMARY KEY (daily_message_id);
