package ru.i_novus.system_application.service.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.SystemRestService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.entity.QSystemEntity;
import ru.i_novus.system_application.service.entity.SystemEntity;
import ru.i_novus.system_application.service.mapper.SystemMapper;
import ru.i_novus.system_application.service.repository.SystemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для получения систем
 */
@Service
@Primary
public class SystemRestServiceImpl implements SystemRestService {

    private SystemRepository systemRepository;

    @Value("${config.common.system.code}")
    private String commonSystemCode;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setSystemRepository(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }


    @Override
    public Page<SystemResponse> getAllSystem(SystemCriteria criteria) {
        QSystemEntity qSystemEntity = QSystemEntity.systemEntity;
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        JPAQuery<SystemEntity> query = new JPAQuery<>(entityManager);
        query.distinct().from(qSystemEntity);
        query = Boolean.TRUE.equals(criteria.getHasApplications()) ?
                query.innerJoin(qSystemEntity.applications, qApplicationEntity) :
                query.leftJoin(qSystemEntity.applications, qApplicationEntity);

        if (criteria.getAppCode() != null) {
            query = new JPAQuery<>(entityManager);
            query.distinct().from(qSystemEntity).innerJoin(qSystemEntity.applications, qApplicationEntity)
                    .on(qApplicationEntity.code.containsIgnoreCase(criteria.getAppCode()));
        }

        if (criteria.getName() != null) {
            query.where(qSystemEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<String> systemCodes = criteria.getCodes();
        if (systemCodes != null && !systemCodes.isEmpty()) {
            query.where(qSystemEntity.code.in(systemCodes));
        }

        query.where(qSystemEntity.isDeleted.isFalse().or(qSystemEntity.isDeleted.isNull()));
        query.where(qApplicationEntity.isDeleted.isFalse().or(qApplicationEntity.isDeleted.isNull()));
        query.orderBy(qSystemEntity.code.asc());

        CommonSystemResponse commonSystemResponse = new CommonSystemResponse();

        // настройка пагинации в зависимости от наличия общесистемных
        if (criteria.getAppCode() == null &&
                (criteria.getName() == null || StringUtils.containsIgnoreCase(commonSystemResponse.getName(), criteria.getName())) &&
                (criteria.getCodes() == null || criteria.getCodes().isEmpty() || criteria.getCodes().contains(commonSystemCode))) {
            if (criteria.getPageNumber() == 0) {
                query.limit(criteria.getPageSize() - 1);
            } else {
                query.offset(criteria.getOffset() - 1);
            }
        } else {
            query.limit(criteria.getPageSize())
                    .offset(criteria.getOffset());
        }
        long total = query.fetchCount();

        // в независимости от условий и фильтраций jpa для каждой системы выводит все ее приложения
        // поэтому проводим дополнительную фильтрацию, чтобы не выводить устаревшие приложения и приложения,
        // имеющие не подходящий критерию код
        List<SystemEntity> systemEntities = query.fetch();
        for (SystemEntity systemEntity : systemEntities) {
            systemEntity.setApplications(
                    systemEntity.getApplications().stream()
                            .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()) &&
                                    (criteria.getAppCode() == null || StringUtils.containsIgnoreCase(a.getCode(), criteria.getAppCode()))
                            ).collect(Collectors.toList())
            );
        }

        Page<SystemResponse> systemResponsePage = new PageImpl<>(systemEntities, criteria, total)
                .map(SystemMapper::toSystemResponse);
        ArrayList<SystemResponse> systemResponses = new ArrayList<>(systemResponsePage.getContent());

        long totalElements = systemResponsePage.getTotalElements();

        if (criteria.getAppCode() == null &&
                (criteria.getName() == null || StringUtils.containsIgnoreCase(commonSystemResponse.getName(), criteria.getName())) &&
                ((criteria.getCodes() == null || criteria.getCodes().isEmpty()) ||
                        (criteria.getCodes() != null && criteria.getCodes().contains(commonSystemCode)))
        ) {
            if (criteria.getPageNumber() == 0) {
                systemResponses.add(0, commonSystemResponse);
            }
            totalElements++;
        }

        return new PageImpl<>(systemResponses, criteria, totalElements);
    }

    @Override
    public SystemResponse getSystem(String code) {
        SystemEntity systemEntity = Optional.ofNullable(systemRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        return SystemMapper.toSystemResponse(systemEntity);
    }
}
