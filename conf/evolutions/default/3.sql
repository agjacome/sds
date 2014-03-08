# Annotations Schema

# --- !Ups

CREATE TABLE `annotations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `document` BIGINT NOT NULL,
    `named_entity` BIGINT NOT NULL,
    `text` VARCHAR(254) NOT NULL,
    `start` BIGINT NOT NULL,
    `end` BIGINT NOT NULL
);

ALTER TABLE `annotations` ADD CONSTRAINT `Document_FK` FOREIGN KEY (`document`) REFERENCES `documents`(`id`) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE `annotations` ADD CONSTRAINT `NamedEntity_FK` FOREIGN KEY (`named_entity`) REFERENCES `entities`(`id`) ON UPDATE CASCADE ON DELETE CASCADE;

# --- !Downs

ALTER TABLE `annotations` DROP FOREIGN KEY `NamedEntity_FK`;
ALTER TABLE `annotations` DROP FOREIGN KEY `Document_FK`;
DROP TABLE `annotations`;
