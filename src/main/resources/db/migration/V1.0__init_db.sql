CREATE TYPE REQUEST_TYPE AS ENUM ('ADD', 'REMOVE', 'EDIT');
CREATE TYPE STATUS AS ENUM ('CONFIRMED', 'REJECTED', 'IN_PROCESSING');
CREATE TYPE USER_ROLE AS ENUM ('ADMIN', 'USER');
CREATE TYPE RATING_VALUE AS ENUM ('1', '2', '3', '4', '5');

CREATE TABLE media_file
(
    file_id    SERIAL PRIMARY KEY,
    file_key   VARCHAR(255) NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    file_size  BIGINT       NOT NULL,
    file_type  VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users
(
    user_id        SERIAL PRIMARY KEY,
    avatar_file_id BIGINT REFERENCES media_file (file_id),
    username       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    user_role      USER_ROLE    NOT NULL DEFAULT 'USER'
);

CREATE TABLE user_settings
(
    setting_id            SERIAL PRIMARY KEY,
    user_id               BIGINT REFERENCES users (user_id) NOT NULL,
    disable_notifications BOOLEAN DEFAULT FALSE
);

CREATE TABLE notification
(
    notification_id SERIAL PRIMARY KEY,
    sender_id       BIGINT REFERENCES users (user_id) NOT NULL,
    title           VARCHAR(255)                      NOT NULL,
    message         TEXT                              NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_notification
(
    user_notification_id SERIAL PRIMARY KEY,
    user_id              BIGINT REFERENCES users (user_id)                NOT NULL,
    notification_id      BIGINT REFERENCES notification (notification_id) NOT NULL,
    is_read              BOOLEAN DEFAULT FALSE
);

CREATE TABLE course
(
    course_id      SERIAL PRIMARY KEY,
    creator_id     BIGINT REFERENCES users (user_id) NOT NULL,
    title          VARCHAR(255)                      NOT NULL,
    published_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE course_section
(
    section_id     SERIAL PRIMARY KEY,
    course_id      BIGINT REFERENCES course (course_id) NOT NULL,
    content        TEXT                                 NOT NULL,
    section_number INTEGER                              NOT NULL
);

CREATE TABLE course_section_media_file
(
    section_media_id SERIAL PRIMARY KEY,
    section_id       BIGINT REFERENCES course_section (section_id) NOT NULL,
    file_id          BIGINT REFERENCES media_file (file_id)        NOT NULL
);

CREATE TABLE favorite_course
(
    favorite_id SERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users (user_id)    NOT NULL,
    course_id   BIGINT REFERENCES course (course_id) NOT NULL
);

CREATE TABLE learning_progress
(
    progress_id  SERIAL PRIMARY KEY,
    user_id      BIGINT REFERENCES users (user_id)             NOT NULL,
    section_id   BIGINT REFERENCES course_section (section_id) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE course_request
(
    request_id   SERIAL PRIMARY KEY,
    user_id      BIGINT REFERENCES users (user_id)    NOT NULL,
    course_id    BIGINT REFERENCES course (course_id) NOT NULL,
    inspector_id BIGINT REFERENCES users (user_id),
    request_type REQUEST_TYPE                         NOT NULL,
    status       STATUS    DEFAULT 'IN_PROCESSING',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment      TEXT
);

CREATE TABLE course_rating
(
    rating_id SERIAL PRIMARY KEY,
    user_id   BIGINT REFERENCES users (user_id)    NOT NULL,
    course_id BIGINT REFERENCES course (course_id) NOT NULL,
    value     RATING_VALUE                         NOT NULL
);
