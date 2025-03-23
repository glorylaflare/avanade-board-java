--liquibase formatted sql
--changeset marcelo:202503212204
--comment: card_movement_history table create

CREATE TABLE CARD_MOVEMENT_HISTORY (
    id BIGSERIAL PRIMARY KEY,
    entered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lefted_at TIMESTAMP,
    card_id BIGINT NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT cards__movements_fk FOREIGN KEY (card_id) REFERENCES cards(id),
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES boards_columns(id) ON DELETE CASCADE
);

--rollback DROP TABLE CARD_MOVEMENT_HISTORY
