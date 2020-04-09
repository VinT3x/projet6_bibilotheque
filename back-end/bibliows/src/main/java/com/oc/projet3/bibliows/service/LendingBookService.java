package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

import javax.mail.MessagingException;

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
    ReturnLendingBookResponse returnLendingBook(ReturnLendingBookRequest request) throws WSException, MessagingException;


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
     * @param idBook, id du livre
     * @param idMember, id du membre
     * @return LendingBook
     */
    LendingBook isAlreadyLentByMember(long idBook, long idMember);

    LendingBook getFirstBookAvailable(Book book);
}
