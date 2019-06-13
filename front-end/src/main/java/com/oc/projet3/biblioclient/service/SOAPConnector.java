package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.Detail;
import javax.xml.transform.Source;
import java.io.IOException;

public class SOAPConnector extends WebServiceGatewaySupport{

    public Object callWebService(String url, Object request) throws SoapFaultClientException {
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
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
