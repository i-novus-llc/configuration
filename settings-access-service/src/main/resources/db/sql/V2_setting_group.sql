CREATE TABLE IF NOT EXISTS scs.setting_group (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250)
);

COMMENT ON TABLE scs.setting_group IS 'Группы настроек';
COMMENT ON COLUMN scs.setting_group.name IS 'Наименование';
COMMENT ON COLUMN scs.setting_group.description IS 'Описание';