CREATE TABLE IF NOT EXISTS configuration.metadata (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  value_type VARCHAR(100) NOT NULL,
  system_id INTEGER,
  CONSTRAINT metadata_system_fk FOREIGN KEY (system_id) REFERENCES configuration.system(id),
  CONSTRAINT unique_metadata_code UNIQUE (code)
);

COMMENT ON TABLE configuration.metadata IS 'Метаданные настроек';
COMMENT ON COLUMN configuration.metadata.code IS 'Код';
COMMENT ON COLUMN configuration.metadata.name IS 'Наименование';
COMMENT ON COLUMN configuration.metadata.description IS 'Описание';
COMMENT ON COLUMN configuration.metadata.value_type IS 'Тип значения';
COMMENT ON COLUMN configuration.metadata.system_id IS 'Идентификатор системы';