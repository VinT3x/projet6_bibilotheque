package com.oc.projet3.bibliows.endpoints;


import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.service.LendingBookService;
import com.oc.projet3.gs_ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class LendingBookEndpoint {

    private final LendingBookService lendingBookService;

    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    @Autowired
    public LendingBookEndpoint(LendingBookService lendingBookService) {
        this.lendingBookService = lendingBookService;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "lendingBookRequest")
    @ResponsePayload
    public LendingBookResponse lendingBook(@RequestPayload LendingBookRequest request) throws WSException {

        return lendingBookService.lendingBook(request);

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "returnLendingBookRequest")
    @ResponsePayload
    public ReturnLendingBookResponse returnLendingBook(@RequestPayload ReturnLendingBookRequest request) throws WSException {

        return lendingBookService.returnLendingBook(request);
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "extendLendingBookRequest")
    @ResponsePayload
    public ExtendLendingBookResponse extendLendingBook(@RequestPayload ExtendLendingBookRequest request) throws WSException{

        return lendingBookService.extendLendingBook(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findLendingBookRequest")
    @ResponsePayload
    public FindLendingBookResponse findLendingBook(@RequestPayload FindLendingBookRequest request) {

        return lendingBookService.findLendingBook(request);
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelLendingBookRequest")
    @ResponsePayload
    public CancelLendingBookResponse cancelLendingBook(@RequestPayload CancelLendingBookRequest request) throws WSException {

        return lendingBookService.cancelLendingBook(request);
    }
}
