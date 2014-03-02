# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `annotations` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`document` BIGINT NOT NULL,`named_entity` BIGINT NOT NULL,`text` VARCHAR(254) NOT NULL,`start` BIGINT NOT NULL,`end` BIGINT NOT NULL);
create table `documents` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`title` VARCHAR(254) NOT NULL,`abstract` TEXT NOT NULL);
create table `named_entities` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`text` VARCHAR(254) NOT NULL,`category` VARCHAR(254) NOT NULL,`counter` BIGINT DEFAULT 0 NOT NULL);
alter table `annotations` add constraint `Annotation_Document_FK` foreign key(`document`) references `documents`(`id`) on update NO ACTION on delete NO ACTION;
alter table `annotations` add constraint `Annotation_NamedEntity_FL` foreign key(`named_entity`) references `named_entities`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE annotations DROP FOREIGN KEY Annotation_Document_FK;
ALTER TABLE annotations DROP FOREIGN KEY Annotation_NamedEntity_FL;
drop table `annotations`;
drop table `documents`;
drop table `named_entities`;

