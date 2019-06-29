package com.oc.projet3.bibliows.endpoints;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.service.MemberService;
import com.oc.projet3.gs_ws.AuthenticationRequest;
import com.oc.projet3.gs_ws.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AuthenticationEndpoint {

    private final
    MemberService memberService;

    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    @Autowired
    public AuthenticationEndpoint(MemberService memberService) {
        this.memberService = memberService;
    }

    @PayloadRoot(namespace = "http://oc.com/projet3/bibliows/anonymous", localPart = "authenticationRequest")
    @ResponsePayload
    public AuthenticationResponse authentication(@RequestPayload AuthenticationRequest request) throws WSException {

        return memberService.connection(request);

    }

}
