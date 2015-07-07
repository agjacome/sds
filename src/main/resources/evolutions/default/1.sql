# --- !Ups

CREATE TABLE users (
    user_id       BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_email    VARCHAR(255) NOT NULL UNIQUE,
    user_password CHAR(60)     NOT NULL
);

-- Default user is:
-- email: admin@sds.sing.ei.uvigo.es
-- pass:  sds_default_pass
INSERT INTO users (user_email, user_password) VALUES('admin@sds.sing.ei.uvigo.es', '$2a$10$Sa5AdrBNXZoTiszVrDEdyun5m2A2AvaEBYfy12tAMUuK/D3xjrtAK');

# --- !Downs

DROP TABLE users;
