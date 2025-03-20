--liquibase formatted sql
--changeset marcelo:202503160244
--comment: set unblockReason nullable

ALTER TABLE BLOCKS
    ALTER COLUMN unblockReason TYPE VARCHAR(255),
    ALTER COLUMN unblockReason DROP NOT NULL;

--rollback ALTER TABLE BLOCKS ALTER COLUMN unblockReason SET NOT NULL;