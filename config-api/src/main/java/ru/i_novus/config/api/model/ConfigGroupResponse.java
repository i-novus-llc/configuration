package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Группа настроек")
public class ConfigGroupResponse {

    @ApiModelProperty("Идентификатор группы")
    private Integer id;

    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Настройки")
    private List<Config> configs;

    @Data
    @ApiModel("Настройка")
    static class Config {
        @ApiModelProperty("Код настройки")
        private String code;

        @ApiModelProperty("Наименование настройки")
        private String name;

        @ApiModelProperty("Значение настройки")
        private String value;

        @ApiModelProperty("Общесистемное значение")
        private String commonSystemValue;
    }
}
