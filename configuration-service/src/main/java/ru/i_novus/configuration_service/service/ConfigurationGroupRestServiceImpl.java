package ru.i_novus.configuration_service.service;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration_api.criteria.FindGroupCriteria;
import ru.i_novus.configuration_api.items.GroupResponseItem;
import ru.i_novus.configuration_api.service.ConfigurationGroupRestService;
import ru.i_novus.configuration_service.entity.GroupCodeEntity;
import ru.i_novus.configuration_service.entity.GroupEntity;
import ru.i_novus.configuration_service.entity.QGroupCodeEntity;
import ru.i_novus.configuration_service.entity.QGroupEntity;
import ru.i_novus.configuration_service.repository.GroupCodeRepository;
import ru.i_novus.configuration_service.repository.GroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Controller
public class ConfigurationGroupRestServiceImpl implements ConfigurationGroupRestService {

    private GroupRepository groupRepository;
    private GroupCodeRepository groupCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupCodeRepository(GroupCodeRepository groupCodeRepository) {
        this.groupCodeRepository = groupCodeRepository;
    }


    @Override
    public GroupResponseItem getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        List<String> codes = groupCodeRepository.findAllCodeByGroupId(groupId);
        return groupEntity.toItem(codes);
    }

    @Override
    public Page<GroupResponseItem> getAllGroup(FindGroupCriteria criteria) {
        return new PageImpl<>(findGroups(criteria), criteria, criteria.getPageSize());
    }

    ///TODO - сохранение новой группы с уже существующими кодами работает (не должно)
    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupResponseItem groupResponseItem) {
        GroupEntity groupEntity = new GroupEntity(groupResponseItem);

        final GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupRepository.save(groupEntity);
        } catch (Exception e) {
            throw new BadRequestException("Группа настроек с именем " + groupResponseItem.getName() + " уже существует");
        }

        groupCodeRepository.saveAll(getGroupCodeEntities(
                groupResponseItem.getCodes(), savedGroupEntity.getId()));
        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupResponseItem groupResponseItem) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        groupEntity.setName(groupResponseItem.getName());
        groupEntity.setDescription(groupResponseItem.getDescription());

        groupRepository.save(groupEntity);

        List<String> codes = groupCodeRepository.findAllCodeByGroupId(groupId);
        if (!groupResponseItem.getCodes().equals(codes)) {
            groupCodeRepository.deleteByGroupId(groupId);
            groupCodeRepository.saveAll(getGroupCodeEntities(groupResponseItem.getCodes(), groupId));
        }
    }

    @Override
    public void deleteGroup(Integer groupId) {
        if (groupRepository.removeById(groupId) == 0) {
            throw new NotFoundException("Группы с идентификатором " + groupId + " не существует.");
        }
    }

    private List<GroupResponseItem> findGroups(FindGroupCriteria criteria) {
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        JPAQuery<GroupResponseItem> query = new JPAQuery(entityManager);
        query.select(qGroupEntity).from(qGroupEntity).leftJoin(qGroupCodeEntity)
                .on(qGroupEntity.id.eq(qGroupCodeEntity.groupId));

        if (criteria.getCode() != null) {
            query.where(qGroupCodeEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

        /// TODO - есть вероятность, что из-за лимита\офсетта не все коды будут включены в запись группы
        /// TODO - пока так, но вероятно нужно использовать group_concat без использования внешнего преобразования
        Map<List<?>, List<String>> results =
                query.limit(criteria.getPageSize()).offset(criteria.getOffset())
                        .transform(GroupBy.groupBy(qGroupEntity.name, qGroupEntity.description)
                                .as(GroupBy.list(qGroupCodeEntity.code))
                        );

        List<GroupResponseItem> groupResponseItems = new ArrayList<>();
        for (Map.Entry<List<?>, List<String>> entry : results.entrySet()) {
            String name = (String) entry.getKey().get(0);
            String description = entry.getKey().size() > 1 ? (String) entry.getKey().get(1) : null;
            groupResponseItems.add(new GroupResponseItem(name, description, entry.getValue()));
        }

        return groupResponseItems;
    }

    private List<GroupCodeEntity> getGroupCodeEntities(List<String> codes, Integer groupId) {
        return codes.stream()
                .map(i -> new GroupCodeEntity(i, groupId))
                .collect(toList());
    }
}
