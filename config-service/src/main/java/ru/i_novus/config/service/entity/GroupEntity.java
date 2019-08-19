package ru.i_novus.config.service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.i_novus.config.api.items.GroupForm;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность Группа настроек
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
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
     * Коды группы
     */
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private List<GroupCodeEntity> codes = new ArrayList<>();

    public GroupEntity(GroupForm groupForm) {
        this.name = groupForm.getName();
        this.description = groupForm.getDescription();
    }

    public GroupForm toGroupForm() {
        GroupForm groupForm = new GroupForm();
        groupForm.setId(this.id);
        groupForm.setName(this.name);
        groupForm.setDescription(this.description);
        groupForm.setCodes(codes.stream().map(GroupCodeEntity::getCode).collect(Collectors.toList()));
        return groupForm;
    }

    public void setCode(String code) {
        GroupCodeEntity groupCodeEntity = new GroupCodeEntity(code, this);
        codes.add(groupCodeEntity);
    }
}
