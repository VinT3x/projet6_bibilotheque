package com.oc.projet3.bibliows.security;

import org.springframework.data.util.CastUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

import javax.annotation.Resource;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.soap.*;

public class FiltreEndpoints {

    // getHeader(String v)
    // extraitle user par decodage
    // test si user OK



    @Resource
    private WebServiceContext context;

    private void getHeaders() throws SOAPException {

//
//        SOAPPart soapPart = message.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//        SOAPHeader header = envelope.getHeader();
//
//        SoapMessage soapMessage = (SoapMessage) webServiceMessageRequest;
//        SoapHeader soapHeader = soapMessage.getSoapHeader();
//
//        MessageContext messageContext = context.getMessageContext();
//
//
//        WebServiceMessage webServiceMessageRequest = messageContext.getRequest();

    }
}
