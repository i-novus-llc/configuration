package ru.i_novus.configuration.system_application;

import lombok.Getter;
import ru.i_novus.system_application.api.model.SystemResponse;

@Getter
public class CommonSystemResponse extends SystemResponse {

    private String code = "common_system";

    private String name = "Общесистемные";
}
