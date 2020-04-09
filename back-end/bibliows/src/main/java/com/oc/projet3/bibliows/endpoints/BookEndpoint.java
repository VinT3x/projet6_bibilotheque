package com.oc.projet3.bibliows.endpoints;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.bibliows.service.BookService;
import com.oc.projet3.gs_ws.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BookEndpoint {

    private final BookService bookService;

    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    @Autowired
    public BookEndpoint(BookService bookService) {
        this.bookService = bookService;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createBookRequest")
    @ResponsePayload
    public CreateBookResponse createBook(@RequestPayload CreateBookRequest request) throws WSException {

        return bookService.createBook(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateBookRequest")
    @ResponsePayload
    public UpdateBookResponse updateBook(@RequestPayload UpdateBookRequest request)
            throws WSException {

        return bookService.updateBook(request);

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteBookRequest")
    @ResponsePayload
    public DeleteBookResponse deleteBook(@RequestPayload DeleteBookRequest request) throws WSException {

        return bookService.deleteBook(request);

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findBooksRequest")
    @ResponsePayload
    public FindBooksResponse FindBooks(@RequestPayload FindBooksRequest request) throws WSNotFoundExceptionException {

        return bookService.findBooks(request);

    }
}
