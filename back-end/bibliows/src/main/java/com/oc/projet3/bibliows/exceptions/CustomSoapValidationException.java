package com.oc.projet3.bibliows.exceptions;


import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class CustomSoapValidationException extends RuntimeException {
    public CustomSoapValidationException(String s) {
        super(s);
    }
}
