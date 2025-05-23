package ru.i_novus.configuration.config.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;
import ru.i_novus.configuration.config.entity.GroupCodeEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.entity.GroupEntity_;

import static org.springframework.util.StringUtils.hasText;
import static ru.i_novus.configuration.config.specification.SpecificationUtils.toLowerCaseLikeString;

public class ConfigGroupSpecification implements Specification<GroupEntity> {

    private final GroupCriteria criteria;

    public ConfigGroupSpecification(GroupCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<GroupEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        if (hasText(criteria.getName())) {
            p = builder.and(p, builder.like(builder.lower(root.get(GroupEntity_.name)), toLowerCaseLikeString(criteria.getName())));
        }

        if (hasText(criteria.getCode())) {
            Subquery<GroupCodeEntity> groupCodeSubQuery = query.subquery(GroupCodeEntity.class);
            Root<GroupCodeEntity> groupCodeRoot = groupCodeSubQuery.from(GroupCodeEntity.class);

            groupCodeSubQuery.select(groupCodeRoot)
                    .where(builder.equal(groupCodeRoot.get(GroupCodeEntity_.group).get(GroupEntity_.id), root.get(GroupEntity_.id)),
                            builder.like(builder.lower(groupCodeRoot.get(GroupCodeEntity_.code)), toLowerCaseLikeString(criteria.getCode())));

            p = builder.and(p, builder.exists(groupCodeSubQuery));
        }

        if (hasText(criteria.getConfigCode())) {
            Join<GroupEntity, GroupCodeEntity> join = root.join("codes", JoinType.LEFT);

            p = builder.and(p, builder.like(builder.lower(join.get(GroupCodeEntity_.code).as(String.class)), wildcard(criteria.getConfigCode())));
        }

        return p;
    }

    private static String wildcard(String s) {
        return "%" + s.strip().toLowerCase() + "%";
    }
}
