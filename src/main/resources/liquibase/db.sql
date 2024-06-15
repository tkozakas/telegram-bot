INSERT INTO daily_message (daily_message_id, key_name, text)
VALUES ('6a035337-02af-4f92-adeb-e21141d6625d', 'stats_all_header', 'Top-10 users of all time:'),
       ('7eddda7f-fe59-427d-b5f0-398bb0a36a98', 'stats_now_header', 'Top-10 users of this year:'),
       ('3b850aef-9a09-4a8d-b539-a3ab2b41413f', 'stats_year_header', 'Top-10 users - %d'),
       ('78839690-7fb6-41bd-9e7d-e4845ce483f3', 'sentences', '[]'),
       ('434baa63-a39e-45f1-b238-e1baecbda3a7', 'stats_table', '%d. %s — *%d times*'),
       ('434baa63-a39e-45f1-b238-e1baecbaa3a8', 'no_stats_available', 'No one is registered'),
       ('55fd873a-174a-40f0-a39a-e52f6a83b7a5', 'winner_message',
        'According to my information, the winner of today''s *%s* is %s'),
       ('09c04c72-f4bb-4a52-a689-95355266960a', 'me_header', '%s, you have been the *%s* of the day %d times'),
       ('18cac7ed-dc66-4d33-81af-23ed9da072ad', 'not_registered_header', 'You are not registered yet, %s'),
       ('3daeac9e-df18-4832-aadd-88a27aea3943', 'registered_header', 'You are already registered, %s'),
       ('de3180fe-b54f-43d2-9335-ecca70af977a', 'registered_now_header', 'You are registered, %s'),
       ('31d82b40-01ed-4cae-8961-75aaca97fc45', 'stats_footer', 'Total participants — %d'),
       ('31d82b40-01ed-4cae-8961-75aacd97fc44', 'welcome_message', 'Bot activated'),
       ('31d82b40-01ed-4cae-8961-75aacd97fc16', 'not_started',
        'To start working with the bot, you need to enter the /start command');

INSERT INTO sentence (sentence_id, group_id, order_number, text, daily_message_id)
VALUES ('9f0f4230-3b9c-4cc3-9a55-c7b332d41094', 'a7c1e930-8535-4eeb-a94c-1239eb067bed', 4, '*%s* of the day - ');
