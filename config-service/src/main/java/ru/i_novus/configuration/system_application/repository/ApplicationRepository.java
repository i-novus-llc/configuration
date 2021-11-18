package ru.i_novus.configuration.system_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.system_application.entity.ApplicationEntity;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String>,
        QuerydslPredicateExecutor<ApplicationEntity> {

    ApplicationEntity findByCode(String code);
}
