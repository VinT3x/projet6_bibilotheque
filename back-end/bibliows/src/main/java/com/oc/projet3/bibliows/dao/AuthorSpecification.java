package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.entities.Member;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AuthorSpecification implements Specification<Author> {

    private Author filter;

    public AuthorSpecification(Author filter) {
        super();
        this.filter=filter;
    }

    @Override
    public Predicate toPredicate(Root<Author> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate=criteriaBuilder.conjunction();
        if(filter.getId()!= null && filter.getId()!=0){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        }
        if(filter.getFullname()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullname")), "%"+filter.getFullname().toLowerCase()+"%"));
        }
        if(filter.getNationality()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nationality")), "%"+filter.getNationality().toLowerCase()+"%"));
        }
        if(filter.getDateOfBirth()!=null){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("dateOfBirth"), filter.getDateOfBirth()));
        }
        if(filter.getDateOfDeath()!=null){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("dateOfDeath"), filter.getDateOfDeath()));
        }

        return criteriaBuilder.and(predicate);
    }
}
