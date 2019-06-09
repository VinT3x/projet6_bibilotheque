package com.oc.projet3.bibliows.dao;


import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    public Book getBookById(Long id);

    @Query("select book from Book book where  upper(book.title) like upper(CONCAT('%', :title, '%'))")
    public List<Book> findBooksByTitle(@Param("title") String title);

    Optional<Book> findBooksByTitleAndAuthor(String title, Author author);

    Integer countBooksByAuthor_Id(Long id);


//    List<Book> findBooksByTitleContainsOrIdAndAuthorFullnameContainsOrderByTitle(String title,Long id, String fullname);
//    List<Book> findBooksByTitleContainsAndIdIs(String title,Long id);
//
//    @Query("from Book b where b.title like '%?1%' and b.id ?2")
//    List<Book> maRequêteAvecQueryDeRechercheParNomEtPrenom(String nom, String prenom);
}
