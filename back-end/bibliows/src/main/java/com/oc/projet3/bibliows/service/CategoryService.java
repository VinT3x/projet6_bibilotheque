package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.gs_ws.*;

/**
 * Category WS
 * les actions sur Category
 */
public interface CategoryService {


    /**
     *  créer une categorie
     * @param request, nom de la categorie
     * @return CreateCategoryResponse
     * @throws WSException
     */
    CreateCategoryResponse createCategory(CreateCategoryRequest request) throws WSException;

    /**
     *  mise à jour d'une categorie
     * @param request, id et nom de la categorie
     * @return UpdateCategoryResponse
     * @throws WSException
     */
    UpdateCategoryResponse updateCategory(UpdateCategoryRequest request) throws WSException;


    /**
     *  rechercher des categories
     * @param request, id ou/et nom de la categorie
     * @return FindCategoriesResponse
     */
    FindCategoriesResponse findCategories(FindCategoriesRequest request) throws WSNotFoundExceptionException;
}
