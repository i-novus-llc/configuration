package ru.i_novus.system_application.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.entity.ApplicationEntity;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;
import ru.i_novus.system_application.service.repository.ApplicationRepository;

/**
 * Реализация REST сервиса для получения приложений
 */
@Service
@Primary
public class ApplicationRestServiceImpl implements ApplicationRestService {

    private ApplicationRepository applicationRepository;

    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }


    @Override
    public Page<ApplicationResponse> getAllApplication(ApplicationCriteria criteria) {
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        return applicationRepository.findAll(toPredicate(criteria), criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = applicationRepository.findByCode(code);
        return applicationEntity != null ? ApplicationMapper.toApplicationResponse(applicationEntity) : null;
    }

    private Predicate toPredicate(ApplicationCriteria criteria) {
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getSystemCode() != null) {
            builder.and(qApplicationEntity.system.code.eq(criteria.getSystemCode()));
        }

        return builder.getValue();
    }
}
