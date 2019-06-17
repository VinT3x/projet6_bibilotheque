package com.oc.projet3.bibliows.config.validation;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.*;
import org.xml.sax.SAXParseException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

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
        Source source = messageContext.getRequest().getPayloadSource();
        DOMSource domSource = (DOMSource) source;
        String prefix = domSource.getNode().getPrefix();
        String namespaceURI = domSource.getNode().getNamespaceURI();
        String type = namespaceURI.substring(namespaceURI.lastIndexOf("/") + 1);
        String action = domSource.getNode().getLocalName();

        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault("Erreur de validation", getFaultStringOrReasonLocale());

            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();
                ValidationError validationError = new ValidationError("/validationsMsg.properties");
                for (SAXParseException error : errors) {
                    logger.warn("XML validation error on request: " + error.getMessage());
                    // suppression de la partie du gauche juste avant le nom du champ
                    String[] arrOfStr = error.getMessage().split(".*'" + prefix + ":");
                    String msg = "";
                    if (arrOfStr.length > 1) {
                        // recuperation du nom du champ en supprimant la partie droite
                        String field = arrOfStr[1].replaceFirst("' n'est pas valide.", "");
                        // récupréation du message paramétré
                        msg = validationError.getErrorMsg(type + "." + action + "." + field);
                    }
                    if (msg != ""){
                        SoapFaultDetailElement detailElement = detail.addFaultDetailElement(getDetailElementName());
                        detailElement.addText(msg);
                    }
                }
            }
        }
        return false;
    }
}

//                    }else if (arrOfStrBib.length >1){
//                        String champ = arrOfStrBib[1].replaceFirst("' n'est pas valide.","");
//                        if (champ.equals("fullname")){
//                            msg = "Veuillez compléter le champ nom";
//                        }

