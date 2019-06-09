package com.oc.projet3.biblioclient.config;

import javax.xml.transform.Source;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.support.interceptor.AbstractValidatingInterceptor;

public class PayloadValidatingInterceptor extends AbstractValidatingInterceptor {

    /**
     * Returns the part of the request message that is to be validated. Default
     *
     * @param request the request message
     * @return the part of the message that is to validated, or {@code null} not to validate anything
     */
    @Override
    protected Source getValidationRequestSource(WebServiceMessage request) {
        return request.getPayloadSource();
    }

    /**
     * Returns the part of the response message that is to be validated.
     *
     * @param response the response message
     * @return the part of the message that is to validated, or {@code null} not to validate anything
     */
    @Override
    protected Source getValidationResponseSource(WebServiceMessage response) {
        return response.getPayloadSource();
    }
}
