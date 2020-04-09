package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class LendingBookRepositoryCustomImpl implements LendingBookRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public LendingBook getFirstByDeadline(Book book){
        TypedQuery query = em.createQuery("SELECT lb FROM LendingBook lb WHERE lb.book=:book and lb.canceled ='false' and lb.deliverydate is null  ORDER BY lb.deadlinedate ASC", LendingBook.class)
                .setParameter("book",  book).setMaxResults(1);

        List<LendingBook> lblist = query.getResultList();

        if(!lblist.isEmpty()){
            return lblist.get(0);
        }else{
            return null;
        }
    }
}