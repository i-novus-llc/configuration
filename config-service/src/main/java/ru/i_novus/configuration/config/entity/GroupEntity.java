package ru.i_novus.configuration.config.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность Группа настроек
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "config_group", schema = "configuration")
public class  GroupEntity {

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

    /**
     * Приоритет группы
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * Коды группы
     */
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<GroupCodeEntity> codes = new ArrayList<>();

    public GroupEntity(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        GroupCodeEntity groupCodeEntity = new GroupCodeEntity();
        groupCodeEntity.setCode(code.strip());
        groupCodeEntity.setGroup(this);
        codes.add(groupCodeEntity);
    }
}
