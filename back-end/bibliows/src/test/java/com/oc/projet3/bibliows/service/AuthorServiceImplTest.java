package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.AuthorRepository;
import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.exceptions.WSAlreadyExistException;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.gs_ws.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AuthorServiceImplTest {
    @Autowired
    private AuthorService authorService;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private AuthorRepository authorRepository;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private BookRepository bookRepository;

    @Test
    void createAuthorNew() throws DatatypeConfigurationException, WSException {
        CreateAuthorRequest request = new CreateAuthorRequest();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfBirth(cal);
        request.setDateOfDeath(null);
        request.setFullname("Nom Prénom");
        request.setNationality("Française");

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");
        author.setDateOfBirth(cal.toGregorianCalendar());


        Mockito.when(authorRepository.save(any())).thenReturn(author);
        CreateAuthorResponse response = authorService.createAuthor(request);

        Assertions.assertEquals("Nom Prénom",response.getAuthor().getFullname());

    }

    @Test
    void createAuthorAlreadyExist() throws DatatypeConfigurationException, WSException {

        CreateAuthorRequest request = new CreateAuthorRequest();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfBirth(cal);
        request.setDateOfDeath(null);
        request.setFullname("Nom Prénom");
        request.setNationality("Française");

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");
        author.setDateOfBirth(cal.toGregorianCalendar());


        Mockito.when(authorRepository.findAuthorsByFullnameAndDateOfBirth(request.getFullname(),request.getDateOfBirth().toGregorianCalendar())).thenReturn(author);

        assertThrows(WSAlreadyExistException.class, () -> authorService.createAuthor(request));

    }

    @Test
    void updateAuthor() throws DatatypeConfigurationException, WSException {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfBirth(cal);
        request.setDateOfDeath(null);
        request.setFullname("Nom Prénom modifié");

        UpdateAuthorResponse response;

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");
        author.setDateOfBirth(cal.toGregorianCalendar());


        Mockito.when(authorRepository.findById(request.getId())).thenReturn(Optional.of(author));

        Mockito.when(authorRepository.findAuthorsByFullnameAndDateOfBirth(any(),any())).thenReturn(null);

        Mockito.when(authorRepository.save(any())).thenReturn(null);

        response = authorService.updateAuthor(request);

        assertEquals(response.getAuthor().getFullname(), request.getFullname());
        assertEquals(response.getAuthor().getNationality(), author.getNationality());
        assertEquals(response.getAuthor().getDateOfBirth().toGregorianCalendar().getTime(), author.getDateOfBirth().getTime());
    }

    @Test
    void updateAuthorChangeFullnameButAlreadyExist() throws DatatypeConfigurationException {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setDateOfBirth(cal);
        request.setDateOfDeath(null);
        request.setFullname("Nom Prénom modifié");
        request.setNationality("Française modifié");

        Author author = new Author();
        author.setId(111L);
        author.setFullname("Nom Prénom");
        author.setNationality("Française");
        author.setDateOfBirth(cal.toGregorianCalendar());

        Author authorWithFullnameExisting = new Author();
        authorWithFullnameExisting.setId(1L);
        authorWithFullnameExisting.setFullname(request.getFullname());


        Mockito.when(authorRepository.findById(request.getId())).thenReturn(Optional.of(author));

        Mockito.when(authorRepository.findAuthorsByFullnameAndDateOfBirth(any(),any())).thenReturn(authorWithFullnameExisting);

        assertThrows(WSAlreadyExistException.class, () -> authorService.updateAuthor(request));
    }

    @Test
    void updateAuthorNotExist() throws DatatypeConfigurationException, WSException {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar("1990-01-01");
        request.setId(111L);
        request.setDateOfBirth(cal);
        request.setDateOfDeath(null);
        request.setFullname("Nom Prénom");
        request.setNationality("Française");

        Mockito.when(authorRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(WSNotFoundExceptionException.class, () -> authorService.updateAuthor(request));
    }

    @Test
    void deleteAuthor() throws WSException {
        DeleteAuthorRequest request = new DeleteAuthorRequest();
        DeleteAuthorResponse response = new DeleteAuthorResponse();
        request.setId(1L);

        Author author = new Author();
        author.setId(111L);

        Mockito.when(authorRepository.findById(request.getId())).thenReturn(Optional.of(author));
        Mockito.when(bookRepository.countBooksByAuthor_Id(request.getId())).thenReturn(0);
        Mockito.doNothing().when(authorRepository).delete(author);

        response = authorService.deleteAuthor(request);
        assertEquals(response.getServiceStatus().getStatusCode(),"SUCCESS");
    }

    @Test
    void deleteAuthorNotExist() {
        DeleteAuthorRequest request = new DeleteAuthorRequest();

        Mockito.when(authorRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(WSNotFoundExceptionException.class, () -> authorService.deleteAuthor(request));
    }
    
}