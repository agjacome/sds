# Annotations Schema

# --- !Ups

CREATE TABLE `annotations` (
    `id`       BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `document` BIGINT NOT NULL,
    `keyword`  BIGINT NOT NULL,
    `text`     VARCHAR(254) NOT NULL,
    `start`    BIGINT NOT NULL,
    `end`      BIGINT NOT NULL
);

ALTER TABLE `annotations` ADD CONSTRAINT `Document_FK` FOREIGN KEY(`document`) REFERENCES `documents`(`id`) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE `annotations` ADD CONSTRAINT `Keyword_FK` FOREIGN KEY(`keyword`) REFERENCES `keywords`(`id`) on UPDATE CASCADE ON DELETE CASCADE;

# --- !Downs

ALTER TABLE `annotations` DROP CONSTRAINT `Document_FK`;
ALTER TABLE `annotations` DROP CONSTRAINT `Keyword_FK`;
DROP TABLE `annotations`;
