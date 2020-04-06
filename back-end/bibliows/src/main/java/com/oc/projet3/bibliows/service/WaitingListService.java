package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface WaitingListService {
    AddToWaitingListResponse addToWaitingList(AddToWaitingListRequest request) throws WSException;

    CancelToWaitingListResponse cancelToWaitingList(CancelToWaitingListRequest request) throws WSException;

    RetrieveToWaitingListResponse retrieveToWaitingList(RetrieveToWaitingListRequest request) throws WSException;


    Optional<WaitingList> findOlderWaitingListActiveByBookIdAndMemberId(Long id, Long id1);

    List<WaitingList> findWaitingListCandidateToLendingBook();

    WaitingList persist(WaitingList wl);

    WaitingList cancel(WaitingList wl);

    List<WaitingList> findWaitingListToCancel(Calendar cal);
}
