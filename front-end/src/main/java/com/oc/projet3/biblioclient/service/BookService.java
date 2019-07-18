package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksResponse;

public interface BookService {
    /**
     * Requête le WS, pour rechercher les livres correpondants aux paramètres
     * @param title, titre du livre
     * @param fullname, nom prénom de l'auteur
     * @param categoryId, catégorie du livre
     * @param id, id du livre
     * @param user, utilisateur courant (connecté)
     * @return FindBooksResponse
     */
    FindBooksResponse findBooks(String title, String fullname, Long categoryId, Long id, User user);
}
