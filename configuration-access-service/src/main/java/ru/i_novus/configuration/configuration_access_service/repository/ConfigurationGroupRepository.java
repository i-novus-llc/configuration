package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupEntity;

@Repository
public interface ConfigurationGroupRepository extends JpaRepository<ConfigurationGroupEntity, Integer> {

    ConfigurationGroupEntity findByName(String name);

    Page<ConfigurationGroupEntity> findByNameStartingWith(String groupName, Pageable pageable);

    @Transactional
    Integer removeById(Integer groupId);
}
