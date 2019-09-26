CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS rdm.system (
  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
  code VARCHAR UNIQUE NOT NULL,
  name VARCHAR NOT NULL,
  description VARCHAR,
  is_deleted BOOLEAN
);

COMMENT ON TABLE rdm.system IS 'Прикладные системы';
COMMENT ON COLUMN rdm.system.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN rdm.system.code IS 'Код системы';
COMMENT ON COLUMN rdm.system.name IS 'Наименование системы';
COMMENT ON COLUMN rdm.system.description IS 'Описание системы';
COMMENT ON COLUMN rdm.system.is_deleted IS 'Признак удаленной записи';