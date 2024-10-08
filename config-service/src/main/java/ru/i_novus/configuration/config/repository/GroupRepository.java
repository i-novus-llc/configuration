package ru.i_novus.configuration.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.i_novus.configuration.config.entity.GroupEntity;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer>, JpaSpecificationExecutor<GroupEntity> {

    @Query("SELECT CASE WHEN (COUNT(g) > 0) THEN true ELSE false END " +
            "FROM GroupEntity g WHERE g.name = :name AND g.id != :groupId")
    Boolean existsByName(@Param("name") String name, @Param("groupId") Integer groupId);

    @Query("""
            SELECT g FROM GroupEntity g
            INNER JOIN GroupCodeEntity gc ON gc.group = g
            WHERE gc.code = :code OR cast(strpos(:code, gc.code || '.') as INTEGER)  = 1
            GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC""")
    List<GroupEntity> findGroupsByConfigCodeStarts(@Param("code") String code);

    default GroupEntity findOneGroupByConfigCodeStarts(String code) {
        List<GroupEntity> groupEntities = findGroupsByConfigCodeStarts(code);
        return groupEntities.isEmpty() ? null : groupEntities.get(0);
    }

    GroupEntity findByName(String name);
}
