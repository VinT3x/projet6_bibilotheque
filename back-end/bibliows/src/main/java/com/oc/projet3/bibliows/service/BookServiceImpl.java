package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.AuthorRepository;
import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.BookSpecification;
import com.oc.projet3.bibliows.dao.CategoryRepository;
import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.Category;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.exceptions.*;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * {@link BookService}
 */
@Service
// pour correction, il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private CategoryRepository categoryRepository;
    private ServiceStatus serviceStatus = new ServiceStatus();
    @Autowired
    private LendingBookService lendingBookService;

    private static Logger logger = LogManager.getLogger(BookServiceImpl.class);

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }


    /**
     * conversion de l'entité Book en BookWS
     * @param book
     * @return BookWS
     */
    private BookWS convertBookToBookWS(Book book){
        BookWS bookWS = new BookWS();

        BeanUtils.copyProperties(book, bookWS);
        bookWS.setDateOfficialRelease(ConvertUtils.convertCalendarToXMLGregorianCalendar(book.getDateOfficialRelease()));

        AuthorWS authorWS = new AuthorWS();
        BeanUtils.copyProperties(book.getAuthor(), authorWS);
        authorWS.setDateOfBirth(ConvertUtils.convertCalendarToXMLGregorianCalendar(book.getAuthor().getDateOfBirth()));
        authorWS.setDateOfDeath(ConvertUtils.convertCalendarToXMLGregorianCalendar(book.getAuthor().getDateOfDeath()));
        bookWS.setAuthor(authorWS);
        int nbReserved = book.getNumberOfCopiesForReservation() - book.getNumberReservationAvailable();
        bookWS.setNumberReserved(nbReserved);
        LendingBook lb = lendingBookService.getFirstBookAvailable(book);
        if(lb != null){
            bookWS.setFirstLoanDeadLineDate(ConvertUtils.convertCalendarToXMLGregorianCalendar(lb.getDeadlinedate()));
        }
        bookWS.setNumberLent(lendingBookService.getActiveLendingByBook(book).size());
        bookWS.setNumberAvailableForReservation(book.getNumberOfCopiesForReservation());

        CategoryWS categoryWS = new CategoryWS();
        BeanUtils.copyProperties(book.getCategory(), categoryWS);
        bookWS.setCategory(categoryWS);

        return bookWS;

    }

    /**
     * {@link BookService#createBook(CreateBookRequest)}
     */
    @Override
    public CreateBookResponse createBook(CreateBookRequest request) throws WSException {
        CreateBookResponse response = new CreateBookResponse();

        Optional<Author> author = authorRepository.findById(request.getAuthorId());
        if ( ! author.isPresent())
            throw new WSNotFoundExceptionException("Cet auteur n'est pas présent. Veuillez vous assurez que l'id de l'auteur est correct !");


        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if ( ! category.isPresent())
            throw new WSNotFoundExceptionException("Cette catégorie n'existe pas !");


        Optional<Book> bookOptional = bookRepository.findBooksByTitleAndAuthor(request.getTitle(),author.get());
        if ( bookOptional.isPresent())
            throw new WSAlreadyExistException("Ce livre existe déjà !");


        Book book = new Book();
        // MAPPING
        BeanUtils.copyProperties(request,book);
        book.setAuthor(author.get());
        book.setNumberAvailable(request.getNumberOfCopies());
        book.setNumberReservationAvailable(request.getNumberOfCopies() * 2);
        book.setNumberOfCopiesForReservation(request.getNumberOfCopies() * 2);
        book.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        book.setCategory(category.get());
        book = bookRepository.save(book);
        response.setBook(convertBookToBookWS(book));

        return response;
    }

    /**
     * {@link BookService#updateBook(UpdateBookRequest)}
     */
    @Override
    public UpdateBookResponse updateBook(UpdateBookRequest request) throws WSException {

        UpdateBookResponse response = new UpdateBookResponse();

        Optional<Book> bookToUpdateOptional = bookRepository.findById(request.getId().longValue());

        if ( ! bookToUpdateOptional.isPresent())
            throw new WSNotFoundExceptionException("Ce livre n'a pas été créé !");

        Book bookToUpdate = bookToUpdateOptional.get();
        Author author;
        String title;

        boolean isTitleModified = false;
        boolean isAuthorModified = false;

        if ( request.getAuthorId() != null && request.getAuthorId().longValue() != bookToUpdate.getAuthor().getId()) {
            Optional<Author> authorOptional = authorRepository.findById(request.getAuthorId().longValue());
            if (! authorOptional.isPresent())
                throw new WSNotFoundExceptionException("Cet auteur n'est pas présent en base. Veuillez vous assurez que l'id de l'auteur est correct !");

            author = authorOptional.get();

            isAuthorModified = true;
        }else {
            author = bookToUpdate.getAuthor();
        }

        if ( request.getTitle() != null && !( request.getTitle().equals(bookToUpdate.getTitle()))){
            title = request.getTitle();
            isTitleModified = true;
        }else {
            title = bookToUpdate.getTitle();
        }

        if ( request.getCategoryId() != null ) {
            Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
            if (! categoryOptional.isPresent())
                throw new WSNotFoundExceptionException("Cette catégorie n'existe pas !");

            bookToUpdate.setCategory(categoryOptional.get());
        }

        if ( isTitleModified || isAuthorModified ){
            Optional<Book> bookToFind = bookRepository.findBooksByTitleAndAuthor(title,author);
            if ( bookToFind.isPresent() && (! bookToFind.get().getId().equals(bookToUpdate.getId())))
                throw new  WSAlreadyExistException("Ce livre avec ce titre et cet auteur existe déjà !");

            bookToUpdate.setTitle(title);
            bookToUpdate.setAuthor(author);
        }

        if ( request.getSummary() != null ){
            bookToUpdate.setSummary(request.getSummary());
        }

        if( request.getNumberOfcopies()!=null ){
            int nbBookLending = bookToUpdate.getNumberOfCopies() - bookToUpdate.getNumberAvailable();
            if( request.getNumberOfcopies() < nbBookLending ){
                throw new WSBadNumberException("Le nombre de livre ne peut pas être inférieur au nombre de livre prêté !");
            }

            if( request.getNumberOfcopies() * 2 < bookToUpdate.getNumberReservationAvailable() ){
                throw new WSBadNumberException("Le nombre dépasse 2 fois le nombre de livre !");
            }
            bookToUpdate.setNumberOfCopies(request.getNumberOfcopies());
            bookToUpdate.setNumberReservationAvailable(request.getNumberOfcopies() * 2);
            bookToUpdate.setNumberOfCopiesForReservation(request.getNumberOfcopies() * 2);
            bookToUpdate.setNumberAvailable(request.getNumberOfcopies() - nbBookLending);
        }

        if ( request.getNumberOfPage()!=null ){
            bookToUpdate.setNumberOfPage(request.getNumberOfPage());
        }

        if (request.getDateOfficialRelease()!=null){
            bookToUpdate.setDateOfficialRelease(ConvertUtils.convertXMLGregorianCalendarToCalendar(request.getDateOfficialRelease()));
        }

        bookRepository.save(bookToUpdate);

        response.setBook(convertBookToBookWS(bookToUpdate));

        return response;
    }

    /**
     * {@link BookService#deleteBook(DeleteBookRequest)}
     */
    @Override
    public DeleteBookResponse deleteBook(DeleteBookRequest request) throws WSException {

        DeleteBookResponse response = new  DeleteBookResponse();

        Optional<Book> bookToUpdateOptional = bookRepository.findById(request.getId());

        if ( ! bookToUpdateOptional.isPresent())
            throw new WSNotFoundExceptionException("Ce livre n'a pas été créé !");

        // si aucun prêt en cours
        if(bookToUpdateOptional.get().getNumberAvailable() < bookToUpdateOptional.get().getNumberOfCopies())
            throw new WSLendingInProgressException("Ce livre ne peut pas être supprimé, car un prêt est en cours !");

        bookRepository.delete(bookToUpdateOptional.get());
        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Le livre "+ bookToUpdateOptional.get().getTitle() +" a été supprimé avec succès");

        response.setServiceStatus(serviceStatus);

        return response;
    }

    /**
     * {@link BookService#findBooks(FindBooksRequest)}
     */
    @Override
    public FindBooksResponse findBooks(FindBooksRequest request) throws WSNotFoundExceptionException {
        FindBooksResponse response = new FindBooksResponse();

        Book bookSearch = new Book();
        bookSearch.setId(request.getId());
        bookSearch.setTitle(request.getTitle());

        if ( request.getCategoryId() != null ) {
            Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
            if (! categoryOptional.isPresent())
                throw new WSNotFoundExceptionException("Cette catégorie n'existe pas !");
            bookSearch.setCategory(categoryOptional.get());
        }


        Author authorSearch = new Author();
        authorSearch.setFullname(request.getFullname());

        bookSearch.setAuthor(authorSearch);

        BookSpecification bookSpecification = new BookSpecification(bookSearch);

        List<Book> bookList = bookRepository.findAll(bookSpecification);

        for (Book book: bookList) {
            BookWS bookWS = convertBookToBookWS(book);
            response.getBooks().add(bookWS);
        }

        return response;
    }

    @Override
    public List<Book> findBookAvailableToLoan(){
        return bookRepository.findBooksByNumberAvailableGreaterThan(0);
    }

    /**
     * {@link BookService#findById(long)}
     */
    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book persist(Book book) {
        return bookRepository.save(book);
    }


}
