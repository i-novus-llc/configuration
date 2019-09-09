CREATE TABLE IF NOT EXISTS configuration.system (
  code VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR
);

COMMENT ON TABLE configuration.system IS 'Прикладные системы';
COMMENT ON COLUMN configuration.system.code IS 'Код системы';
COMMENT ON COLUMN configuration.system.name IS 'Наименование системы';
COMMENT ON COLUMN configuration.system.description IS 'Описание системы';