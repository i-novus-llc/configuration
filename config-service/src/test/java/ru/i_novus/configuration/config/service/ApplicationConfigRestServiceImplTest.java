package ru.i_novus.configuration.config.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigGroupResponse;
import ru.i_novus.config.api.model.ConfigsApplicationResponse;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigValidationService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.repository.ConfigRepository;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Тесты группировки настроек по приложениям и группам в {@link ApplicationConfigRestServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class ApplicationConfigRestServiceImplTest {

    private static final String COMMON_SYSTEM_CODE = "application";

    @Mock
    private ConfigRepository configRepository;

    @Mock
    private ConfigValueService configValueService;

    @Mock
    private ConfigValidationService configValidationService;

    @InjectMocks
    private ApplicationConfigRestServiceImpl service;

    /**
     * Настройки без группы соседствуют с настройками, у которых группа задана.
     * Настройки без группы должны попадать в отдельную группу "Без группировки".
     */
    @Test
    void getAllConfigsPutsConfigsWithoutGroupIntoEmptyGroup() {
        GroupEntity group = group(1, "Безопасность");
        ApplicationEntity application = new ApplicationEntity("app-a", "Приложение А");

        givenConfigs(
                config("a.grouped", "Настройка в группе", application, group),
                config("a.ungrouped", "Настройка без группы", application, null)
        );
        givenCommonSystemValues(emptyMap());
        givenApplicationValues("app-a", Map.of("a.grouped", "value-1", "a.ungrouped", "value-2"));

        List<ConfigsApplicationResponse> result = service.getAllConfigs(new ApplicationConfigCriteria());

        assertEquals(1, result.size());
        List<ConfigGroupResponse> groups = result.getFirst().getGroups();
        assertEquals(2, groups.size());

        assertEquals(1, groups.getFirst().getId());
        assertEquals("Безопасность", groups.get(0).getName());
        assertEquals(List.of("a.grouped"), codesOf(groups.get(0)));

        assertEquals(0, groups.get(1).getId());
        assertEquals("Без группировки", groups.get(1).getName());
        assertEquals(List.of("a.ungrouped"), codesOf(groups.get(1)));
    }

    /**
     * Настройки нескольких приложений должны разложиться по отдельным приложениям
     * с сохранением порядка, а значения должны браться из своего приложения.
     */
    @Test
    void getAllConfigsSplitsConfigsByApplication() {
        GroupEntity group1 = group(1, "Безопасность");
        GroupEntity group2 = group(2, "Почта");
        ApplicationEntity applicationA = new ApplicationEntity("app-a", "Приложение А");
        ApplicationEntity applicationB = new ApplicationEntity("app-b", "Приложение Б");

        givenConfigs(
                config("a.one", "А, группа 1", applicationA, group1),
                config("a.two", "А, группа 2", applicationA, group2),
                config("b.one", "Б, группа 1", applicationB, group1)
        );
        givenCommonSystemValues(Map.of("a.one", "common-value"));
        givenApplicationValues("app-a", Map.of("a.one", "a-value-1"));
        givenApplicationValues("app-b", Map.of("b.one", "b-value-1"));

        List<ConfigsApplicationResponse> result = service.getAllConfigs(new ApplicationConfigCriteria());

        assertEquals(2, result.size());

        ConfigsApplicationResponse first = result.getFirst();
        assertEquals("app-a", first.getCode());
        assertEquals("Приложение А", first.getName());
        assertEquals(2, first.getGroups().size());
        assertEquals(List.of("a.one"), codesOf(first.getGroups().get(0)));
        assertEquals(List.of("a.two"), codesOf(first.getGroups().get(1)));

        ConfigsApplicationResponse second = result.get(1);
        assertEquals("app-b", second.getCode());
        assertEquals("Приложение Б", second.getName());
        assertEquals(1, second.getGroups().size());
        assertEquals(List.of("b.one"), codesOf(second.getGroups().getFirst()));

        ApplicationConfigResponse configA = first.getGroups().getFirst().getConfigs().getFirst();
        assertEquals("a-value-1", configA.getValue());
        assertEquals("common-value", configA.getCommonSystemValue());

        ApplicationConfigResponse configB = second.getGroups().getFirst().getConfigs().getFirst();
        assertEquals("b-value-1", configB.getValue());
        assertNull(configB.getCommonSystemValue());
    }

    private void givenConfigs(ConfigEntity... configs) {
        ReflectionTestUtils.setField(service, "commonSystemCode", COMMON_SYSTEM_CODE);
        when(configRepository.findAll(
                ArgumentMatchers.<Specification<ConfigEntity>>any(),
                ArgumentMatchers.any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(configs)));
    }

    private void givenCommonSystemValues(Map<String, String> values) {
        when(configValueService.getKeyValueList(COMMON_SYSTEM_CODE)).thenReturn(values);
    }

    private void givenApplicationValues(String applicationCode, Map<String, String> values) {
        when(configValueService.getKeyValueList(applicationCode)).thenReturn(values);
    }

    private static ConfigEntity config(String code, String name, ApplicationEntity application, GroupEntity group) {
        ConfigEntity config = new ConfigEntity();
        config.setCode(code);
        config.setName(name);
        config.setValueType(ValueTypeEnum.STRING);
        config.setApplication(application);
        config.setGroup(group);
        return config;
    }

    private static GroupEntity group(Integer id, String name) {
        GroupEntity group = new GroupEntity(id);
        group.setName(name);
        return group;
    }

    private static List<String> codesOf(ConfigGroupResponse group) {
        return group.getConfigs().stream().map(ApplicationConfigResponse::getCode).toList();
    }
}