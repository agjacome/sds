# Documents Schema

# --- !Ups

CREATE TABLE `documents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(254) NOT NULL,
    `abstract` TEXT NOT NULL
);

# --- !Downs

DROP TABLE `documents`;
