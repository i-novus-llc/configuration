package ru.i_novus.config.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.i_novus.config.service.entity.GroupCodeEntity;

import java.util.List;

@Repository
public interface GroupCodeRepository extends JpaRepository<GroupCodeEntity, String> {

    @Query("SELECT CASE WHEN (COUNT(g) > 0) THEN true ELSE false END " +
            "FROM GroupCodeEntity g WHERE g.code IN (:codes) AND g.group.id != :groupId")
    Boolean existsAtLeastOneCode(@Param("codes") List<String> codes, @Param("groupId") Integer groupId);

    void deleteByGroupId(Integer groupId);
}
