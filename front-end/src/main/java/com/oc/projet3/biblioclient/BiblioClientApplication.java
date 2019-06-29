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

}

