CREATE TABLE IF NOT EXISTS scs.configuration (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  value_type VARCHAR(100) NOT NULL,
  group_id INTEGER,
  system_id INTEGER,
  CONSTRAINT configuration_group_fk FOREIGN KEY (group_id) REFERENCES scs.configuration_group(id),
  CONSTRAINT configuration_system_fk FOREIGN KEY (system_id) REFERENCES scs.configuration_system(id)
);

COMMENT ON TABLE scs.configuration IS 'Настройки';
COMMENT ON COLUMN scs.configuration.code IS 'Код';
COMMENT ON COLUMN scs.configuration.name IS 'Наименование';
COMMENT ON COLUMN scs.configuration.description IS 'Описание';
COMMENT ON COLUMN scs.configuration.value_type IS 'Тип значения';
COMMENT ON COLUMN scs.configuration.group_id IS 'Идентификатор группы';
COMMENT ON COLUMN scs.configuration.system_id IS 'Идентификатор системы';