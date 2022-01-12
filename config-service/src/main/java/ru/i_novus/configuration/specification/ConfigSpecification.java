package ru.i_novus.configuration.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.configuration.config.entity.ConfigEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

public class ConfigSpecification implements Specification<ConfigEntity> {

    private final ConfigCriteria criteria;

    public ConfigSpecification(ConfigCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate p = builder.and();

        if (hasText(criteria.getCode())) {

        }

        if (hasText(criteria.getName())) {

        }

        if (Boolean.TRUE.equals(criteria.getIsCommonSystemConfig())) {

        }

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {

        }

        List<String> applicationCodes = criteria.getApplicationCodes();
        if (applicationCodes != null && !applicationCodes.isEmpty()) {

        }

        return p;
    }
}
