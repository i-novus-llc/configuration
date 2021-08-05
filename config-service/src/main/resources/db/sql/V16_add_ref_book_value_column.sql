ALTER TABLE configuration.config
    ADD COLUMN ref_book_value VARCHAR;

COMMENT ON COLUMN configuration.config.ref_book_value IS 'Значения справочника';