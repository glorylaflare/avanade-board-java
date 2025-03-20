--liquibase formatted sql
--changeset marcelo:202503160244
--comment: set unblock_reason nullable

ALTER TABLE BLOCKS
    ALTER COLUMN unblock_reason TYPE VARCHAR(255),
    ALTER COLUMN unblock_reason DROP NOT NULL;

--rollback ALTER TABLE BLOCKS ALTER COLUMN unblock_reason SET NOT NULL;