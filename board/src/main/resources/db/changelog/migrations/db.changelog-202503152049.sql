--liquibase formatted sql
--changeset marcelo:202503152049
--comment: cards table create

CREATE TABLE CARDS (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES boards_columns(id) ON DELETE CASCADE
);

--rollback DROP TABLE CARDS
