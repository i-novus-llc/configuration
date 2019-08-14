package ru.i_novus.configuration_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration_service.entity.MetadataEntity;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, Integer>,
        QuerydslPredicateExecutor<MetadataEntity> {

    MetadataEntity findByCode(String code);

    @Transactional
    Integer removeByCode(String code);
}
