package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigGroupResponse;
import ru.i_novus.config.api.service.CommonSystemConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.repository.ConfigRepository;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommonSystemConfigRestServiceImpl implements CommonSystemConfigRestService {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    ConfigValueService configValueService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String commonSystemCode;


    @Override
    public Page<ConfigGroupResponse> getAllConfigs(ApplicationConfigCriteria criteria) {
        Map<String, String> commonSystemConfigValues = configValueService.getKeyValueList(commonSystemCode);
        List<Object[]> groupedCommonSystemConfigs = configRepository.findGroupedCommonSystemConfigs();

        return null;
    }

    @Override
    public ApplicationConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).
                orElseThrow(NotFoundException::new);

        String value = configValueService.getValue(commonSystemCode, code);
        return toConfigResponse(configEntity, value);
    }

    @Override
    public void saveApplicationConfig(String code, String value) {
        Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        configValueService.saveValue(commonSystemCode, code, value);
    }

    private ApplicationConfigResponse toConfigResponse(ConfigEntity configEntity, String value) {
        ApplicationConfigResponse configResponse = new ApplicationConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setValue(value);
        return configResponse;
    }
}
