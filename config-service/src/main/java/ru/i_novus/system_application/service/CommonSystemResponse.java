package ru.i_novus.system_application.service;

import lombok.Getter;
import ru.i_novus.system_application.api.model.SystemResponse;

@Getter
public class CommonSystemResponse extends SystemResponse {

    private String code = "common-system";

    private String name = "Общесистемные";
}
