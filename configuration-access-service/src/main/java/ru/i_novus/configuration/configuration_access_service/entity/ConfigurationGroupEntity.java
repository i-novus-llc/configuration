package ru.i_novus.configuration.configuration_access_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;

import javax.persistence.*;

/**
 * Сущность Группа настроек
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "group", schema = "configuration")
public class ConfigurationGroupEntity {

    /**
     * Идентификатор группы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Наименование группы
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Описание группы
     */
    @Column(name = "description")
    private String description;


    public ConfigurationGroupEntity(ConfigurationGroupResponseItem responseItem) {
        this.name = responseItem.getName();
        this.description = responseItem.getDescription();
    }
}
