--liquibase formatted sql
--changeset marcelo:202503200210
--comment: add date_created and date_moved

ALTER TABLE CARDS
    ADD COLUMN date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN date_moved TIMESTAMP;

--rollback ALTER TABLE CARDS DROP COLUMN date_created, DROP COLUMN date_moved;