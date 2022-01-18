package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.service.ApplicationRestService;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.mapper.ApplicationMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;
import ru.i_novus.configuration.config.specification.ApplicationSpecification;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
public class ApplicationRestServiceImpl implements ApplicationRestService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Page<ApplicationResponse> getAllApplications(ApplicationCriteria criteria) {
        ApplicationSpecification specification = new ApplicationSpecification(criteria);
        return applicationRepository.findAll(specification, criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = Optional.ofNullable(applicationRepository.findByCode(code))
                .orElseThrow(NotFoundException::new);
        return ApplicationMapper.toApplicationResponse(applicationEntity);
    }

}
