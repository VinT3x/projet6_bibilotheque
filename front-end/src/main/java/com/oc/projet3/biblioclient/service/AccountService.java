package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;

public interface AccountService {

    /**
     * Requete le WS pour créer un compte
     * @param request
     * @return CreateAccountResponse
     */
    CreateAccountResponse createAccount(CreateAccountRequest request);
}
