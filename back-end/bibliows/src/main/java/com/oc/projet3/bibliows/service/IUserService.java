package com.oc.projet3.bibliows.service;


import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.exceptions.UserAlreadyExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This interface groups all the business operations concerning the users
 * @author
 *
 */
@Component
public interface IUserService {

    /**
     * This method registers a new user in the system
     * @param user
     * @return
     * @throws UserAlreadyExistException
     */
    public Member register(Member user) throws UserAlreadyExistException;

    /**
     * This method find a user by his id
     * @param email
     * @return
     */
    public Optional<Member> getUserByEmail(String email);

    /**
     * This method find all the users containing the email param in his email
     * @param email
     * @param pageRequest
     * @return
     */
    public Page<Member> getAllUserByEmail(String email, Pageable pageRequest);

    /**
     * This method find all the user of the system
     * @param pageRequest
     * @return
     */
    public Page<Member> getAllUser(Pageable pageRequest);

    /**
     * This method find a user by his id
     * @param id
     * @return
     */
    public Optional<Member> getUserById(Long id);

}

