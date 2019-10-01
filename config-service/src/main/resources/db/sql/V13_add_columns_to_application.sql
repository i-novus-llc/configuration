CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE rdm.application
    DROP CONSTRAINT application_pkey,
    DROP CONSTRAINT application_system_fk,
    ALTER COLUMN code SET NOT NULL,
    ADD CONSTRAINT unique_application_code UNIQUE(code);

ALTER TABLE rdm.application
    ADD COLUMN id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    ADD COLUMN is_deleted BOOLEAN;

COMMENT ON COLUMN rdm.application.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN rdm.application.is_deleted IS 'Признак удаленной записи';