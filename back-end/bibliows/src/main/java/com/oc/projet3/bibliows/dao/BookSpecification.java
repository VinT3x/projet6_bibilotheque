package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BookSpecification implements Specification<Book> {

    private Book filter;

    public BookSpecification(Book filter) {
        super();
        this.filter=filter;
    }

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate=criteriaBuilder.conjunction();
        if(filter.getId()!= null && filter.getId()!=0){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        }
        if(filter.getCategory()!=null){
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("category"), filter.getCategory()));
        }
        if(filter.getTitle()!=null){
            predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+filter.getTitle().toLowerCase()+"%"));
        }
        if(filter.getAuthor()!=null){
            if(filter.getAuthor().getFullname()!=null){
                predicate.getExpressions().add(criteriaBuilder.like(criteriaBuilder.lower(root.join("author").get("fullname")), "%"+filter.getAuthor().getFullname().toLowerCase()+"%"));
            }

        }


        return criteriaBuilder.and(predicate);
    }
}