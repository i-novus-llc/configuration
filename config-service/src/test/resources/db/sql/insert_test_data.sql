INSERT INTO rdm.application (code, name) VALUES
                ('app-auth', 'auth'),
                ('app-security', 'security'),
                ('app-test-security', 'test security');

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