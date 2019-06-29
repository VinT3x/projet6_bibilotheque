package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksResponse;

public interface BookService {
    /**
     * Requête le WS, pour rechercher les livres correpondants aux paramètres
     * @param title, titre du livre
     * @param fullname, nom prénom de l'auteur
     * @param category, cateégorie du livre
     * @param user, utilisateur courant (connecté)
     * @return FindBooksResponse
     */
    FindBooksResponse findBooks(String title, String fullname, String category, User user);
}
