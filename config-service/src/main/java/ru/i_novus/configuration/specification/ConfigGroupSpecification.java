package ru.i_novus.configuration.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;
import ru.i_novus.configuration.config.entity.GroupCodeEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.entity.GroupEntity_;

import javax.persistence.criteria.*;

import static org.springframework.util.StringUtils.hasText;
import static ru.i_novus.configuration.util.SpecificationUtils.toLowerCaseString;

public class ConfigGroupSpecification implements Specification<GroupEntity> {

    private final GroupCriteria criteria;

    public ConfigGroupSpecification(GroupCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<GroupEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        if (hasText(criteria.getName())) {
            p = builder.and(p, builder.like(builder.lower(root.get(GroupEntity_.name)), toLowerCaseString(criteria.getName())));
        }

        if (hasText(criteria.getCode())) {
            Subquery<GroupCodeEntity> groupCodeSubQuery = query.subquery(GroupCodeEntity.class);
            Root<GroupCodeEntity> groupCodeRoot = groupCodeSubQuery.from(GroupCodeEntity.class);

            groupCodeSubQuery.select(groupCodeRoot)
                    .where(builder.equal(groupCodeRoot.get(GroupCodeEntity_.group).get(GroupEntity_.id), root.get(GroupEntity_.id)),
                            builder.like(builder.lower(groupCodeRoot.get(GroupCodeEntity_.code)), toLowerCaseString(criteria.getCode())));

            p = builder.and(p, builder.exists(groupCodeSubQuery));
        }

        return p;
    }
}
