package ru.i_novus.configuration.configuration_access_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataJsonItem;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationSystemEntity;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationMetadataRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationSystemRepository;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с метаданными настроек
 */
@Api("REST сервис для работы с метаданными настроек")
@RestController
public class ConfigurationAccessRestServiceImpl implements ConfigurationAccessRestService {

    private ConfigurationMetadataRepository configurationMetadataRepository;
    private ConfigurationGroupRepository configurationGroupRepository;
    private ConfigurationSystemRepository configurationSystemRepository;

    @Autowired
    public void setConfigurationMetadataRepository(ConfigurationMetadataRepository configurationMetadataRepository) {
        this.configurationMetadataRepository = configurationMetadataRepository;
    }

    @Autowired
    public void setConfigurationGroupRepository(ConfigurationGroupRepository configurationGroupRepository) {
        this.configurationGroupRepository = configurationGroupRepository;
    }

    @Autowired
    public void setConfigurationSystemRepository(ConfigurationSystemRepository configurationSystemRepository) {
        this.configurationSystemRepository = configurationSystemRepository;
    }


    @Override
    @ApiOperation(value = "Список метаданных настроек", response = ConfigurationMetadataEntity.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    public List<ConfigurationMetadataJsonItem> getAllConfigurationsMetadata() {
        List<ConfigurationMetadataEntity> configurationMetadataEntities = configurationMetadataRepository.findAll();
        return configurationMetadataEntities.stream()
                .map(ConfigurationMetadataJsonItem::new)
                .collect(Collectors.toList());
    }

    @Override
    @ApiOperation(value = "Выборка метаданных настройки", response = ConfigurationMetadataEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение метаданных конкретной настройки"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    public ConfigurationMetadataJsonItem getConfigurationMetadata(@ApiParam(name = "Код настройки") String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = configurationMetadataRepository.findByCode(code);
        if (configurationMetadataEntity != null) {
            return new ConfigurationMetadataJsonItem(configurationMetadataEntity);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Настройки с кодом " + code + " не существует.");
    }

    @Override
    @ApiOperation(value = "Добавление метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сохранение метаданных успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    public void saveConfigurationMetadata(@ApiParam(name = "Метаданные новой настройки") @NotNull
                                                  ConfigurationMetadataJsonItem configurationMetadataJsonItem) {
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataJsonItem.getGroupCode());
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataJsonItem.getSystemCode());

        if (configurationGroupEntity != null || configurationSystemEntity != null) {
            ConfigurationMetadataEntity configurationMetadataEntity = new ConfigurationMetadataEntity();
            configurationMetadataEntity.setAttributes(configurationMetadataJsonItem, configurationGroupEntity, configurationSystemEntity);
            try {
                configurationMetadataRepository.save(configurationMetadataEntity);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Метаданные не могут быть сохранены", e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У настройки должна быть указана группа или система");
        }
    }

    @Override
    @Transactional
    @ApiOperation(value = "Изменение метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Изменение метаданных успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    public void updateConfigurationMetadata(@ApiParam(name = "Код настройки") String code,
                                            @ApiParam(name = "Обновленные метаданные настройки") @NotNull
                                                    ConfigurationMetadataJsonItem configurationMetadataJsonItem) {
        ConfigurationMetadataEntity configurationMetadataEntity = configurationMetadataRepository.findByCode(code);
        if (configurationMetadataEntity != null) {
            ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataJsonItem.getGroupCode());
            ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataJsonItem.getSystemCode());

            String newCode = configurationMetadataJsonItem.getCode();
            if (!code.equals(newCode) && configurationMetadataRepository.findByCode(newCode) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Настройка с кодом " + newCode + " уже существует");
            }

            if (configurationGroupEntity != null || configurationSystemEntity != null) {
                configurationMetadataEntity.setAttributes(configurationMetadataJsonItem, configurationGroupEntity, configurationSystemEntity);
                try {
                    configurationMetadataRepository.save(configurationMetadataEntity);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Метаданные не могут быть сохранены", e);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У настройки должна быть указана группа или система");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Настройки с кодом " + code + " не существует.");
        }
    }

    @Override
    @ApiOperation(value = "Удаление метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Метаданные настройки успешно удалены"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    public void deleteConfigurationMetadata(@ApiParam(name = "Код настройки") String code) {
        if (configurationMetadataRepository.deleteByCode(code) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Настройки с кодом " + code + " не существует.");
        }
    }

    /// TODO: попытаться сделать одним запросом (независимо от put/post)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadJsonFile(@RequestParam MultipartFile file) throws IOException {
        if (file != null && file.getOriginalFilename().endsWith(".json") && !file.isEmpty()) {
            List<ConfigurationMetadataJsonItem> items = new ObjectMapper().readValue(
                    file.getInputStream(), new TypeReference<List<ConfigurationMetadataJsonItem>>() {
                    });

            for (ConfigurationMetadataJsonItem i : items) {
                if (configurationMetadataRepository.findByCode(i.getCode()) == null) {
                    saveConfigurationMetadata(i);
                } else {
                    updateConfigurationMetadata(i.getCode(), i);
                }
            }
        }
    }
}
