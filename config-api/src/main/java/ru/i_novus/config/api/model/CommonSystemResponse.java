package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel("Приложение")
public class CommonSystemResponse extends ApplicationResponse {

    private String code = "common_system";

    private String name = "Общесистемные";
}
