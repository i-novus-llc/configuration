INSERT INTO rdm_sync.version (code, sys_table, unique_sys_field, deleted_field) VALUES
                ('system', 'rdm.system', 'code', 'is_deleted'),
                ('application', 'rdm.application', 'code', 'is_deleted');

INSERT INTO rdm_sync.field_mapping (code, sys_field, sys_data_type, rdm_field) VALUES
                ('system', 'code', 'varchar', 'code'),
                ('system', 'name', 'varchar', 'name'),
                ('system', 'description', 'varchar', 'description'),
                ('application', 'code', 'varchar', 'code'),
                ('application', 'name', 'varchar', 'name'),
                ('application', 'system_code', 'varchar', 'system_code');

