package ru.i_novus.system_application.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.system_application.api.model.System;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonSystemResponse extends System {

    private String code = "common-system";

    private String name = "Общесистемные";
}
