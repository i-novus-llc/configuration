package ru.i_novus.configuration.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.config.entity.ApplicationEntity;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String>, JpaSpecificationExecutor<ApplicationEntity> {

    ApplicationEntity findByCode(String code);
}
