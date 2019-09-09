package ru.i_novus.system_application.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.system_application.api.model.SystemResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonSystemResponse extends SystemResponse {

    private String code = "common-system";

    private String name = "Общесистемные";
}
