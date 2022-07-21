package ru.i_novus.configuration.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.config.entity.ConfigEntity;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, JpaSpecificationExecutor<ConfigEntity> {

    ConfigEntity findByCode(String code);

    List<ConfigEntity> findByApplicationCode(String code);

    Boolean existsByCode(String code);

    void deleteByCode(String code);
}
