-- =====================================================
--  Library Management System (LMS)
--  Correct SQL Schema aligned with Java DAO implementation
-- =====================================================

-- =============================================
-- Create database
-- =============================================
DROP DATABASE IF EXISTS library_db;
CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- =============================================
-- Table: books (A)
-- =============================================
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    title VARCHAR(150) NOT NULL,
    author VARCHAR(120) NOT NULL,
    publisher VARCHAR(100),
    publication_year INT
);

-- =============================================
-- Table: bibliographic_records (B)
-- =============================================
CREATE TABLE bibliographic_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    isbn VARCHAR(17) UNIQUE,
    dewey_class VARCHAR(20),
    shelf_location VARCHAR(50),
    language VARCHAR(30),

    book_id BIGINT UNIQUE,

    CONSTRAINT fk_record_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
