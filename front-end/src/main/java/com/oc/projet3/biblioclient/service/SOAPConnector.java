package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import java.io.IOException;

public class SOAPConnector extends WebServiceGatewaySupport{

    public Object callWebService(String url, Object request) throws SoapFaultClientException {

        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }

    public Object callWebService(String url, Object request, User user) throws SoapFaultClientException {

        return getWebServiceTemplate().marshalSendAndReceive(url, request, new SoapRequestHeaderModifier(user));
    }

    public Object unmarshall(Source detailSource){
        try {
            return getWebServiceTemplate().getUnmarshaller().unmarshal(detailSource);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object callWebServicec(String url, Object request){
        JAXBElement res = (JAXBElement) getWebServiceTemplate().marshalSendAndReceive(url, request);
        return res.getValue();
    }

    public CreateAccountResponse callWebServiceCreateAccountRequest(String url, CreateAccountRequest request){
        JAXBElement<CreateAccountResponse> res = (JAXBElement<CreateAccountResponse>) getWebServiceTemplate().marshalSendAndReceive(url, request);
        return res.getValue();
    }
}
