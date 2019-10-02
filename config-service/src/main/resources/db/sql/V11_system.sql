CREATE TABLE IF NOT EXISTS rdm.system (
  code VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR
);

COMMENT ON TABLE rdm.system IS 'Прикладные системы';
COMMENT ON COLUMN rdm.system.code IS 'Код системы';
COMMENT ON COLUMN rdm.system.name IS 'Наименование системы';
COMMENT ON COLUMN rdm.system.description IS 'Описание системы';
