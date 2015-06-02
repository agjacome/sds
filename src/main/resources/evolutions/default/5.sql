# --- !Ups

CREATE TABLE authors (
    author_id         BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    author_pmid       BIGINT(20)   NOT NULL UNIQUE,
    author_last_name  VARCHAR(255) NOT NULL,
    author_first_name VARCHAR(255) NOT NULL,
    author_initials   VARCHAR(10)  NOT NULL,
);

# --- !Downs

DROP TABLE authors;
