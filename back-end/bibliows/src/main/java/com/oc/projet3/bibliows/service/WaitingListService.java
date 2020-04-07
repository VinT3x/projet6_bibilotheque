package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface WaitingListService {
    /**
     * ajout sur liste d'attente
     * @param request
     * @return AddToWaitingListResponse
     * @throws WSException
     */
    AddToWaitingListResponse addToWaitingList(AddToWaitingListRequest request) throws WSException;


    /**
     * Annuler réservation sur liste d'attente
     * @param request
     * @return
     * @throws WSException
     */
    CancelToWaitingListResponse cancelToWaitingList(CancelToWaitingListRequest request) throws WSException;

    /**
     * Récupérer un livre, passage de la liste d'attente à la liste de prêt
     * @param request
     * @return RetrieveToWaitingListResponse
     * @throws WSException
     */
    RetrieveToWaitingListResponse retrieveToWaitingList(RetrieveToWaitingListRequest request) throws WSException;

    /**
     * Rechercher la plus ancienne réservation sur liste d'attente
     * @param id
     * @param id1
     * @return
     */
    Optional<WaitingList> findOlderWaitingListActiveByBookIdAndMemberId(Long id, Long id1);

    List<WaitingList> findWaitingListCandidateToLendingBook();

    WaitingList persist(WaitingList wl);

    WaitingList cancel(WaitingList wl);

    /**
     * Liste les réservations sur liste d'attente, candidate à l'annulation
     * @param cal
     * @return List<WaitingList>
     */
    List<WaitingList> findWaitingListToCancel(Calendar cal);
}
