package ru.i_novus.configuration.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.config.entity.ConfigEntity;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, QuerydslPredicateExecutor<ConfigEntity> {

    ConfigEntity findByCode(String code);

    @Query(nativeQuery = true, value =
            "SELECT g.id, g.name AS g_name, c.code, c.name AS c_name, c.description, c.value_type, c.default_value, c.application_code " +
            "FROM configuration.config c " +
            "INNER JOIN (SELECT * FROM ( " +
            "SELECT ROW_NUMBER() OVER (PARTITION BY c.code ORDER BY LENGTH(gc.code) DESC) AS rn, c.code, gc.group_id " +
            "FROM configuration.config c LEFT JOIN configuration.config_group_code gc " +
            "ON c.code = gc.code OR strpos(c.code, gc.code || '.') = 1 " +
            "WHERE c.application_code IS NULL OR c.application_code = CAST(?1 AS TEXT)) x " +
            "WHERE x.rn <= 1) x " +
            "ON c.code = x.code " +
            "LEFT JOIN configuration.config_group g ON g.id = x.group_id " +
            "GROUP BY g.id, c.code ORDER BY g.priority, c.code"
    )
    List<Object[]> findGroupedConfigByAppCode(@Param("code") String code);

    List<ConfigEntity> findByApplicationCode(String code);

    Boolean existsByCode(String code);

    @Transactional
    void deleteByCode(String code);
}
