CREATE TYPE RequestType AS ENUM ('add', 'remove', 'edit');

CREATE TYPE Status AS ENUM ('confirmed', 'rejected', 'in_processing');

CREATE TYPE User_role AS ENUM ('admin', 'user');

CREATE TYPE Rating_value AS ENUM('1', '2', '3', '4', '5');

CREATE TABLE "User" (
    user_id SERIAL PRIMARY KEY,
    avatar_file_id INTEGER,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_role User_role NOT NULL DEFAULT 'user'
);

CREATE TABLE MediaFile (
    file_id SERIAL PRIMARY KEY,
    file_key VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Course (
    course_id SERIAL PRIMARY KEY,
    creator_id INTEGER,
    title VARCHAR(255),
    published_date DATE
);

CREATE TABLE CourseSection (
    section_id SERIAL PRIMARY KEY,
    course_id INTEGER,
    title VARCHAR(255),
    section_number INTEGER
);

CREATE TABLE CourseSectionMediaFile (
    section_media_id SERIAL PRIMARY KEY,
    section_id INTEGER,
    file_id INTEGER
);

CREATE TABLE FavoriteCourse (
    favorite_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    course_id INTEGER
);

CREATE TABLE LearningProgress (
    progress_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    section_id INTEGER,
    is_completed BOOLEAN
);

CREATE TABLE CourseRequest(
    request_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    course_id INTEGER,
    request_type RequestType,
    status Status,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Course_rating(
    rating_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    course_id INTEGER,
    value Rating_value
);