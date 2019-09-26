CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS rdm.application (
  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
  code VARCHAR UNIQUE NOT NULL,
  name VARCHAR NOT NULL,
  system_code VARCHAR,
  is_deleted BOOLEAN,
  CONSTRAINT application_system_fk FOREIGN KEY (system_code) REFERENCES rdm.system(code)
);

COMMENT ON TABLE rdm.application IS 'Приложения';
COMMENT ON COLUMN rdm.application.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN rdm.application.code IS 'Код приложения';
COMMENT ON COLUMN rdm.application.name IS 'Наименование приложения';
COMMENT ON COLUMN rdm.application.system_code IS 'Код прикладной системы';
COMMENT ON COLUMN rdm.application.is_deleted IS 'Признак удаленной записи';