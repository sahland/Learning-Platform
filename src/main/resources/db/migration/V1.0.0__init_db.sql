CREATE TABLE users
(
    user_id         SERIAL PRIMARY KEY NOT NULL,
    nickname        VARCHAR(25),
    user_avatar_key VARCHAR(255),
    keycloak_login  VARCHAR(255)
);

CREATE TABLE notification
(
    notification_id SERIAL PRIMARY KEY                     NOT NULL,
    sender_id       INTEGER REFERENCES users (user_id)     NOT NULL,
    title           VARCHAR(255)                           NOT NULL,
    message         TEXT                                   NOT NULL,
    created_at      TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE course
(
    course_id         SERIAL PRIMARY KEY                  NOT NULL,
    creator_id        INTEGER REFERENCES users (user_id)  NOT NULL,
    title             VARCHAR(255)                        NOT NULL,
    published_date    DATE        DEFAULT CURRENT_DATE    NOT NULL,
    status            VARCHAR(20) DEFAULT 'IN_PROCESSING' NOT NULL,
    course_avatar_key VARCHAR(255)
);

CREATE TABLE course_section
(
    section_id     SERIAL PRIMARY KEY                    NOT NULL,
    course_id      INTEGER REFERENCES course (course_id) NOT NULL,
    title          VARCHAR(255)                          NOT NULL,
    content        TEXT                                  NOT NULL,
    section_number INTEGER                               NOT NULL
);

CREATE TABLE learning_progress
(
    progress_id  SERIAL PRIMARY KEY                             NOT NULL,
    user_id      INTEGER REFERENCES users (user_id)             NOT NULL,
    section_id   INTEGER REFERENCES course_section (section_id) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE                          NOT NULL
);

CREATE TABLE course_rating
(
    rating_id SERIAL PRIMARY KEY                    NOT NULL,
    user_id   INTEGER REFERENCES users (user_id)    NOT NULL,
    course_id INTEGER REFERENCES course (course_id) NOT NULL,
    value     INTEGER                               NOT NULL
);

CREATE TABLE tag
(
    tag_id   SERIAL PRIMARY KEY NOT NULL,
    tag_name VARCHAR(255)       NOT NULL
);

CREATE TABLE course_subscription
(
    subscription_id SERIAL PRIMARY KEY                    NOT NULL,
    user_id         INTEGER REFERENCES users (user_id)    NOT NULL,
    course_id       INTEGER REFERENCES course (course_id) NOT NULL
);

CREATE TABLE course_tag
(
    course_tag_id SERIAL PRIMARY KEY                    NOT NULL,
    course_id     INTEGER REFERENCES course (course_id) NOT NULL,
    tag_id        INTEGER REFERENCES tag (tag_id)       NOT NULL
);