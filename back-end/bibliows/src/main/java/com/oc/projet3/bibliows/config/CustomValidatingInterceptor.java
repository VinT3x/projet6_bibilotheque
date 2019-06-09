package com.oc.projet3.bibliows.config;
import javax.xml.transform.Source;

import com.oc.projet3.bibliows.exceptions.CustomSoapValidationException;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.*;
import org.xml.sax.SAXParseException;


public class CustomValidatingInterceptor extends org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor {
    @Override
    protected Source getValidationRequestSource(WebServiceMessage request)
    {
        try {
            Source source = super.getValidationRequestSource(request);
            return source;

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
        return handleResponseValidationErrors(messageContext, errors);
    }

    @Override
    protected boolean handleResponseValidationErrors(MessageContext messageContext,
                                                     SAXParseException[] errors)
    {
        for (SAXParseException error : errors) {
            logger.error("XML validation error on response: " + error.getMessage());
        }
        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault(getFaultStringOrReason(), getFaultStringOrReasonLocale());
            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();
                for (SAXParseException error : errors) {

                    String str = error.getMessage();
                    String[] arrOfStr = str.split(".*'bib:");
                    String[] arrOfStrA = str.split(".*'anon:");

                    if (arrOfStr.length > 1 ){

                        // validator mettre toutes les erreurs
                        throw new CustomSoapValidationException("Validation Erreur: " + arrOfStr[1].replaceFirst("\'",""));
                    }

                    if (arrOfStrA.length > 1 ){

                        // validator mettre toutes les erreurs
                        throw new CustomSoapValidationException("Validation Erreur: " + arrOfStrA[1].replaceFirst("\'",""));
                    }
                }
            }
        }
        return false;
   }
}