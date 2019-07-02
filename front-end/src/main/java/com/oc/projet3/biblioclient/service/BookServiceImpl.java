package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksRequest;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class BookServiceImpl implements BookService {

    private final
    SOAPConnector soapConnector;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    @Autowired
    public BookServiceImpl(SOAPConnector soapConnector){
        this.soapConnector = soapConnector;
    }

    /**
     * {@link BookService#findBooks(String, String, long, User)}
     *
     * @param title, titre du livre
     * @param fullname, nom prénom de l'auteur
     * @param categoryId, id catégorie du livre
     * @param user, utilisateur courant (connecté)
     * @return
     */
    @Override
    public FindBooksResponse findBooks(String title, String fullname, long categoryId, User user){
        FindBooksRequest findBooksRequest = new FindBooksRequest();
        findBooksRequest.setFullname(fullname);
        findBooksRequest.setTitle(title);

        if (categoryId != 0){
            findBooksRequest.setCategoryId(categoryId);
        }


        return  (FindBooksResponse) soapConnector.callWebService(URI_BIBLIO + "/findBooks", findBooksRequest, user);

    }

}
