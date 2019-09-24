package ru.i_novus.system_application.service.service;

import com.querydsl.jpa.impl.JPAQuery;
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
import java.util.ArrayList;
import java.util.List;

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
        query.distinct().from(qSystemEntity).leftJoin(qApplicationEntity).on(qSystemEntity.code.eq(qApplicationEntity.system.code));

        // TODO - фильтрует системы, но почему-то не фильтрует приложения
        if (criteria.getAppCode() != null) {
            query = new JPAQuery<>(entityManager);
            query.distinct().from(qSystemEntity).innerJoin(qApplicationEntity)
                    .on(qSystemEntity.code.eq(qApplicationEntity.system.code))
                    .on(qApplicationEntity.code.containsIgnoreCase(criteria.getAppCode()));
        }

        List<String> systemCodes = criteria.getCodes();
        if (systemCodes != null && !systemCodes.isEmpty()) {
            query.where(qSystemEntity.code.in(systemCodes));
        }
        query.orderBy(qSystemEntity.code.asc());

        // настройка пагинации в зависимости от наличия общесистемных
        if (criteria.getAppCode() == null && (criteria.getCodes() == null || criteria.getCodes().isEmpty() ||
                criteria.getCodes().contains(commonSystemCode))) {
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

        Page<SystemResponse> systemResponsePage = new PageImpl<>(query.fetch(), criteria, total)
                .map(SystemMapper::toSystemResponse);
        ArrayList<SystemResponse> systemResponses = new ArrayList<>(systemResponsePage.getContent());

        long totalElements = systemResponsePage.getTotalElements();
        CommonSystemResponse commonSystemResponse = new CommonSystemResponse();

        if (criteria.getAppCode() == null &&
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
        SystemEntity systemEntity = systemRepository.findByCode(code);
        return systemEntity != null ? SystemMapper.toSystemResponse(systemEntity) : null;
    }
}
