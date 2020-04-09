package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;

public interface LendingBookRepositoryCustom {

    /**
     * Récupération de la deadline la plus ancienne, pour savoir quand sera rendu au plus tôt le
     * livre passé en paramètre
     * @param book
     * @return LendingBook, réservation
     */
    LendingBook getFirstByDeadline(Book book);
}
