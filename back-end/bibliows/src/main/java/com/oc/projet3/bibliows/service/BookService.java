package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.gs_ws.*;

import java.util.List;
import java.util.Optional;

/**
 * Livre WS
 * les actions sur BOOK
 */
public interface BookService {

    /**
     *  créer un livre
     * @param request
     * @return CreateBookResponse
     * @throws WSException
     */
    CreateBookResponse createBook(CreateBookRequest request) throws WSException;

    /**
     *  mise à jour d'un livre
     * @param request
     * @return UpdateBookResponse
     * @throws WSException
     */
    UpdateBookResponse updateBook(UpdateBookRequest request) throws WSException;

    /**
     *  supprimer un livre
     * @param request
     * @return DeleteBookResponse
     * @throws WSException
     */
    DeleteBookResponse deleteBook(DeleteBookRequest request) throws WSException;

    /**
     *  rechercher des livres
     * @param request
     * @return FindBooksResponse
     */
    FindBooksResponse findBooks(FindBooksRequest request) throws WSNotFoundExceptionException;

    List<Book> findBookAvailableToLoan();

    /**
     *  récupérer un livre par son id
     * @param id
     * @return Book
     */
    Optional<Book> findById(long id);

    Book persist(Book book);
}
