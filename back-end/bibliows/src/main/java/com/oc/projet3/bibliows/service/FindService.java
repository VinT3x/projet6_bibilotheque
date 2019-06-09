package com.oc.projet3.bibliows.service;


import com.oc.projet3.bibliows.entities.Book;

import com.oc.projet3.bibliows.dao.AuthorRepository;
import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FindService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LendingBookRepository lendingBookRepository;



    @Autowired
    FindService(AuthorRepository authorRepository, BookRepository bookRepository, MemberRepository memberRepository, LendingBookRepository lendingBookRepository) {

        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.lendingBookRepository = lendingBookRepository;
    }

    private List<Book> findBook(String title) {
        Book book = new Book();
        book.setTitle(title);
//        book.setAuthor();

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("numberOfCopies")
                .withIgnorePaths("numberAvailable");

        Example<Book> example = Example.of(book, matcher);

        return bookRepository.findAll(example);


    }

//    private List<Author> findAuthor(String firstname, String lastname) {
//
//        Author author = new Author();
//        author.setLastname(lastname);
//        author.setFirstname(firstname);
//
//        ExampleMatcher matcher = ExampleMatcher.matchingAll()
//                .withMatcher("lastname", contains().ignoreCase())
//                .withMatcher("firstname", contains().ignoreCase());
//
//        Example<Author> exampleA = Example.of(author, matcher);
//        return authorRepository.findAll(exampleA);
//    }

//    protected List<Book> findBook_bookAndAuthor(String title, String fullname) {
//
//        List<Book> bookList = new ArrayList<>();
//        Book book = new Book();
//        book.setTitle(title);
//        Author author = authorRepository.
//        book.setAuthor();
//
//        ExampleMatcher matcher = ExampleMatcher.matchingAll()
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                .withIgnoreCase()
//                .withIgnorePaths("numberOfCopies")
//                .withIgnorePaths("numberAvailable");
//
//        Example<Book> example = Example.of(book, matcher);
//
//        return bookRepository.findAll(example);
//
//        return bookList;
//    }


//
//    protected List<ReserveBook> findReserveBook(String email, boolean iscurrent) {
//        ReserveBook reserveBook = new ReserveBook();
//        Optional<Member> member = memberRepository.findByEmail(email);
//
//        if (! member.isPresent()){
//            throw new DbNotFoundException("Member with email " + email + " not found.");
//        }
//
//        reserveBook.setMember(member.get());
//
//        if(iscurrent){
//            reserveBook.setDeliverydate(null);
//        }
//
//        ExampleMatcher matcher = ExampleMatcher.matchingAll()
//                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
//                .withMatcher("deliveryDate", exact())
//                .withIgnoreCase();
//
//        Example<ReserveBook> example = Example.of(reserveBook, matcher);
//
//        return lendingBookRepository.findAll(example, new Sort(Sort.Direction.ASC, "deliverydate"));
//    }
}
