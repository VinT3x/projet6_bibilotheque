package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindCategoriesRequest;
import com.oc.projet3.biblioclient.generated.biblio.FindCategoriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final
    SOAPConnector soapConnector;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    @Autowired
    public CategoryServiceImpl(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    /**
     * Requête le WS, pour récupérer tous les objets category
     *
     * @param user@return FindCategoriesResponse
     */
    @Override
    public FindCategoriesResponse findCategories(User user) {
        FindCategoriesRequest findCategoriesRequest = new FindCategoriesRequest();
        return  (FindCategoriesResponse) soapConnector.callWebService(URI_BIBLIO + "/findBooks", findCategoriesRequest, user);
    }
}
