package ru.i_novus.system_application.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.entity.ApplicationEntity;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.entity.SystemEntity;
import ru.i_novus.system_application.service.repository.ApplicationRepository;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.service.repository.SystemRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Реализация REST сервиса для получения приложений
 */
@Service
public class ApplicationRestServiceImpl implements ApplicationRestService {

    private ApplicationRepository applicationRepository;
    private SystemRepository systemRepository;

    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Autowired
    public void setSystemRepository(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }


    @Override
    public Page<ApplicationResponse> getAllApplication(ApplicationCriteria criteria) {
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        return applicationRepository.findAll(toPredicate(criteria), criteria)
                .map(ApplicationEntity::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = applicationRepository.findByCode(code);
        return applicationEntity != null ? applicationEntity.toApplicationResponse() : null;
    }

    @Override
    public void saveApplication(@Valid @NotNull ApplicationRequest application) {
        if (applicationRepository.existsByCode(application.getCode())) {
            throw new UserException("application.code.not.unique");
        }
        
        SystemEntity systemEntity = systemRepository.findByCode(application.getCode());
        ApplicationEntity applicationEntity = new ApplicationEntity(application, systemEntity);
        applicationRepository.save(applicationEntity);
    }

    @Override
    public void deleteApplication(String code) {
        applicationRepository.deleteByCode(code);
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
