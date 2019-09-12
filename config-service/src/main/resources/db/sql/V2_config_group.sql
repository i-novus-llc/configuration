CREATE TABLE IF NOT EXISTS configuration.config_group (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR,
  priority INTEGER,
  CONSTRAINT unique_config_group_name UNIQUE (name)
);

COMMENT ON TABLE configuration.config_group IS 'Группы настроек';
COMMENT ON COLUMN configuration.config_group.name IS 'Наименование группы';
COMMENT ON COLUMN configuration.config_group.description IS 'Описание';
COMMENT ON COLUMN configuration.config_group.priority IS 'Приоритет';