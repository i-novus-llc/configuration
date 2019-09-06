package ru.i_novus.config.service.model;

import lombok.Getter;
import ru.i_novus.config.api.model.SystemForm;

/**
 * Общесистемные значения по умолчанию
 */
@Getter
public class CommonSystemForm extends SystemForm {
    private String code = "common-system";

    private String name = "Общесистемные";
}
