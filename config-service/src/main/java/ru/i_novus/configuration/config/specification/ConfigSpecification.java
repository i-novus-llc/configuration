package ru.i_novus.configuration.config.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.entity.GroupEntity_;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.ApplicationEntity_;

import jakarta.persistence.criteria.*;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static ru.i_novus.configuration.config.specification.SpecificationUtils.toLowerCaseLikeString;

public class ConfigSpecification implements Specification<ConfigEntity> {

    private final ConfigCriteria criteria;

    public ConfigSpecification(ConfigCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        if (hasText(criteria.getCode())) {
            p = builder.and(p, builder.like(builder.lower(root.get(ConfigEntity_.code)), toLowerCaseLikeString(criteria.getCode())));
        }

        if (hasText(criteria.getName())) {
            p = builder.and(p, builder.like(builder.lower(root.get(ConfigEntity_.name)), toLowerCaseLikeString(criteria.getName())));
        }

        if (Boolean.TRUE.equals(criteria.getIsCommonSystemConfig())) {
            p = builder.and(p, builder.isNull(root.get(ConfigEntity_.application)));
        }

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            Subquery<GroupEntity> groupSubQuery = query.subquery(GroupEntity.class);
            Root<GroupEntity> groupRoot = groupSubQuery.from(GroupEntity.class);

            Join<ConfigEntity, GroupEntity> joinGroup = root.join(ConfigEntity_.group);
            Path<Integer> configGroupId = joinGroup.get(GroupEntity_.id);

            groupSubQuery.select(groupRoot)
                    .where(builder.equal(groupRoot.get(GroupEntity_.id), configGroupId),
                            groupRoot.get(GroupEntity_.id).in(groupIds));

            p = builder.and(p, builder.exists(groupSubQuery));
        }

        List<String> applicationCodes = criteria.getApplicationCodes();
        if (applicationCodes != null && !applicationCodes.isEmpty()) {
            Join<ConfigEntity, ApplicationEntity> joinApplication = root.join(ConfigEntity_.application);
            Path<String> applicationCode = joinApplication.get(ApplicationEntity_.code);
            p = builder.and(p, applicationCode.in(criteria.getApplicationCodes()));
        }

        if (criteria.getSort().stream().findFirst().isEmpty()) {
            Join<ConfigEntity, ApplicationEntity> joinApplication = root.join(ConfigEntity_.application, JoinType.LEFT);
            Path<String> applicationNamePath = joinApplication.get(ApplicationEntity_.name);
            query.orderBy(builder.asc(builder.coalesce(applicationNamePath, " ")), builder.asc(root.get("code")));
        }
        return p;
    }
}
