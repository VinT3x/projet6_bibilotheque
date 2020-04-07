package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

public interface LendingBookService {
    /**
     * créer un prêt à partir du endpoint
     * @param request
     * @return LendingBookResponse
     * @throws WSException
     */
    LendingBookResponse addLendingBook(LendingBookRequest request) throws WSException;

    /**
     * créer un prêt
     * @param bookId, id du livre à réserver
     * @param memberId, id du membre qui effectue la réservation
     * @return LendingBookResponse
     * @throws WSException
     */
    LendingBookWS createLendingBook(long bookId, long memberId) throws WSException;

    /**
     * Prolonger la durée d'un prêt
     * @param request
     * @return
     * @throws WSException
     */
    ExtendLendingBookResponse extendLendingBook(ExtendLendingBookRequest request) throws WSException;

    /**
     * Remise à la bibliothèque d'un livre emprunté
     * @param request
     * @return ReturnLendingBookResponse
     * @throws WSException
     */
    ReturnLendingBookResponse returnLendingBook(ReturnLendingBookRequest request) throws WSException;


    /**
     * Recherche d'un prêt
     * @param request
     * @return
     */
    FindLendingBookResponse findLendingBook(FindLendingBookRequest request);

    /**
     * Annuler un prêt
     * @param request
     * @return CancelLendingBookResponse
     * @throws WSException
     */
    CancelLendingBookResponse cancelLendingBook(CancelLendingBookRequest request) throws WSException;

    /**
     * Vérifie si le livre est en cours d'emprunt ou pas par un membre donné
     * @param idBook
     * @param idMember
     * @return boolean
     */
    boolean isAlreadyLentByMember(long idBook, long idMember);
}
