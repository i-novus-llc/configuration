package ru.i_novus.system_application.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.i_novus.system_application.service.entity.SystemEntity;

@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, String>,
        QuerydslPredicateExecutor<SystemEntity> {

    SystemEntity findByCode(String code);

    boolean existsByCode(String code);

    void deleteByCode(String code);
}