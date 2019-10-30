package ru.i_novus.config.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.service.entity.ConfigEntity;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, QuerydslPredicateExecutor<ConfigEntity> {

    ConfigEntity findByCode(String code);

    @Query("SELECT g, c FROM ConfigEntity c " +
            "LEFT JOIN GroupCodeEntity gc ON c.code = gc.code OR strpos(c.code, gc.code || '.') = 1 " +
            "LEFT JOIN GroupEntity g ON g.id = gc.group.id " +
            "WHERE c.applicationCode = :code OR c.applicationCode = null " +
            "GROUP BY g.id, c.code ORDER BY g.priority, c.code"
    )
    List<Object[]> findGroupedConfigByAppCode(@Param("code") String code);

    List<ConfigEntity> findByApplicationCode(String code);

    Boolean existsByCode(String code);

    @Transactional
    void deleteByCode(String code);
}
