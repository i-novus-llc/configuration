CREATE TABLE IF NOT EXISTS configuration.system (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  CONSTRAINT unique_system_code UNIQUE (code)
);

COMMENT ON TABLE configuration.system IS 'Прикладные системы настроек';
COMMENT ON COLUMN configuration.system.code IS 'Код';
COMMENT ON COLUMN configuration.system.name IS 'Наименование';
COMMENT ON COLUMN configuration.system.description IS 'Описание';