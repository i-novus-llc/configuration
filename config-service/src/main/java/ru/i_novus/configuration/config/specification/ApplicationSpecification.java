package ru.i_novus.configuration.config.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.ApplicationEntity_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ApplicationSpecification implements Specification<ApplicationEntity> {

    private final ApplicationCriteria criteria;

    public ApplicationSpecification(ApplicationCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ApplicationEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        p = builder.and(p, builder.or(
                builder.isFalse(root.get(ApplicationEntity_.isDeleted)),
                builder.isNull(root.get(ApplicationEntity_.isDeleted))
        ));

        return p;
    }
}
