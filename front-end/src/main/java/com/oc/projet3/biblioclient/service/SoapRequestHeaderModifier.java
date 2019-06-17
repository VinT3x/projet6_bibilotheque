package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.MimeHeaders;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Base64;

public class SoapRequestHeaderModifier implements WebServiceMessageCallback {
    private final User user;


    public SoapRequestHeaderModifier(User user) {
        this.user = user;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        if (message instanceof SaajSoapMessage) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) message;
            MimeHeaders mimeHeader = soapMessage.getSaajMessage().getMimeHeaders();
            mimeHeader.setHeader("Authorization", getB64Auth(user.getEmail(), user.getPassword()));
        }
    }

    private String getB64Auth(String login, String pass) {
        String source = login + ":" + pass;
        String retunVal = "Basic " + Base64.getUrlEncoder().encodeToString(source.getBytes());
        return retunVal;
    }
}
