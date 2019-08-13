package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;

import java.util.List;

@Repository
public interface ConfigurationGroupRepository extends JpaRepository<ConfigurationGroupEntity, Integer> {
    @Query(value = "SELECT g.name FROM ConfigurationGroupEntity g LEFT JOIN ConfigurationGroupCodeEntity gc " +
            "ON g.id = gc.groupId WHERE strpos(:code, gc.code) = 1 " +
            "GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC")
    List<String> findGroupsNameByConfigurationCode(@Param("code") String code, Pageable pageable);

    @Transactional
    Integer removeById(Integer groupId);
}
