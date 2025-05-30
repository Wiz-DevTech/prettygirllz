CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role    VARCHAR(255)
);

CREATE TABLE users
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email          VARCHAR(255)                            NOT NULL,
    password_hash  VARCHAR(255)                            NOT NULL,
    sensitive_data VARCHAR(255),
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);