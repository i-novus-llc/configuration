CREATE TABLE IF NOT EXISTS rdm.application (
  code VARCHAR PRIMARY KEY,
  name VARCHAR,
  system_code VARCHAR,
  CONSTRAINT application_system_fk FOREIGN KEY (system_code) REFERENCES rdm.system(code)
);

COMMENT ON TABLE rdm.application IS 'Приложения';
COMMENT ON COLUMN rdm.application.code IS 'Код приложения';
COMMENT ON COLUMN rdm.application.name IS 'Наименование приложения';
COMMENT ON COLUMN rdm.application.system_code IS 'Код прикладной системы';
