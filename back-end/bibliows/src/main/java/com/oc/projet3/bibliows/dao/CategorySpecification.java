package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Category;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CategorySpecification implements Specification<Category> {
    private Category filter;

    public CategorySpecification(Category filter) {
        super();
        this.filter=filter;
    }

    @Override
    public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate=criteriaBuilder.conjunction();
        if(filter.getId()!= null && filter.getId()!=0){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        }
        if(filter.getLabel()!=null){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("label"), filter.getLabel()));
        }
        return criteriaBuilder.and(predicate);
    }
}
