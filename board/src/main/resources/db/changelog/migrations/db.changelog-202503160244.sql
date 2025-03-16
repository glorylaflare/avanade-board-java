--liquibase formatted sql
--changeset marcelo:202503160244
--comment: set motivo_desbloqueio nullable

ALTER TABLE BLOCKS
    ALTER COLUMN motivo_desbloqueio TYPE VARCHAR(255),
    ALTER COLUMN motivo_desbloqueio DROP NOT NULL;

--rollback ALTER TABLE BLOCKS ALTER COLUMN motivo_desbloqueio SET NOT NULL;