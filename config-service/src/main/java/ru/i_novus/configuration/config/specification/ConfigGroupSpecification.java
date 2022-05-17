package ru.i_novus.configuration.config.specification;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;
import ru.i_novus.configuration.config.entity.GroupCodeEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.entity.GroupEntity_;

import javax.persistence.criteria.*;

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
            Subquery<GroupCodeEntity> groupCodeSubQuery = query.subquery(GroupCodeEntity.class);
            Root<GroupCodeEntity> groupCodeRoot = groupCodeSubQuery.from(GroupCodeEntity.class);

            groupCodeSubQuery.select(groupCodeRoot)
                    .where(builder.equal(groupCodeRoot.get(GroupCodeEntity_.group).get(GroupEntity_.id), root.get(GroupEntity_.id)),
                            builder.like(new LiteralExpression<>((CriteriaBuilderImpl) builder, criteria.getConfigCode()),
                                    builder.concat(groupCodeRoot.get(GroupCodeEntity_.code).as(String.class), ".%")));

            p = builder.and(p, builder.exists(groupCodeSubQuery));
        }

        return p;
    }
}
