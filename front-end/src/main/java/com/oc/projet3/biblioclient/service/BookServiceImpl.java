package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.Category;
import com.oc.projet3.biblioclient.generated.biblio.ConstantType;
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
     * {@link BookService#findBooks(String, String, String, User)}
     *
     * @param title, titre du livre
     * @param fullname, nom prénom de l'auteur
     * @param category, cateégorie du livre
     * @param user, utilisateur courant (connecté)
     * @return
     */
    @Override
    public FindBooksResponse findBooks(String title, String fullname, String category, User user){
        FindBooksRequest findBooksRequest = new FindBooksRequest();
        findBooksRequest.setFullname(fullname);
        findBooksRequest.setTitle(title);

        if (! category.equals("TOUS")){
            Category categoryWS = new Category();
            categoryWS.setElementCategory(ConstantType.fromValue(category));
            findBooksRequest.setCategory(categoryWS);
        }


        return  (FindBooksResponse) soapConnector.callWebService(URI_BIBLIO + "/findBooks", findBooksRequest, user);

    }

}
