package ru.i_novus.configuration.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity_;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;

import javax.persistence.criteria.*;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static ru.i_novus.configuration.util.SpecificationUtils.toLowerCaseString;

public class ConfigSpecification implements Specification<ConfigEntity> {

    private final ConfigCriteria criteria;

    public ConfigSpecification(ConfigCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        if (hasText(criteria.getCode())) {
            p = builder.and(p, builder.like(builder.lower(root.get(ConfigEntity_.code)), toLowerCaseString(criteria.getCode())));
        }

        if (hasText(criteria.getName())) {
            p = builder.and(p, builder.like(builder.lower(root.get(ConfigEntity_.name)), toLowerCaseString(criteria.getName())));
        }

        if (Boolean.TRUE.equals(criteria.getIsCommonSystemConfig())) {
            p = builder.and(p, builder.isNull(root.get(ConfigEntity_.code)));
        }

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            Subquery<GroupCodeEntity> groupCodeSubQuery = query.subquery(GroupCodeEntity.class);
            Root<GroupCodeEntity> groupCodeRoot = groupCodeSubQuery.from(GroupCodeEntity.class);

//            groupCodeSubQuery.select(groupCodeRoot)
//                    .where(builder.equal(groupCodeRoot.get(GroupCodeEntity_.group).get(ru.i_novus.configuration.config.entity.GroupEntity_.id), root.get(GroupEntity_.id)),
//                            builder.like(builder.lower(groupCodeRoot.get(GroupCodeEntity_.code)), toLowerCaseString(criteria.getCode()))
//                    );

            p = builder.and(p, builder.exists(groupCodeSubQuery));

        }

        List<String> applicationCodes = criteria.getApplicationCodes();
        if (applicationCodes != null && !applicationCodes.isEmpty()) {
            p = builder.and(p, root.get(ConfigEntity_.code).in(criteria.getApplicationCodes()));
        }

        return p;
    }
}
