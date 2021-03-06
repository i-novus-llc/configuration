CREATE TABLE IF NOT EXISTS configuration.config (
  code VARCHAR PRIMARY KEY,
  application_code VARCHAR,
  name VARCHAR,
  description VARCHAR,
  value_type VARCHAR NOT NULL
);

COMMENT ON TABLE configuration.config IS 'Метаданные настроек';
COMMENT ON COLUMN configuration.config.code IS 'Код';
COMMENT ON COLUMN configuration.config.application_code IS 'Код приложения';
COMMENT ON COLUMN configuration.config.name IS 'Наименование';
COMMENT ON COLUMN configuration.config.description IS 'Описание';
COMMENT ON COLUMN configuration.config.value_type IS 'Тип значения';