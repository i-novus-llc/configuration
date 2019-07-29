CREATE TABLE IF NOT EXISTS configuration.group (
  id SERIAL PRIMARY KEY,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  parent_id INTEGER,
  CONSTRAINT unique_group_code UNIQUE (code)
);

COMMENT ON TABLE configuration.group IS 'Группы настроек';
COMMENT ON COLUMN configuration.group.code IS 'Код';
COMMENT ON COLUMN configuration.group.name IS 'Наименование';
COMMENT ON COLUMN configuration.group.description IS 'Описание';
COMMENT ON COLUMN configuration.group.parent_id IS 'Родительская группа';