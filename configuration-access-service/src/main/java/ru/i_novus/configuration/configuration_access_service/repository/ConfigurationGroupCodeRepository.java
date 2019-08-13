package ru.i_novus.configuration.configuration_access_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupCodeEntity;

import java.util.List;

@Repository
public interface ConfigurationGroupCodeRepository extends JpaRepository<ConfigurationGroupCodeEntity, Integer> {

    @Query("SELECT g.code FROM ConfigurationGroupCodeEntity g WHERE g.groupId =:groupId")
    List<String> findAllCodeByGroupId(@Param("groupId") Integer groupId);

    void deleteByGroupId(Integer groupId);
}
