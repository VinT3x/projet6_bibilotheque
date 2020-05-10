package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.AuthorRepository;
import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.CategoryRepository;
import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.Category;
import com.oc.projet3.bibliows.exceptions.*;
import com.oc.projet3.gs_ws.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class BookServiceImplTest {
    @Autowired
    private BookService bookService;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private AuthorRepository authorRepository;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private BookRepository bookRepository;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private CategoryRepository categoryRepository;

    @Test
    void createBookWithAuthorNotExist() {
        //GIVEN
        CreateBookRequest request = new CreateBookRequest();
        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        //WHEN
        Mockito.when(authorRepository.findById(request.getAuthorId())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> bookService.createBook(request));
    }

    @Test
    void createBookWithExistingBook() {
        //GIVEN
        CreateBookRequest request = new CreateBookRequest();
        request.setCategoryId(12L);
        request.setTitle("titre livre");

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(1L);

        //WHEN
        Mockito.when(authorRepository.findById(request.getAuthorId())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(request.getTitle(), author)).thenReturn(Optional.of(book));

        //THEN
        assertThrows(WSAlreadyExistException.class, () -> bookService.createBook(request));
    }

    @Test
    void createBook() throws WSException, DatatypeConfigurationException {
        //GIVEN
        CreateBookRequest request = new CreateBookRequest();
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(111L);
        request.setNumberOfCopies(111);
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setNumberAvailable(request.getNumberOfCopies());
        book.setNumberReservationAvailable(request.getNumberOfCopies() * 2);
        book.setNumberOfCopiesForReservation(request.getNumberOfCopies() * 2);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);


        //WHEN
        Mockito.when(authorRepository.findById(request.getAuthorId())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(request.getTitle(), author)).thenReturn(Optional.empty());
        Mockito.when(bookRepository.save(any())).thenReturn(book);

        //THEN
        CreateBookResponse response = bookService.createBook(request);
        assertEquals(response.getBook().getTitle(), request.getTitle());
        assertEquals(response.getBook().getId(), 1L);
    }

    @Test
    void updateBookWithBookNotExist() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);

        //WHEN
        Mockito.when(authorRepository.findById(request.getId().longValue())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> bookService.updateBook(request));
    }

    @Test
    void updateBookWithAuthorIdNotExist() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);

        //WHEN
        Mockito.when(authorRepository.findById(request.getId().longValue())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> bookService.updateBook(request));
    }

    @Test
    void updateBookWithCategoryIdNotExist() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);


        //WHEN
        Mockito.when(authorRepository.findById(request.getId().longValue())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> bookService.updateBook(request));
    }


    @Test
    void updateBookWithBookAlreadyExist() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(2L);
        book.setTitle(request.getTitle() + "modif");
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);

        Book bookToFind = new Book();
        bookToFind.setId(3L);


        //WHEN
        Mockito.when(bookRepository.findById(request.getId().longValue())).thenReturn(Optional.of(book));
        Mockito.when(authorRepository.findById(request.getAuthorId().longValue())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(any(),any())).thenReturn(Optional.of(bookToFind));

        //THEN
        assertThrows(WSAlreadyExistException.class, () -> bookService.updateBook(request));
    }

    @Test
    void updateBookWithBadNumberOfCopy() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);
        request.setNumberOfcopies(5);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(2L);
        book.setTitle(request.getTitle() + "modif");
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);
        book.setNumberOfCopies(45);
        book.setNumberAvailable(4);

        Book bookToFind = new Book();
        bookToFind.setId(2L);


        //WHEN
        Mockito.when(bookRepository.findById(request.getId().longValue())).thenReturn(Optional.of(book));
        Mockito.when(authorRepository.findById(request.getAuthorId().longValue())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(any(),any())).thenReturn(Optional.of(bookToFind));

        //THEN
        assertThrows(WSBadNumberException.class, () -> bookService.updateBook(request));
    }

    @Test
    void updateBookWithBadNumberOfCopyForReservation() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);
        request.setNumberOfcopies(50);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(2L);
        book.setTitle(request.getTitle() + "modif");
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);
        book.setNumberOfCopies(45);
        book.setNumberAvailable(4);
        book.setNumberReservationAvailable(110);

        Book bookToFind = new Book();
        bookToFind.setId(2L);


        //WHEN
        Mockito.when(bookRepository.findById(request.getId().longValue())).thenReturn(Optional.of(book));
        Mockito.when(authorRepository.findById(request.getAuthorId().longValue())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(any(),any())).thenReturn(Optional.of(bookToFind));

        //THEN
        assertThrows(WSBadNumberException.class, () -> bookService.updateBook(request));
    }

    @Test
    void updateBook() throws DatatypeConfigurationException, WSException {
        //GIVEN
        UpdateBookResponse response = new UpdateBookResponse();
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(BigInteger.valueOf(1));
        request.setCategoryId(12L);
        request.setTitle("titre livre modifié");
        request.setAuthorId(BigInteger.valueOf(111));
        request.setSummary("Résumé");
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfficialRelease(cal);
        request.setNumberOfcopies(50);

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");

        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        book.setId(2L);
        book.setTitle(request.getTitle() + "modif");
        book.setAuthor(author);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category);
        book.setNumberOfCopies(45);
        book.setNumberAvailable(4);
        book.setNumberReservationAvailable(90);

        Book bookToFind = new Book();
        bookToFind.setId(2L);


        //WHEN
        Mockito.when(bookRepository.findById(request.getId().longValue())).thenReturn(Optional.of(book));
        Mockito.when(authorRepository.findById(request.getAuthorId().longValue())).thenReturn(Optional.of(author));
        Mockito.when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findBooksByTitleAndAuthor(any(),any())).thenReturn(Optional.of(bookToFind));
        Mockito.when(bookRepository.save(any())).thenReturn(null);

        //THEN
        response = bookService.updateBook(request);
        assertEquals(request.getTitle(), response.getBook().getTitle());
        assertEquals(request.getNumberOfcopies().intValue(), response.getBook().getNumberOfCopies());
    }


    @Test
    void deleteBookWithBookNotExist() {
        //GIVEN
        DeleteBookRequest request = new DeleteBookRequest();
        request.setId(1L);

        //WHEN
        Mockito.when(bookRepository.findById(request.getId())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class,() -> bookService.deleteBook(request));
    }

    @Test
    void deleteBookWithLendingInProgress() {
        //GIVEN
        DeleteBookRequest request = new DeleteBookRequest();
        request.setId(1L);
        Book book = new Book();
        book.setNumberOfCopies(15);
        book.setNumberAvailable(12);

        //WHEN
        Mockito.when(bookRepository.findById(request.getId())).thenReturn(Optional.of(book));

        //THEN
        assertThrows(WSLendingInProgressException.class,() -> bookService.deleteBook(request));
    }

    @Test
    void deleteBook() throws WSException {
        //GIVEN
        DeleteBookResponse response = new DeleteBookResponse();
        DeleteBookRequest request = new DeleteBookRequest();
        request.setId(1L);
        Book book = new Book();
        book.setNumberOfCopies(15);
        book.setNumberAvailable(15);

        //WHEN
        Mockito.when(bookRepository.findById(request.getId())).thenReturn(Optional.of(book));

        //THEN
        response = bookService.deleteBook(request);
        assertEquals(response.getServiceStatus().getStatusCode(),"SUCCESS");
    }
}