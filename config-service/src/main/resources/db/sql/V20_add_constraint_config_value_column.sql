UPDATE configuration.config c
    SET group_id = (
        SELECT id FROM configuration.config_group g
            INNER JOIN configuration.config_group_code gc
                  ON g.id = gc.group_id WHERE gc.code = c.code
                    OR strpos(c.code, gc.code || '.') = 1
        GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC LIMIT 1
        )
    WHERE group_id IS NULL;

ALTER TABLE configuration.config
    ALTER COLUMN group_id SET NOT NULL,
    DROP CONSTRAINT config_group_fk,
    ADD CONSTRAINT config_group_fk FOREIGN KEY (group_id)
        REFERENCES configuration.config_group(id) ON DELETE CASCADE;