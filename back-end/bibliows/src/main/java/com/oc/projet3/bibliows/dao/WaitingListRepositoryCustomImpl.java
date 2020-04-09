package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.WaitingList;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class WaitingListRepositoryCustomImpl implements WaitingListRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public WaitingList findAllCandidateReservationForLoan(Book book){
        TypedQuery query = em.createQuery("SELECT wl FROM WaitingList wl WHERE wl.canceled = 'false' " +
                "AND wl.retrieved = 'false' AND wl.book = :book ORDER BY wl.reservationDate ASC", WaitingList.class)
                .setParameter("book",  book).setMaxResults(1);

        List wlList;
        wlList = query.getResultList();

        if (wlList != null && ! wlList.isEmpty()){
            return (WaitingList) wlList.get(0);
        }else{
            return null;
        }
    }

}
