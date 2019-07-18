package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long>, JpaSpecificationExecutor<Author> {
    int countAuthorByDateOfBirthIsAndFullnameIs(Calendar dateOfBirth, String lastname);

    @Query(value = "select count(*) from books_authors where books_authors.authors_id=:id",nativeQuery = true)
    int countAssociationAuthorBook(@Param("id") Long id);

    Author findAuthorsByFullnameAndDateOfBirth(String fullname, Calendar dateOfBirth);
}
