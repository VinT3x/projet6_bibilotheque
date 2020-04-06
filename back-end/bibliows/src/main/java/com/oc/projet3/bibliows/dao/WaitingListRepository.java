package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface WaitingListRepository extends JpaRepository<WaitingList,Long> {
    boolean existsWaitingListByBookIdAndMemberIdAndCanceledIsFalseAndRetrievedIsFalse(long bookId, long memberId);

    Optional<WaitingList> findFirstByReservationDateAndCanceledIsFalseAndRetrievedIsFalseOrderByReservationDateAsc(long bookId, long memberId);

    List<WaitingList> findAllByAlertDateBeforeAndCanceledIsFalseAndRetrievedIsFalse(Calendar currentDate);
}

