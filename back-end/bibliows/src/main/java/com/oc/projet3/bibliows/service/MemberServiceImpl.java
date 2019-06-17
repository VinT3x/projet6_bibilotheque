package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.MemberSpecification;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.exceptions.WSConnectionException;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.security.BiblioUserDetailsService;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
// pour correction, il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class MemberServiceImpl implements MemberService{

    private PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;
    private ServiceStatus serviceStatus = new ServiceStatus();

    private final BiblioUserDetailsService userDetailsService;

    private static Logger logger = LogManager.getLogger("MemberService_logger");

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, BiblioUserDetailsService userDetailsService,PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResponse connection(AuthenticationRequest request) throws WSException {
        AuthenticationResponse response = new AuthenticationResponse();

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        if( passwordEncoder.matches(request.getPassword(),userDetails.getPassword()) ){
            response.setEmail(userDetails.getUsername());
            logger.info("Connexion de " + request.getEmail());
        }
        else {
            logger.error("Tentative de connexion de " + request.getEmail());
            throw new WSConnectionException("Echec de connexion");
        }

        return response;
    }


    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest request) throws WSException {
        CreateAccountResponse response = new CreateAccountResponse();

        // create account only if email doesn't exist
        Optional<Member> memberToFind = memberRepository.findByEmail(request.getEmail());

        if (memberToFind.isPresent())
            throw new WSException("Cet utilisateur existe déjà");

        Member member = new Member();
        BeanUtils.copyProperties(request, member);
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setActive(true);
        member.setRole("USER");
        member = memberRepository.save(member);

        AccountWS memberWS = new AccountWS();
        BeanUtils.copyProperties(member,response);
        logger.info("Account "+ request.getEmail() +" created");

        return response;
    }

    @Override
    public UpdateAccountResponse updateAccount(UpdateAccountRequest request) throws WSException {

        UpdateAccountResponse response = new UpdateAccountResponse();

        Optional<Member> memberToUpdate = memberRepository.getMembersById(request.getId());

        if (! memberToUpdate.isPresent())
            throw new WSException("L'utilisateur n'existe pas");

        Member member = memberToUpdate.get();

        // si modification d'email, vérification si cet email n'existe pas déjà
        if (request.getEmail() != null && (! request.getEmail().equals(member.getEmail())) ) {

            Optional<Member> memberToFind = memberRepository.findByEmail(request.getEmail());
            if (memberToFind.isPresent() && memberToFind.get().getId() != request.getId())
                throw new WSException("Cet email existe déjà");
            member.setEmail(request.getEmail());
        }

        if (request.getPassword() != null)
            member.setPassword(passwordEncoder.encode(request.getPassword()));

        if(request.getFirstname()!= null)
            member.setFirstname(request.getFirstname());

        if(request.getLastname()!= null){
            member.setLastname(request.getLastname());
        }

        memberRepository.save(member);
        MemberWS memberWS = new MemberWS();
        BeanUtils.copyProperties(member, memberWS);
        logger.info("Compte "+ request.getEmail() +" mis à jour");
        response.setMember(memberWS);
        return response;
    }

    @Override
    public DeleteAccountResponse deleteAccount(DeleteAccountRequest request) throws WSException {

        DeleteAccountResponse response = new DeleteAccountResponse();

        Optional<Member> member = memberRepository.getMembersById(request.getId());

        if (! member.isPresent())
            throw new WSException("Ce compte n'existe pas !");

        memberRepository.delete(member.get());
        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Supression du compte");

        response.setServiceStatus(serviceStatus);

        return response;
    }

    @Override
    public FindAccountsResponse findAccount(FindAccountsRequest request){
        FindAccountsResponse response = new FindAccountsResponse();

        Member memberSearch = new Member();

        memberSearch.setId(request.getId());
        memberSearch.setEmail(request.getEmail());
        memberSearch.setLastname(request.getLastname());
        memberSearch.setFirstname(request.getFirstname());

        MemberSpecification memberSpecification = new MemberSpecification(memberSearch);

        List<Member> memberList = memberRepository.findAll(memberSpecification);

        for (Member member: memberList) {
            MemberWS memberWS = new MemberWS();
            BeanUtils.copyProperties(member, memberWS);
            response.getMembers().add(memberWS);
        }

        return response;
    }
}