--liquibase formatted sql
--changeset marcelo:202503200210
--comment: add date_added and date_moved

ALTER TABLE CARDS
    ADD COLUMN date_added TIMESTAMP,
    ADD COLUMN date_moved TIMESTAMP;

--rollback ALTER TABLE CARDS DROP COLUMN date_added, DROP COLUMN date_moved;