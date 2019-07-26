package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;

@Repository
public interface ConfigurationGroupRepository extends JpaRepository<ConfigurationGroupEntity, Integer> {

    ConfigurationGroupEntity findByCode(String code);
}
