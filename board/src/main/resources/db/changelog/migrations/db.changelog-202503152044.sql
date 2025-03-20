--liquibase formatted sql
--changeset marcelo:202503152044
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sort INTEGER NOT NULL,
    type VARCHAR(7) NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT unique_board_id_order UNIQUE (board_id, sort)
);

--rollback DROP TABLE BOARDS_COLUMNS
