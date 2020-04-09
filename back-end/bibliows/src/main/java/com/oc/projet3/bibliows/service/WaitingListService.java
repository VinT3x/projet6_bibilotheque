package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

import javax.mail.MessagingException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface WaitingListService {
    AddToWaitingListResponse addToWaitingList(AddToWaitingListRequest request) throws WSException;

    CancelToWaitingListResponse cancelToWaitingList(CancelToWaitingListRequest request) throws WSException, MessagingException;

    RetrieveToWaitingListResponse retrieveToWaitingList(RetrieveToWaitingListRequest request) throws WSException;


    WaitingList findOlderWaitingListActiveByBook(Book book);

    List<WaitingList> findWaitingListCandidateToLendingBook();

    WaitingList persist(WaitingList wl);

    WaitingList toCancel(WaitingList wl);

    Optional<WaitingList> getOnWaitingListActiveByBookIdAndMemberId(Book book, Member member);

    List<WaitingList> findWaitingListToCancel(Calendar cal);

    void setAlertReservation(WaitingList wl);

    FindWaitingListResponse findWaitingListByUser(String email) throws WSException;
}
