package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;

@Repository
public interface ConfigurationMetadataRepository extends JpaRepository<ConfigurationMetadataEntity, Integer> {
    ConfigurationMetadataEntity findByCode(String code);

    @Transactional
    Integer deleteByCode(String code);
}
