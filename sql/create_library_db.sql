-- =====================================================
--  Library Management System (LMS)
--  SQL Script: Database + Tables Creation
--  Entities: Book (A) → BibliographicRecord (B)
--  Relation: 1 → 1 unidirectional (Book → BibliographicRecord)
-- =====================================================

-- =============================================
-- Create database
-- =============================================
CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- =============================================
-- Table: bibliographic_records (B)
-- =============================================
CREATE TABLE bibliographic_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    isbn VARCHAR(17) UNIQUE,
    dewey_class VARCHAR(20),
    shelf_location VARCHAR(20),
    language VARCHAR(30)
);

-- =============================================
-- Table: books (A)
-- =============================================
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    title VARCHAR(150) NOT NULL,
    author VARCHAR(120) NOT NULL,
    publisher VARCHAR(100),
    publication_year INT,

    bibliographic_record_id BIGINT UNIQUE,

    CONSTRAINT fk_book_biblio
        FOREIGN KEY (bibliographic_record_id)
        REFERENCES bibliographic_records(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);