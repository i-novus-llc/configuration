CREATE TABLE IF NOT EXISTS configuration.config_group_code (
  code VARCHAR PRIMARY KEY,
  group_id INTEGER,
  CONSTRAINT config_group_code_config_group_fk FOREIGN KEY (group_id) REFERENCES configuration.config_group(id) ON DELETE CASCADE
);

COMMENT ON TABLE configuration.config_group_code IS 'Коды группы настроек';
COMMENT ON COLUMN configuration.config_group_code.code IS 'Код группы';
COMMENT ON COLUMN configuration.config_group_code.group_id IS 'Идентификатор группы';

