# NamedEntities Schema

# --- !Ups

CREATE TABLE `entities` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `text` VARCHAR(254) NOT NULL,
    `category` INTEGER NOT NULL,
    `counter` BIGINT DEFAULT 0 NOT NULL
);

# --- !Downs

DROP TABLE `entities`;
