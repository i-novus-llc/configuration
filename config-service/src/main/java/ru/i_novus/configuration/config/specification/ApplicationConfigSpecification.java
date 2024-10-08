package ru.i_novus.configuration.config.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.configuration.config.entity.ApplicationEntity_;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity_;

import jakarta.persistence.criteria.*;

import static org.springframework.util.StringUtils.hasText;
import static ru.i_novus.configuration.config.specification.SpecificationUtils.toLowerCaseLikeString;

public class ApplicationConfigSpecification implements Specification<ConfigEntity> {

    private final ApplicationConfigCriteria criteria;

    public ApplicationConfigSpecification(ApplicationConfigCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        Expression exp;
        if (Boolean.TRUE.equals(criteria.getIsCommonSystem()))
            exp = builder.isNull(root.get(ConfigEntity_.application));
        else
            exp = builder.isNotNull(root.get(ConfigEntity_.application));
        p = builder.and(p, exp);

        if (!CollectionUtils.isEmpty(criteria.getApplicationCodes()))
            p = builder.and(p, root.get(ConfigEntity_.application).get(ApplicationEntity_.code).in(criteria.getApplicationCodes()));

        if (!CollectionUtils.isEmpty(criteria.getGroupIds()))
            p = builder.and(p, root.get(ConfigEntity_.group).get(GroupEntity_.id).in(criteria.getGroupIds()));

        if (hasText(criteria.getConfigName())) {
            p = builder.and(p, builder.like(builder.lower(root.get(ConfigEntity_.name)), toLowerCaseLikeString(criteria.getConfigName())));
        }
        return p;
    }
}