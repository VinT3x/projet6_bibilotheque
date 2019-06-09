package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Member;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class MemberSpecification implements Specification<Member> {

    private Member filter;

    public MemberSpecification(Member filter) {
        super();
        this.filter=filter;
    }

    @Override
    public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate=criteriaBuilder.conjunction();
        if(filter.getId()!= null && filter.getId()!=0){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        }
        if(filter.getEmail()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%"+filter.getEmail().toLowerCase()+"%"));
        }
        if(filter.getLastname()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), "%"+filter.getLastname().toLowerCase()+"%"));
        }
        if(filter.getFirstname()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), "%"+filter.getFirstname().toLowerCase()+"%"));
        }

        return criteriaBuilder.and(predicate);
    }
}
