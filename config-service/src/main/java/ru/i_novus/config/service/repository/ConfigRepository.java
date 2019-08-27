package ru.i_novus.config.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.service.entity.ConfigEntity;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, QuerydslPredicateExecutor<ConfigEntity> {

    ConfigEntity findByCode(String code);

    Boolean existsByCode(String code);

    @Transactional
    Integer deleteByCode(String code);
}
