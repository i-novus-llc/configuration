package ru.i_novus.configuration.config.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.service.ApplicationRestService;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.mapper.ApplicationMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;
import ru.i_novus.configuration.config.specification.ApplicationSpecification;

import java.util.Optional;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
@RequiredArgsConstructor
public class ApplicationRestServiceImpl implements ApplicationRestService {

    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getAllApplications(ApplicationCriteria criteria) {
        ApplicationSpecification specification = new ApplicationSpecification(criteria);
        return applicationRepository.findAll(specification, criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = Optional.ofNullable(applicationRepository.findByCode(code))
                .orElseThrow(NotFoundException::new);
        return ApplicationMapper.toApplicationResponse(applicationEntity);
    }

}
