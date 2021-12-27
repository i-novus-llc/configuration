package ru.i_novus.config.api.model;

public class EmptyGroup extends ConfigGroupResponse {

    @Override
    public Integer getId() {
        return 0;
    }

    @Override
    public String getName() {
        return "Без группировки";
    }
}
