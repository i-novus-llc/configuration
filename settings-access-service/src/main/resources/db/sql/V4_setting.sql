CREATE TABLE IF NOT EXISTS scs.setting (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  value_type VARCHAR(100) NOT NULL,
  group_id INTEGER,
  system_id INTEGER,
  CONSTRAINT setting_group_fk FOREIGN KEY (group_id) REFERENCES scs.setting_group(id),
  CONSTRAINT setting_system_fk FOREIGN KEY (system_id) REFERENCES scs.setting_system(id)
);

COMMENT ON TABLE scs.setting IS 'Настройки';
COMMENT ON COLUMN scs.setting.code IS 'Код';
COMMENT ON COLUMN scs.setting.name IS 'Наименование';
COMMENT ON COLUMN scs.setting.description IS 'Описание';
COMMENT ON COLUMN scs.setting.value_type IS 'Тип значения';
COMMENT ON COLUMN scs.setting.group_id IS 'Идентификатор группы';
COMMENT ON COLUMN scs.setting.system_id IS 'Идентификатор системы';