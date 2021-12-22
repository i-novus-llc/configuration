package ru.i_novus.configuration.config.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.service.ApplicationRestService;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.QApplicationEntity;
import ru.i_novus.configuration.config.mapper.ApplicationMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

/**
 * Реализация REST сервиса для работы с приложениями
 */
public class ApplicationRestServiceImpl implements ApplicationRestService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Page<ApplicationResponse> getAllApplications(ApplicationCriteria criteria) {
        return applicationRepository.findAll(toPredicate(), criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = Optional.ofNullable(applicationRepository.findByCode(code))
                .orElseThrow(NotFoundException::new);
        return ApplicationMapper.toApplicationResponse(applicationEntity);
    }

    private Predicate toPredicate() {
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qApplicationEntity.isDeleted.isFalse().or(qApplicationEntity.isDeleted.isNull()));

        return builder.getValue();
    }
}
