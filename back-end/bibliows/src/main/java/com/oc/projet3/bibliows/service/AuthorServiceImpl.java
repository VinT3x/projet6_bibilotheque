package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.AuthorRepository;
import com.oc.projet3.bibliows.dao.AuthorSpecification;
import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.exceptions.WSAlreadyExistException;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;


/**
 * {@link AuthorService}
 */
@Service
// pour correction, il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class AuthorServiceImpl implements AuthorService {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private ServiceStatus serviceStatus = new ServiceStatus();

    private static Logger logger = LogManager.getLogger(AuthorServiceImpl.class);


    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Conversion de l'entité Author en AuthorWS
     * @param author
     * @return AuthorWS
     */
    private AuthorWS authorToAuthorWS(Author author){

        AuthorWS authorWS = new AuthorWS();
        BeanUtils.copyProperties(author, authorWS);
        authorWS.setDateOfBirth(ConvertUtils.convertCalendarToXMLGregorianCalendar(author.getDateOfBirth()));
        authorWS.setDateOfDeath(ConvertUtils.convertCalendarToXMLGregorianCalendar(author.getDateOfDeath()));

        return authorWS;
    }


    /**
     * {@link AuthorService#createAuthor}
     */
    @Override
    public CreateAuthorResponse createAuthor(CreateAuthorRequest request) throws WSException {

        CreateAuthorResponse response = new CreateAuthorResponse();

        Calendar dateOfBirth = request.getDateOfBirth().toGregorianCalendar();

        if (authorRepository.findAuthorsByFullnameAndDateOfBirth(request.getFullname(), dateOfBirth) != null)
            throw new WSAlreadyExistException("Cet auteur existe déjà !");

        Calendar dateOfDeath = null;
        if(request.getDateOfDeath()!= null){
            dateOfDeath=request.getDateOfDeath().toGregorianCalendar();
        }

        Author author = new Author(
                request.getFullname(),
                dateOfBirth,
                dateOfDeath,
                request.getNationality()
        );

        authorRepository.save(author);

        // convertion en AuthorWS
        response.setAuthor(authorToAuthorWS(author));

        return response;
    }

    /**
     * {@link AuthorService#updateAuthor}
     */
    @Override
    public UpdateAuthorResponse updateAuthor(UpdateAuthorRequest request) throws WSException {

        UpdateAuthorResponse response = new UpdateAuthorResponse();

        Optional<Author> authorToUpdate = authorRepository.findById(request.getId());

        if (!authorToUpdate.isPresent())
            throw new WSNotFoundExceptionException("Cet auteur n'est pas présent !");

        Author author = authorToUpdate.get();

        String fullname;

        if (request.getFullname() != null) {
            fullname = request.getFullname();
        } else {
            fullname = author.getFullname();
        }

        Calendar dateOfBirth;
        if (request.getDateOfBirth() != null) {
            dateOfBirth = ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfBirth());
         } else {
            dateOfBirth = author.getDateOfBirth();
        }

        Author authorToFind = authorRepository.findAuthorsByFullnameAndDateOfBirth(fullname, dateOfBirth);


        if (authorToFind != null && (! authorToFind.getId().equals(author.getId())) )
            throw new WSAlreadyExistException("L'auteur " + request.getFullname() + " existe déjà !");

        author.setFullname(fullname);
        author.setDateOfBirth(dateOfBirth);

        if (request.getDateOfDeath() != null) {
            author.setDateOfDeath(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfDeath()));
        }

        if (request.getNationality() != null) {
            author.setNationality(request.getNationality());
        }

        authorRepository.save(author);

        // convertion en AuthorWS
        response.setAuthor(authorToAuthorWS(author));

        return response;
    }

    /**
     * {@link AuthorService#deleteAuthor}
     */
    @Override
    public DeleteAuthorResponse deleteAuthor(DeleteAuthorRequest request) throws WSException {
        DeleteAuthorResponse response = new DeleteAuthorResponse();

        Optional<Author> authorToFind = authorRepository.findById(request.getId());

        if (! authorToFind.isPresent())
            throw new WSNotFoundExceptionException("L'auteur n'existe pas !");

        if (bookRepository.countBooksByAuthor_Id(request.getId()) == 0){
            authorRepository.delete(authorToFind.get());
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Author deleted Successfully");
        }else{
            serviceStatus.setStatusCode("FAILED");
            serviceStatus.setMessage("Author associated with book");
        }

        response.setServiceStatus(serviceStatus);

        return response;
    }

    /**
     * {@link AuthorService#findAuthor}
     */
    @Override
    public FindAuthorsResponse findAuthor(FindAuthorsRequest request) {
        FindAuthorsResponse response = new FindAuthorsResponse();

        Author authorSearch = new Author();

        authorSearch.setId(request.getId());
        authorSearch.setFullname(request.getFullname());
        authorSearch.setNationality(request.getNationality());
        authorSearch.setDateOfBirth(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfBirth()));
        authorSearch.setDateOfDeath(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfDeath()));

        AuthorSpecification authorSpecification = new AuthorSpecification(authorSearch);

        List<Author> authorList = authorRepository.findAll(authorSpecification);

        for (Author author: authorList) {
            response.getAuthors().add(authorToAuthorWS(author));
        }

        return response;
    }

}
