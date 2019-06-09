package com.oc.projet3.biblioclient;

import com.oc.projet3.biblioclient.service.SOAPConnector;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.soap.client.SoapFaultClientException;

@SpringBootApplication
public class BiblioClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiblioClientApplication.class, args);
    }

    @Bean
    CommandLineRunner lookup(SOAPConnector soapConnector) {
        return args -> {
            CreateAccountRequest request = new CreateAccountRequest();
            request.setFirstname("test");
            request.setEmail("sp@eilmlm.com");
            request.setLastname("laxstname");
            request.setPassword("aaaaa");
            CreateAccountResponse response = null;
            try {
//                CreateAccountResponse response = (CreateAccountResponse) JAXBIntrospector.getValue(soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request));
                  response = (CreateAccountResponse) soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request);
            }catch (Exception e){
               System.out.println(e.getMessage());
            }

            if (response != null){
                System.out.println("Got Response As below ========= : ");
                System.out.println("Name : "+ response.getFirstname());
            }

            CreateAccountRequest request1 = new CreateAccountRequest();
            request1.setFirstname("tre");
            request1.setEmail("sp@eilmlm.com");
            request1.setLastname("laxstname");
            request1.setPassword("aaaaa");
            CreateAccountResponse response1 = null;
            try {
                response1 = (CreateAccountResponse) soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request);
            }catch (SoapFaultClientException e){
                e.printStackTrace();
                System.out.println(e.getFaultStringOrReason());

                System.out.println(e.getMessage());
            }

            if (response1 != null){
                System.out.println("Got Response As below ========= : ");
                System.out.println("Name : "+ response1.getFirstname());
            }

        };
    }
}

