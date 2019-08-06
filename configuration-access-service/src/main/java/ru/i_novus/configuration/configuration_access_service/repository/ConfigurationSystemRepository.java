package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.configuration_access_service.entity.system.ConfigurationSystemEntity;

@Repository
public interface ConfigurationSystemRepository extends JpaRepository<ConfigurationSystemEntity, Integer> {

    ConfigurationSystemEntity findByCode(String code);
}
