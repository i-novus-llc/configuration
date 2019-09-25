INSERT INTO rdm.system (code, name) VALUES
                ('system-auth', 'system auth'),
                ('system-security', 'system security'),
                ('test', 'test');

INSERT INTO rdm.application (code, name, system_code) VALUES
                ('app-auth', 'auth', 'system-auth'),
                ('app-security', 'security', 'system-security'),
                ('app-test-security', 'test security', 'system-security');

INSERT INTO configuration.config_group (id, name) VALUES
                (101, 'Authentication settings'),
                (102, 'Security settings'),
                (103, 'Base security settings');

INSERT INTO configuration.config_group_code(code, group_id) VALUES
                ('auth', 101),
                ('sec1', 102),
                ('sec2', 102),
                ('base-sec', 103);

INSERT INTO configuration.config (code, name, value_type, application_code) VALUES
                ('auth.config', 'something', 'STRING', 'app-auth'),
                ('sec1.url', 'name', 'STRING', 'app-security'),
                ('sec2.spring.sec-token', 'name 2', 'STRING', null);