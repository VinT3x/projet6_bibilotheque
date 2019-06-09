package com.oc.projet3.bibliows.service;


import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.exceptions.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This is the implementation for IUserService
 * @author 
 *
 */
@Service
public class UserServiceImpl implements IUserService{
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private MemberRepository memberRepository;
	

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	// pour correction, il faut spécifier quel transaction manager utiliser  :
	// No qualifying bean of type
	// 'org.springframework.transaction.PlatformTransactionManager'
	// available: expected single matching bean but found 2:
	// transactionManager,resourcelessTransactionManager
	@Transactional("transactionManager")
	public Member register(Member user) throws UserAlreadyExistException {
		
		logger.info("Find the user: if it already exists a user with the email, throw the exception");
		Optional<Member> opUser=memberRepository.findByEmail(user.getEmail());
		if(opUser.isPresent())
			throw new UserAlreadyExistException("There is already a user with this email address");

		logger.info("Create the user in the database");
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return memberRepository.save(user);
	}

	@Override
	public Optional<Member> getUserByEmail(String email) {
		return memberRepository.findByEmail(email);
	}
	
	@Override
	public Optional<Member> getUserById(Long id) {
		return memberRepository.getMembersById(id);
	}

	@Override
	public Page<Member> getAllUserByEmail(String email, Pageable pageRequest) {
		return memberRepository.findByEmailContaining(email, pageRequest);
	}

	@Override
	public Page<Member> getAllUser(Pageable pageRequest) {
		return memberRepository.findAll(pageRequest);
	}

}
