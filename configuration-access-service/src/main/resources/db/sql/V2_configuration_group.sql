CREATE TABLE IF NOT EXISTS scs.configuration_group (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250)
);

COMMENT ON TABLE scs.configuration_group IS 'Группы настроек';
COMMENT ON COLUMN scs.configuration_group.name IS 'Наименование';
COMMENT ON COLUMN scs.configuration_group.description IS 'Описание';