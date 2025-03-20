--liquibase formatted sql
--changeset marcelo:202503152051
--comment: blocks table create

CREATE TABLE BLOCKS (
    id BIGSERIAL PRIMARY KEY,
    blockDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    blockReason VARCHAR(255) NOT NULL,
    unblockDate TIMESTAMP NULL,
    unblockReason VARCHAR(255) NOT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

--rollback DROP TABLE BLOCKS
