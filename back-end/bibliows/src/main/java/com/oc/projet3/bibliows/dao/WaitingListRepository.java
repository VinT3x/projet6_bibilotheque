package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.entities.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface WaitingListRepository extends JpaRepository<WaitingList,Long>, WaitingListRepositoryCustom {
    boolean existsWaitingListByBookIdAndMemberIdAndCanceledIsFalseAndRetrievedIsFalse(long bookId, long memberId);

    @Query(value = "select wl from WaitingList wl " +
            "where wl.book=:book and wl.member=:member and wl.retrieved = false and wl.canceled = false")
    Optional<WaitingList> findOnWaitingListActiveByBookAndMember(Book book, Member member);

    List<WaitingList> findAllByAlertDateBeforeAndCanceledIsFalseAndRetrievedIsFalse(Calendar currentDate);

    @Query(value = "select wl from WaitingList wl " +
            "where wl.book =:book and wl.member=:member and wl.retrieved = false and wl.canceled = false")
    Optional<WaitingList> getOnWaitingListActiveByBookAndMember(Book book, Member member);

    @Query(value = "select wl from WaitingList wl " +
            "where wl.member=:member and wl.retrieved = false and wl.canceled = false")
    List<WaitingList> findOnWaitingListActiveByMember(Member member);

    boolean existsWaitingListsByBook(Book book);
}
