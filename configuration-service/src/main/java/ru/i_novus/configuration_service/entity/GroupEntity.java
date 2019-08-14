package ru.i_novus.configuration_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration_api.items.GroupResponseItem;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность Группа настроек
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "group", schema = "configuration")
public class GroupEntity {

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


    public GroupEntity(GroupResponseItem responseItem) {
        this.name = responseItem.getName();
        this.description = responseItem.getDescription();
    }

    public GroupResponseItem toItem(List<String> codes) {
        GroupResponseItem item = new GroupResponseItem();
        item.setName(this.name);
        item.setDescription(this.description);
        item.setCodes(codes);
        return item;
    }
}
