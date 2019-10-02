UPDATE rdm_sync.version
        SET code = 'SYS001'
        WHERE code = 'system';
UPDATE rdm_sync.version
        SET code = 'APP001'
        WHERE code = 'application';
UPDATE rdm_sync.field_mapping
        SET code = 'SYS001'
        WHERE code = 'system';
UPDATE rdm_sync.field_mapping
        SET code = 'APP001'
        WHERE code = 'application';