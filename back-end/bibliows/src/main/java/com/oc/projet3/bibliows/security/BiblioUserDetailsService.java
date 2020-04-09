package com.oc.projet3.bibliows.security;

import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BiblioUserDetailsService implements UserDetailsService {

    private final MemberRepository userRepository;

    @Autowired
    public BiblioUserDetailsService(MemberRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Member> user = userRepository.findByEmail(email);
        if (! user.isPresent()) {
            throw new UsernameNotFoundException("login ou mot de passe invalide");
        }

        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                AuthorityUtils.createAuthorityList(user.get().getRole()));

    }
}