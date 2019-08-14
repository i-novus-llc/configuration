CREATE TABLE IF NOT EXISTS configuration.metadata (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  service_code VARCHAR(100),
  name VARCHAR(100),
  description VARCHAR(250),
  value_type VARCHAR(100) NOT NULL,
  CONSTRAINT unique_metadata_code UNIQUE (code)
);

COMMENT ON TABLE configuration.metadata IS 'Метаданные настроек';
COMMENT ON COLUMN configuration.metadata.code IS 'Код';
COMMENT ON COLUMN configuration.metadata.service_code IS 'Код службы';
COMMENT ON COLUMN configuration.metadata.name IS 'Наименование';
COMMENT ON COLUMN configuration.metadata.description IS 'Описание';
COMMENT ON COLUMN configuration.metadata.value_type IS 'Тип значения';