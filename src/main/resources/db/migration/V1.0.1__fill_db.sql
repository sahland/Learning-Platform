-- Вставка данных в таблицу media_file
INSERT INTO media_file (file_key, file_name, file_size, file_type)
SELECT md5(random()::text),
       'file_' || generate_series,
       floor(random() * 10000 + 1),
       CASE floor(random() * 3)
           WHEN 0 THEN 'image'
           WHEN 1 THEN 'video'
           ELSE 'audio'
           END
FROM generate_series(1, 100);

-- Вставка данных в таблицу users
INSERT INTO users (avatar_file_id)
SELECT file_id
FROM media_file
ORDER BY random()
LIMIT 100;

-- Вставка данных в таблицу tag
INSERT INTO tag (tag_name)
SELECT substr(md5(random()::text), 1, 10)
FROM generate_series(1, 100);

-- Вставка данных в таблицу course_section
INSERT INTO course (creator_id, title, published_date)
SELECT user_id,
       'Course ' || gs.id,
       CURRENT_DATE - INTERVAL '7 days' * (random() * 30)::int
FROM (SELECT generate_series(1, 100) AS id) AS gs
         CROSS JOIN (SELECT user_id FROM users ORDER BY random() LIMIT 100) AS u
ORDER BY random()
LIMIT 100;

-- Вставка данных в таблицу course
INSERT INTO course_section (course_id, content, section_number)
SELECT course_id,
       md5(random()::text),
       row_number() over (partition by course_id order by random())
FROM course
ORDER BY random()
LIMIT 100;


-- Вставка данных в таблицу course_tag
WITH numbered_courses AS (SELECT course_id,
                                 ROW_NUMBER() OVER (ORDER BY random()) AS course_row_number
                          FROM course),
     numbered_tags AS (SELECT tag_id,
                              ROW_NUMBER() OVER (ORDER BY random()) AS tag_row_number
                       FROM tag)
INSERT
INTO course_tag (course_id, tag_id)
SELECT nc.course_id,
       nt.tag_id
FROM numbered_courses nc
         CROSS JOIN numbered_tags nt
WHERE (nc.course_row_number, nt.tag_row_number) IN (SELECT course_row_number,
                                                           tag_row_number
                                                    FROM (SELECT ROW_NUMBER() OVER () AS course_row_number,
                                                                 ROW_NUMBER() OVER () AS tag_row_number
                                                          FROM generate_series(1, 100)) AS numbers);

-- Вставка данных в таблицу course_subscription
WITH numbered_users AS (SELECT user_id,
                               ROW_NUMBER() OVER (ORDER BY user_id) AS user_row_number
                        FROM users),
     numbered_courses AS (SELECT course_id,
                                 ROW_NUMBER() OVER (ORDER BY course_id) AS course_row_number
                          FROM course)
INSERT
INTO course_subscription (user_id, course_id)
SELECT nu.user_id,
       nc.course_id
FROM numbered_users nu
         CROSS JOIN numbered_courses nc
WHERE (nu.user_row_number, nc.course_row_number) IN (SELECT user_row_number,
                                                            course_row_number
                                                     FROM (SELECT ROW_NUMBER() OVER () AS user_row_number,
                                                                  ROW_NUMBER() OVER () AS course_row_number
                                                           FROM generate_series(1, (SELECT COUNT(*) FROM users) *
                                                                                   (SELECT COUNT(*) FROM course))) AS numbers)
ORDER BY random()
LIMIT 100;

-- Вставка данных в таблицу course_rating
INSERT INTO course_rating (user_id, course_id, value)
SELECT (SELECT user_id FROM users ORDER BY random() LIMIT 1),
       (SELECT course_id FROM course ORDER BY random() LIMIT 1),
       (SELECT value
        FROM (SELECT unnest(ARRAY ['1', '2', '3', '4', '5']::RATING_VALUE[]) AS value
              ORDER BY random()
              LIMIT 1) AS random_value)
FROM generate_series(1, 100);

-- Вставка данных в таблицу course_section_media_file
INSERT INTO course_section_media_file (section_id, file_id)
SELECT cs.section_id,
       mf.file_id
FROM (SELECT section_id FROM course_section ORDER BY random() LIMIT 100) AS cs,
     (SELECT file_id FROM media_file ORDER BY random() LIMIT 100) AS mf
ORDER BY random()
LIMIT 100;

-- Вставка данных в таблицу learning_progress
INSERT INTO learning_progress (user_id, section_id, is_completed)
SELECT user_id,
       section_id,
       CASE floor(random() * 2)
           WHEN 0 THEN FALSE
           ELSE TRUE
           END
FROM generate_series(1, 100) as s,
     LATERAL (SELECT user_id FROM users ORDER BY random() LIMIT 1) AS u,
     LATERAL (SELECT section_id FROM course_section ORDER BY random() LIMIT 1) AS cs;

-- Вставка данных в таблицу notification
INSERT INTO notification (sender_id, title, message)
SELECT user_id,
       md5(random()::text),
       md5(random()::text)
FROM generate_series(1, 100), LATERAL (SELECT user_id FROM users ORDER BY random() LIMIT 1) AS u;

-- Вставка данных в таблицу user_notification
INSERT INTO user_notification (user_id, notification_id, is_read)
SELECT user_id,
       notification_id,
       CASE floor(random() * 2)
           WHEN 0 THEN FALSE
           ELSE TRUE
           END
FROM (SELECT user_id FROM users) AS u,
     (SELECT notification_id FROM notification) AS n
ORDER BY random()
LIMIT 100;
