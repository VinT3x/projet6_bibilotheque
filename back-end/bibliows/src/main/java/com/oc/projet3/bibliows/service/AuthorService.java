package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

/**
 * Auteur WS
 * les actions sur AUTHOR
 */
public interface AuthorService {


    /**
     * Création d'un auteur
     * @param request
     * @return CreateAuthorResponse
     * @throws WSException
     */
    CreateAuthorResponse createAuthor(CreateAuthorRequest request) throws WSException;


    /**
     * Mise à jour d'un auteur
     * @param request
     * @return UpdateAuthorResponse
     * @throws WSException
     */
    UpdateAuthorResponse updateAuthor(UpdateAuthorRequest request) throws WSException;

    /**
     * Suppression d'un auteur
     * @param request
     * @return DeleteAuthorResponse
     * @throws WSException
     */
    DeleteAuthorResponse deleteAuthor(DeleteAuthorRequest request) throws WSException;


    /**
     * Recherche d'auteur
     * @param request
     * @return FindAuthorsResponse
     */
    FindAuthorsResponse findAuthor(FindAuthorsRequest request);
}
