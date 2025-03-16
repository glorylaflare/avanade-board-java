--liquibase formatted sql
--changeset marcelo:202503151945
--comment: boards table create

CREATE TABLE BOARDS(
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

--rollback DROP TABLE BOARDS
