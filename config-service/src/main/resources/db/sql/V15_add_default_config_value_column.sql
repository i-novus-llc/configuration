ALTER TABLE configuration.config
    ADD COLUMN default_value VARCHAR;

COMMENT ON COLUMN configuration.config.default_value IS 'Значение по умолчанию';