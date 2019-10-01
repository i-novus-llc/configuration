CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE rdm.system
    DROP CONSTRAINT system_pkey,
    ALTER COLUMN code SET NOT NULL,
    ADD CONSTRAINT unique_system_code UNIQUE(code);

ALTER TABLE rdm.system
    ADD COLUMN id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    ADD COLUMN is_deleted BOOLEAN;

ALTER TABLE rdm.application
    ADD CONSTRAINT application_system_fk FOREIGN KEY (system_code) REFERENCES rdm.system(code);

COMMENT ON COLUMN rdm.system.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN rdm.system.is_deleted IS 'Признак удаленной записи';