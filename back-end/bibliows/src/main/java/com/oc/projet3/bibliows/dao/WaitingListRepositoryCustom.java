package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.WaitingList;

public interface WaitingListRepositoryCustom {
    WaitingList findAllCandidateReservationForLoan(Book book);
}
