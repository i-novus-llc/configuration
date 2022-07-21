package ru.i_novus.configuration.config.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.configuration.config.entity.ApplicationEntity_;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity_;
import ru.i_novus.configuration.config.entity.GroupEntity_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

        p = builder.and(p, builder.isNotNull(root.get(ConfigEntity_.application)));

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