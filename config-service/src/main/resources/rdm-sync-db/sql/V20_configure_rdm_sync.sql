INSERT INTO rdm_sync.version (code, sys_table, unique_sys_field, deleted_field) VALUES
                ('SYS001', 'rdm.system', 'code', 'is_deleted'),
                ('APP001', 'rdm.application', 'code', 'is_deleted');

INSERT INTO rdm_sync.field_mapping (code, sys_field, sys_data_type, rdm_field) VALUES
                ('SYS001', 'code', 'varchar', 'code'),
                ('SYS001', 'name', 'varchar', 'name'),
                ('SYS001', 'description', 'varchar', 'description'),
                ('APP001', 'code', 'varchar', 'code'),
                ('APP001', 'name', 'varchar', 'name'),
                ('APP001', 'system_code', 'varchar', 'system_code');
