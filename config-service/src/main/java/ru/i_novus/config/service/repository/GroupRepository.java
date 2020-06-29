package ru.i_novus.config.service.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.i_novus.config.service.entity.GroupEntity;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer>, QuerydslPredicateExecutor<GroupEntity> {

    @Query(value = "SELECT g FROM GroupEntity g INNER JOIN GroupCodeEntity gc " +
            "ON g.id = gc.group.id " +
            "WHERE strpos(:code, gc.code) = 1 OR " +
            "strpos(gc.code, :code) = 1 " +
            "GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC")
    List<GroupEntity> findGroupsByConfigCode(@Param("code") String code);

    @Query("SELECT CASE WHEN (COUNT(g) > 0) THEN true ELSE false END " +
            "FROM GroupEntity g WHERE g.name = :name AND g.id != :groupId")
    Boolean existsByName(@Param("name") String name, @Param("groupId") Integer groupId);

    @Query(value = "SELECT g FROM GroupEntity g INNER JOIN GroupCodeEntity gc " +
            "ON g.id = gc.group.id WHERE gc.code = :code OR " +
            "strpos(:code, gc.code || '.') = 1 " +
            "GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC")
    List<GroupEntity> findGroupsByConfigCodeStarts(@Param("code") String code, Pageable pageable);

    default GroupEntity findOneGroupByConfigCodeStarts(String code) {
        List<GroupEntity> groupEntities = findGroupsByConfigCodeStarts(code, PageRequest.of(0, 1));
        return groupEntities.isEmpty() ? null : groupEntities.get(0);
    }

    GroupEntity findByName(String name);
}
