package ru.i_novus.config.service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.service.entity.GroupEntity;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer>, QuerydslPredicateExecutor<GroupEntity> {
    @Query(value = "SELECT g.name FROM GroupEntity g INNER JOIN GroupCodeEntity gc " +
            "ON g.id = gc.group.id WHERE strpos(:code, gc.code) = 1 " +
            "GROUP BY g.id ORDER BY length(MAX(gc.code)) DESC")
    List<String> findGroupsNameByConfigCode(@Param("code") String code, Pageable pageable);

    @Query("SELECT CASE WHEN (COUNT(g) > 0) THEN true ELSE false END " +
            "FROM GroupEntity g WHERE g.name = :name AND g.id != :groupId")
    Boolean existsByName(@Param("name") String name, @Param("groupId") Integer groupId);

    @Transactional
    Integer removeById(Integer groupId);
}
