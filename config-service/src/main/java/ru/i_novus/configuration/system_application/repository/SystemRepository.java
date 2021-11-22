package ru.i_novus.configuration.system_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.system_application.entity.SystemEntity;

@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, String>,
        QuerydslPredicateExecutor<SystemEntity> {

    SystemEntity findByCode(String code);
}