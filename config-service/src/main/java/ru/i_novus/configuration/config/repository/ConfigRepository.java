package ru.i_novus.configuration.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.config.entity.ConfigEntity;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, QuerydslPredicateExecutor<ConfigEntity> {

    ConfigEntity findByCode(String code);

    @Query(nativeQuery = true, value =
            "SELECT a.code as a_code, a.name as a_name, " +
                    "g.id, g.name as g_name, " +
                    "c.code as c_code, c.name as c_name " +
                    "FROM configuration.config c " +
                    "LEFT JOIN configuration.config_group g ON c.group_id = g.id " +
                    "INNER JOIN rdm.application a ON c.application_code = a.code " +
                    "GROUP BY a.code, g.id, c.code ORDER BY a.name, g.priority"
    )
    List<Object[]> findGroupedApplicationConfigs();

    @Query(nativeQuery = true, value =
            "SELECT g.id, g.name as g_name, c.code, c.name as c_name " +
                    "FROM configuration.config c " +
                    "LEFT JOIN configuration.config_group g ON c.group_id = g.id " +
                    "WHERE c.application_code IS NULL " +
                    "GROUP BY g.id, c.code ORDER BY g.priority"
    )
    List<Object[]> findGroupedCommonSystemConfigs();

    List<ConfigEntity> findByApplicationCode(String code);

    Boolean existsByCode(String code);

    void deleteByCode(String code);
}
