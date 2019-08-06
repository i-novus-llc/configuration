CREATE TABLE IF NOT EXISTS configuration.group_code (
  code VARCHAR(100) PRIMARY KEY,
  group_id INTEGER,
  CONSTRAINT group_code_fk FOREIGN KEY (group_id) REFERENCES configuration.group(id) ON DELETE CASCADE
);

COMMENT ON TABLE configuration.group_code IS 'Коды групп настроек';
COMMENT ON COLUMN configuration.group_code.code IS 'Код группы';
COMMENT ON COLUMN configuration.group_code.group_id IS 'Идентификатор группы';

