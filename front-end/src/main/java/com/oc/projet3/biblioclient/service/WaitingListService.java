package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindWaitingListResponse;

public interface WaitingListService {
    void addToWaitingList(long id, User user);

    void cancelReservation(long id, User user);

    FindWaitingListResponse findReservation(User user);
}
