--liquibase formatted sql
--changeset marcelo:202503152051
--comment: blocks table create

CREATE TABLE BLOCKS (
    id SERIAL PRIMARY KEY,
    data_bloqueio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo_bloqueio VARCHAR(255) NOT NULL,
    data_desbloqueio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo_desbloqueio VARCHAR(255) NOT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

--rollback DROP TABLE BLOCKS
