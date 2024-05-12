CREATE TYPE RATING_VALUE AS ENUM ('1', '2', '3', '4', '5');

CREATE TABLE media_file
(
    file_id    SERIAL PRIMARY KEY,
    file_key   VARCHAR(255) NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    file_size  INTEGER      NOT NULL,
    file_type  VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users
(
    user_id        SERIAL PRIMARY KEY,
    avatar_file_id INTEGER REFERENCES media_file (file_id),
    nickname       VARCHAR(25) NOT NULL
);

CREATE TABLE notification
(
    notification_id SERIAL PRIMARY KEY,
    sender_id       INTEGER REFERENCES users (user_id) NOT NULL,
    title           VARCHAR(255)                       NOT NULL,
    message         TEXT                               NOT NULL,
    created_at      TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_notification
(
    user_notification_id SERIAL PRIMARY KEY,
    user_id              INTEGER REFERENCES users (user_id)                NOT NULL,
    notification_id      INTEGER REFERENCES notification (notification_id) NOT NULL,
    is_read              BOOLEAN DEFAULT FALSE
);

CREATE TABLE course
(
    course_id      SERIAL PRIMARY KEY,
    creator_id     INTEGER REFERENCES users (user_id),
    title          VARCHAR(255),
    published_date DATE         DEFAULT CURRENT_DATE,
    status         VARCHAR(20) DEFAULT 'IN_PROCESSING'
);

CREATE TABLE course_section
(
    section_id     SERIAL PRIMARY KEY,
    course_id      INTEGER REFERENCES course (course_id) NOT NULL,
    content        TEXT                                  NOT NULL,
    section_number INTEGER                               NOT NULL
);

CREATE TABLE learning_progress
(
    progress_id  SERIAL PRIMARY KEY,
    user_id      INTEGER REFERENCES users (user_id)             NOT NULL,
    section_id   INTEGER REFERENCES course_section (section_id) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE course_rating
(
    rating_id SERIAL PRIMARY KEY,
    user_id   INTEGER REFERENCES users (user_id)    NOT NULL,
    course_id INTEGER REFERENCES course (course_id) NOT NULL,
    value     RATING_VALUE                          NOT NULL
);

CREATE TABLE tag
(
    tag_id   SERIAL PRIMARY KEY,
    tag_name VARCHAR(255) NOT NULL
);

CREATE TABLE course_section_media_file
(
    section_media_id SERIAL PRIMARY KEY,
    section_id       INTEGER REFERENCES course_section (section_id) NOT NULL,
    file_id          INTEGER REFERENCES media_file (file_id)        NOT NULL
);

CREATE TABLE course_subscription
(
    subscription_id SERIAL PRIMARY KEY,
    user_id         INTEGER REFERENCES users (user_id)    NOT NULL,
    course_id       INTEGER REFERENCES course (course_id) NOT NULL
);

CREATE TABLE course_tag
(
    course_tag_id SERIAL PRIMARY KEY,
    course_id     INTEGER REFERENCES course (course_id) NOT NULL,
    tag_id        INTEGER REFERENCES tag (tag_id)       NOT NULL
);

-- Создание функции для обновления никнейма
CREATE OR REPLACE FUNCTION update_nickname()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.nickname := 'user' || CAST(NEW.user_id AS VARCHAR);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_nickname_trigger
    BEFORE INSERT
    ON users
    FOR EACH ROW
EXECUTE FUNCTION update_nickname();
