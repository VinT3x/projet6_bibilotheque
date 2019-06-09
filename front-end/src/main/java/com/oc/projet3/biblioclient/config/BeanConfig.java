package com.oc.projet3.biblioclient.config;


import com.oc.projet3.biblioclient.service.SOAPConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import sun.net.www.http.HttpClient;

import java.util.Arrays;

@EnableWs
@Configuration
public class BeanConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setContextPath("com.oc.projet3.biblioclient.generated.biblio");
        marshaller.setPackagesToScan("com.oc.projet3.biblioclient.generated.biblio");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        SOAPConnector  client = new SOAPConnector();
        client.setDefaultUri("http://oc.com/projet3/bibliows/anonymous");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    // test
    @Bean
    public WebServiceTemplate createWebServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setCheckConnectionForFault(true);
        webServiceTemplate.afterPropertiesSet();
        return webServiceTemplate;
    }

    private ClientInterceptor createLoggingInterceptor() {
        return new SoapLoggingInterceptor();
    }


}
