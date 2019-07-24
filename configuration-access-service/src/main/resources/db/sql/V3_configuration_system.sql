CREATE TABLE IF NOT EXISTS scs.configuration_system (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250)
);

COMMENT ON TABLE scs.configuration_system IS 'Прикладные системы настроек';
COMMENT ON COLUMN scs.configuration_system.name IS 'Наименование';
COMMENT ON COLUMN scs.configuration_system.description IS 'Описание';