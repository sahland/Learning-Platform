CREATE TYPE request_type AS ENUM ('add', 'remove', 'edit');
CREATE TYPE status AS ENUM ('confirmed', 'rejected', 'in_processing');
CREATE TYPE user_role AS ENUM ('admin', 'user');
CREATE TYPE rating_value AS ENUM ('1', '2', '3', '4', '5');

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
    avatar_file_id INTEGER REFERENCES media_file (file_id),
    username       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    user_role      user_role    NOT NULL DEFAULT 'user'
);

CREATE TABLE course
(
    course_id      SERIAL PRIMARY KEY,
    creator_id     INTEGER REFERENCES users (user_id),
    title          VARCHAR(255) NOT NULL,
    published_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE course_section
(
    section_id     SERIAL PRIMARY KEY,
    course_id      INTEGER REFERENCES course (course_id),
    content        TEXT    NOT NULL,
    section_number INTEGER NOT NULL
);

CREATE TABLE course_section_media_file
(
    section_media_id SERIAL PRIMARY KEY,
    section_id       INTEGER REFERENCES course_section (section_id),
    file_id          INTEGER REFERENCES media_file (file_id)
);

CREATE TABLE favorite_course
(
    favorite_id SERIAL PRIMARY KEY,
    user_id     INTEGER REFERENCES users (user_id),
    course_id   INTEGER REFERENCES course (course_id)
);

CREATE TABLE learning_progress
(
    progress_id  SERIAL PRIMARY KEY,
    user_id      INTEGER REFERENCES users (user_id),
    section_id   INTEGER REFERENCES course_section (section_id),
    is_completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE course_request
(
    request_id   SERIAL PRIMARY KEY,
    user_id      INTEGER REFERENCES users (user_id),
    course_id    INTEGER REFERENCES course (course_id),
    request_type request_type NOT NULL,
    status       status    DEFAULT 'in_processing',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment      TEXT
);

CREATE TABLE course_rating
(
    rating_id SERIAL PRIMARY KEY,
    user_id   INTEGER REFERENCES users (user_id),
    course_id INTEGER REFERENCES course (course_id),
    value     rating_value NOT NULL
);
