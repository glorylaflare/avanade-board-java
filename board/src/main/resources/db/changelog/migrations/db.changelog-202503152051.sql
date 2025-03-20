--liquibase formatted sql
--changeset marcelo:202503152051
--comment: blocks table create

CREATE TABLE BLOCKS (
    id BIGSERIAL PRIMARY KEY,
    block_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(255) NOT NULL,
    unblock_date TIMESTAMP NULL,
    unblock_reason VARCHAR(255) NOT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

--rollback DROP TABLE BLOCKS
