ALTER TABLE configuration.config
    DROP COLUMN IF EXISTS ref_book_value;

ALTER TABLE configuration.config
    ADD COLUMN group_id INTEGER,
    ADD CONSTRAINT config_group_fk FOREIGN KEY (group_id) REFERENCES configuration.config_group(id);

COMMENT
ON COLUMN configuration.config.group_id IS 'Идентификатор группы настроек';
