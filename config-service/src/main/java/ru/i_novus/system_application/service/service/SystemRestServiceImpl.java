package ru.i_novus.system_application.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.SystemRestService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.entity.QSystemEntity;
import ru.i_novus.system_application.service.entity.SystemEntity;
import ru.i_novus.system_application.service.repository.SystemRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация REST сервиса для получения систем
 */
@Service
public class SystemRestServiceImpl implements SystemRestService {

    private SystemRepository systemRepository;

    @Autowired
    public void setSystemRepository(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }


    @Override
    public Page<SystemResponse> getAllSystem(SystemCriteria criteria) {
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        Page<SystemResponse> systemResponsePage = systemRepository.findAll(toPredicate(criteria), criteria)
                .map(SystemEntity::toSystemResponse);
        ArrayList<SystemResponse> systemResponses = new ArrayList<>(systemResponsePage.getContent());

        long totalElements = systemResponsePage.getTotalElements();
        CommonSystemResponse commonSystemResponse = new CommonSystemResponse();

        if ((criteria.getPageNumber() == 0 && criteria.getAppCode() == null &&
                (criteria.getCodes() == null || criteria.getCodes().isEmpty())) ||
                (criteria.getCodes() != null && criteria.getCodes().contains(commonSystemResponse.getCode()))
        ) {
            systemResponses.add(0, commonSystemResponse);
            totalElements++;
        }

        return new PageImpl<>(systemResponses, criteria, totalElements);
    }

    @Override
    public SystemResponse getSystem(String code) {
        SystemEntity systemEntity = systemRepository.findByCode(code);
        return systemEntity != null ? systemEntity.toSystemResponse() : null;
    }

    @Override
    public void saveSystem(@Valid @NotNull SystemRequest system) {
        if (systemRepository.existsByCode(system.getCode())) {
            throw new UserException("system.code.not.unique");
        }

        SystemEntity systemEntity = new SystemEntity(system);
        systemRepository.save(systemEntity);
    }

    @Override
    public void deleteSystem(String code) {
        systemRepository.deleteByCode(code);
    }

    private Predicate toPredicate(SystemCriteria criteria) {
        QSystemEntity qSystemEntity = QSystemEntity.systemEntity;
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getAppCode() != null) {
            builder.and(JPAExpressions.selectFrom(qApplicationEntity)
                    .where(qSystemEntity.code.eq(qApplicationEntity.system.code)
                            .and(qApplicationEntity.code.containsIgnoreCase(criteria.getAppCode())))
                    .exists()
            );
        }

        List<String> systemCodes = criteria.getCodes();
        if (systemCodes != null && !systemCodes.isEmpty()) {
            builder.and(qSystemEntity.code.in(systemCodes));
        }

        return builder.getValue();
    }
}
