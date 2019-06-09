package com.oc.projet3.biblioclient.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.TransportOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SoapLoggingInterceptor implements ClientInterceptor {

    private static Logger log = LoggerFactory.getLogger(SoapLoggingInterceptor.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        ByteArrayTransportOutputStream os = new ByteArrayTransportOutputStream();
        try {
            messageContext.getRequest().writeTo(os);
        } catch (IOException e) {
            throw new WebServiceIOException(e.getMessage(), e);
        }
        String request = new String(os.toByteArray()).replaceAll("\n", "");
        log.info("Soap request\n----------------------------\n" + request + "\n----------------------------\n");
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        logMessageContext(messageContext);
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        logMessageContext(messageContext);
        return true;
    }

    private void logMessageContext(MessageContext messageContext) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            messageContext.getResponse().writeTo(os);
        } catch (IOException e) {
            throw new WebServiceIOException(e.getMessage(), e);
        }
        String response = new String(os.toByteArray());
        log.info("Soap response\n----------------------------\n" + response + "\n----------------------------\n");
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }

    class ByteArrayTransportOutputStream extends TransportOutputStream {

        private ByteArrayOutputStream outputStream;

        @Override
        public void addHeader(String name, String value) throws IOException {
            createOutputStream();
            String header = name + ": " + value + "\n";
            outputStream.write(header.getBytes());
        }

        public byte[] toByteArray() {
            return outputStream.toByteArray();
        }

        @Override
        protected OutputStream createOutputStream() throws IOException {
            if (outputStream == null) {
                outputStream = new ByteArrayOutputStream();
            }
            return outputStream;
        }
    }
}
