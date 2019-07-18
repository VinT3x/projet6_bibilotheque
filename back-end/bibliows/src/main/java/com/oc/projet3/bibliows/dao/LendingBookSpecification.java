package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.LendingBook;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class LendingBookSpecification implements Specification<LendingBook> {

    private LendingBook filter;

    public LendingBookSpecification(LendingBook filter) {
        super();
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<LendingBook> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (filter.getId() != null && filter.getId() != 0) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        }

        if (filter.getDeliverydate() == null) {
            predicate.getExpressions().add(criteriaBuilder.isNull(root.get("deliverydate")));
        }else {
            predicate.getExpressions().add(criteriaBuilder.isNotNull(root.get("deliverydate")));
        }


        if (filter.getBook() != null) {
            if (filter.getBook().getTitle() != null)
                predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.join("book").get("title")), "%" + filter.getBook().getTitle().toLowerCase() + "%"));


            if (filter.getBook().getId() != null)
                predicate.getExpressions().add(criteriaBuilder.equal(root.join("book").get("id"), filter.getBook().getId()));

        }

        if (filter.getMember() != null) {
            if (filter.getMember().getEmail() != null)
                predicate.getExpressions().add(criteriaBuilder.equal(root.join("member").get("email"), filter.getMember().getEmail()));
        }

        return criteriaBuilder.and(predicate);
    }
}
