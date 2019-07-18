package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>, JpaSpecificationExecutor<Member> {

    Member getMembersByEmail(String email);

    Optional<Member> getMembersById(long id);

    Optional<Member> findByEmail(String email);

    Page<Member> findByEmailContaining(String email, Pageable pageRequest);
}
