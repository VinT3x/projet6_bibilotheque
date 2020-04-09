package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindCategoriesResponse;

public interface CategoryService {
    /**
     * Requête le WS, pour récupérer tous les objets category
     * @param user, utilisateur courant (connecté)
     * @return FindCategoriesResponse
     */
    FindCategoriesResponse findCategories(User user);
}
