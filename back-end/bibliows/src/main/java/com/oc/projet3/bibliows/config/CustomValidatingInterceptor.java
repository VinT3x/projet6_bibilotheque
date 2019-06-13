package com.oc.projet3.bibliows.config;


import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.*;
import org.xml.sax.SAXParseException;
import javax.xml.transform.Source;


public class CustomValidatingInterceptor extends org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor {
    @Override
    protected Source getValidationRequestSource(WebServiceMessage request)
    {
        try {
            return super.getValidationRequestSource(request);

        } catch (RuntimeException  e) {
            logger.error( "####### Runtime ####################");
            throw e;
        }
    }
    @Override
    protected Source getValidationResponseSource(WebServiceMessage response) {
        return super.getValidationResponseSource(response);
    }

    @Override
    protected boolean handleRequestValidationErrors(MessageContext messageContext,
                                                    SAXParseException[] errors)
    {
        for (SAXParseException error : errors) {
            logger.warn("XML validation error on request: " + error.getMessage());
        }
        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault("esrr", getFaultStringOrReasonLocale());
            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();
                for (SAXParseException error : errors) {
                    String str = error.getMessage();
                    String[] arrOfStr = str.split(".*'ns2:");
                    String[] arrOfStrBib = str.split(".*'bib:");
                    String msg = "";
                    if (arrOfStr.length > 1 ){
                        String champ = arrOfStr[1].replaceFirst("' n'est pas valide.","");
                        if (champ.equals("password")){
                            msg = "Votre mot de passe doit contenir au moins 5 caractères";
                        }else if (champ.equals("lastname")){
                            msg = "Veuillez compléter votre nom de famille";
                        }else if (champ.equals("firstname")){
                            msg = "Veuillez compléter votre prénom";
                        }else if (champ.equals("email")){
                            msg = "Email invalide";
                        }
                    }else if (arrOfStrBib.length >1){
                        String champ = arrOfStrBib[1].replaceFirst("' n'est pas valide.","");
                        if (champ.equals("fullname")){
                            msg = "Veuillez compléter le champ nom";
                        }
                    }
                    SoapFaultDetailElement detailElement = detail.addFaultDetailElement(getDetailElementName());
                    detailElement.addText(msg);
                }
            }
        }
        return false;
    }


}