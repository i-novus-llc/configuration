ALTER TABLE rdm.application
    DROP CONSTRAINT application_pkey,
    DROP COLUMN id,
    ADD CONSTRAINT application_pkey PRIMARY KEY (code);

ALTER TABLE rdm.system
    DROP CONSTRAINT system_pkey,
    DROP COLUMN id,
    ADD CONSTRAINT system_pkey PRIMARY KEY (code);

DROP EXTENSION "uuid-ossp";