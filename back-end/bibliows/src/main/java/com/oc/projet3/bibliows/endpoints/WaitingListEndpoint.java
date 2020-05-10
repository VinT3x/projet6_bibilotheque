package com.oc.projet3.bibliows.endpoints;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.service.WaitingListService;
import com.oc.projet3.gs_ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.mail.MessagingException;

@Endpoint
public class WaitingListEndpoint {

    private final WaitingListService waitingListService;

    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    @Autowired
    public WaitingListEndpoint(WaitingListService wlservice) {
        this.waitingListService = wlservice;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addToWaitingListRequest")
    @ResponsePayload
    public AddToWaitingListResponse addWaitingList(@RequestPayload AddToWaitingListRequest request) throws WSException {

        return waitingListService.addToWaitingList(request);

    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelToWaitingListRequest")
    @ResponsePayload
    public CancelToWaitingListResponse cancelToWaitingList(@RequestPayload CancelToWaitingListRequest request) throws WSException, MessagingException {

        return waitingListService.cancelToWaitingList(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "retrieveToWaitingListRequest")
    @ResponsePayload
    public RetrieveToWaitingListResponse retrieveToWaitingList(@RequestPayload RetrieveToWaitingListRequest request) throws WSException {

        return waitingListService.retrieveToWaitingList(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findWaitingListRequest")
    @ResponsePayload
    public FindWaitingListResponse findWaitingList(@RequestPayload FindWaitingListRequest request) throws WSException {

        return waitingListService.findWaitingListByUser(request.getEmail());
    }

}
