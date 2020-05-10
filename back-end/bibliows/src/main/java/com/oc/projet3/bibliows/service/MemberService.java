package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

import java.util.Optional;

public interface MemberService {

    AuthenticationResponse connection(AuthenticationRequest request) throws WSException;

    CreateAccountResponse createAccount(CreateAccountRequest request) throws WSException;

    UpdateAccountResponse updateAccount(UpdateAccountRequest request) throws WSException;

    DeleteAccountResponse deleteAccount(DeleteAccountRequest request) throws WSException;

    FindAccountsResponse findAccount(FindAccountsRequest request);

    Optional<Member> findById(long id);

    Optional<Member> findByEmail(String email);
}
