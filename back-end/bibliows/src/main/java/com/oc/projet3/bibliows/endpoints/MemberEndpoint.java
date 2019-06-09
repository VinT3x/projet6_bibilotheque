package com.oc.projet3.bibliows.endpoints;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.service.MemberService;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class MemberEndpoint {
    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    private MemberService memberService;

    private static Logger logger = LogManager.getLogger(MemberEndpoint.class);

    @Autowired
    public MemberEndpoint(MemberService memberService) {
        this.memberService = memberService;
    }

    @PayloadRoot(namespace = "http://oc.com/projet3/bibliows/anonymous", localPart = "createAccountRequest")
    @ResponsePayload
    public CreateAccountResponse createAccount(@RequestPayload CreateAccountRequest request) throws WSException {
        return memberService.createAccount(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateAccountRequest")
    @ResponsePayload
    public UpdateAccountResponse updateAccount(@RequestPayload UpdateAccountRequest request) throws WSException {

        return memberService.updateAccount(request);

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAccountRequest")
    @ResponsePayload
    public DeleteAccountResponse deleteAccount(@RequestPayload DeleteAccountRequest request) throws WSException {

        return memberService.deleteAccount(request);
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findAccountsRequest")
    @ResponsePayload
    public FindAccountsResponse findAccount(@RequestPayload FindAccountsRequest request) throws WSException {

        return memberService.findAccount(request);
    }
}
