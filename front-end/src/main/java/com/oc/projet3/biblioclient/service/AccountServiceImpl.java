package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    final
    SOAPConnector soapConnector;

    @Autowired
    public AccountServiceImpl(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest request) {

        CreateAccountResponse response = null;
        try {
            response = (CreateAccountResponse) soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }
}
