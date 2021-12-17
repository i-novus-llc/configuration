ALTER TABLE rdm.application
    DROP CONSTRAINT application_system_fk,
    DROP COLUMN system_code;

DROP TABLE rdm.system;