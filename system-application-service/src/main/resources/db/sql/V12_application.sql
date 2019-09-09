CREATE TABLE IF NOT EXISTS configuration.application (
  code VARCHAR PRIMARY KEY,
  name VARCHAR,
  system_code VARCHAR,
  CONSTRAINT application_system_fk FOREIGN KEY (system_code) REFERENCES configuration.system(code)
);

COMMENT ON TABLE configuration.application IS 'Приложения';
COMMENT ON COLUMN configuration.application.code IS 'Код приложения';
COMMENT ON COLUMN configuration.application.name IS 'Наименование приложения';
COMMENT ON COLUMN configuration.application.system_code IS 'Код прикладной системы';