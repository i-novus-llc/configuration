CREATE TABLE IF NOT EXISTS scs.setting_system (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250)
);

COMMENT ON TABLE scs.setting_system IS 'Прикладные системы настроек';
COMMENT ON COLUMN scs.setting_system.name IS 'Наименование';
COMMENT ON COLUMN scs.setting_system.description IS 'Описание';