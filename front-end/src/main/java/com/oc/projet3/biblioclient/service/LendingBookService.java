package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindLendingBookResponse;
import org.springframework.stereotype.Service;

@Service
public interface LendingBookService {
    /**
     * Recherche sur le WS des prêts de l'utilisateur donné en paramètre.
     * Le parametre isCurrent, permet de filtrer uniqument les prêts en cours.
     * @param isCurrent, prêt en cours (true) ou tous (false)
     * @param user, utilisateur emprumpteur
     * @return FindLendingBookResponse
     */
    FindLendingBookResponse findBook(boolean isCurrent, User user);

    /**
     * Requête le WS pour emprunter un livre.
     *
     * @param id, id du livre
     * @param user, utilisateur emprumpteur
     */
    void loanBook(long id, User user);

    void loanBookExtend(long id, User user);
}
