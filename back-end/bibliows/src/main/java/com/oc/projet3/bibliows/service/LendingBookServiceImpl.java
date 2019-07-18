package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import com.oc.projet3.bibliows.dao.LendingBookSpecification;
import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
// pour correction, il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class LendingBookServiceImpl implements LendingBookService {
    private static Logger logger = LogManager.getLogger(LendingBookServiceImpl.class);
    private final LendingBookRepository lendingBookRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    // application.properties get delay day for a book's reservation
    @Value("${delayDay_reserveBook:0}")
    int nb_delayForReservation;
    private ServiceStatus serviceStatus = new ServiceStatus();

    @Autowired
    public LendingBookServiceImpl(LendingBookRepository lendingBookRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.lendingBookRepository = lendingBookRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public LendingBookResponse lendingBook(LendingBookRequest request) throws WSException {

        LendingBookResponse response = new LendingBookResponse();

        Optional<Book> bookToLendOptional = bookRepository.findById(request.getBookId());
        if (!bookToLendOptional.isPresent())
            throw new WSException("Ce livre n'est pas disponible !");

        Optional<Member> memberOptional = memberRepository.findById(request.getAccountId());
        if (!memberOptional.isPresent())
            throw new WSException("Cet utilisateur n'existe pas !");

        if (lendingBookRepository.countReservedBookByBookIdAndMemberId(request.getBookId(), request.getAccountId()) > 0)
            throw new WSException("Vous avez déjà emprunté ce livre !");

        Book bookToLend = bookToLendOptional.get();

        if (bookToLend.getNumberAvailable() == 0)
            throw new WSException("Ce livre n'est pas disponible !");

        LendingBook lendingBook = new LendingBook();
        lendingBook.setBook(bookToLend);
        lendingBook.setMember(memberOptional.get());
        lendingBook.setIscancel(false);

        // Le prêt commence tout de suite
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        lendingBook.setStartdate(calendar);

        // Ajout durée pour un prêt à la date de fin
        Calendar cal = (Calendar) calendar.clone();
        cal.add(GregorianCalendar.DATE, nb_delayForReservation);
        lendingBook.setDeadlinedate(cal);
        lendingBookRepository.save(lendingBook);

        // decrement du nombre de livre disponinle
        bookToLend.setNumberAvailable(bookToLend.getNumberAvailable() - 1);
        bookRepository.save(bookToLend);

        LendingBookWS lendingBookWS = convertLendingBookToLendingBookWS(lendingBook);
        lendingBookWS.setTitle(bookToLend.getTitle());
        lendingBookWS.setIdBook(bookToLend.getId());
        lendingBookWS.setEmail(memberOptional.get().getEmail());
        lendingBookWS.setIdAccount(memberOptional.get().getId());

        logger.info("PRET : " + lendingBook.getId() + ", livre " + bookToLend.getTitle() + ", compte " + memberOptional.get().getEmail());

        response.setLendingBook(lendingBookWS);

        return response;

    }

    @Override
    public ExtendLendingBookResponse extendLendingBook(ExtendLendingBookRequest request) throws WSException {
        ExtendLendingBookResponse response = new ExtendLendingBookResponse();

        Optional<LendingBook> lendingBookOptional = lendingBookRepository.findById(request.getId());
        if ( ! lendingBookOptional.isPresent() )
            throw new WSException("Prêt inexistant !");

        LendingBook lendingBook = lendingBookOptional.get();
        if ( lendingBook.getDeliverydate() != null )
            throw new WSException("Le livre a été rendu, extension impossible !");

        Calendar calStart = lendingBook.getStartdate();
        Calendar calDeadLine = lendingBook.getDeadlinedate();

        // calcul de la difference en jour entre la date de fin de pret et la date de debut de prêt
        long diff = calDeadLine.getTime().getTime() - calStart.getTime().getTime();
        float delay_deadlineDate_startDate = (diff / (1000f * 60f * 60f * 24f));

        // si la date de fin est inférieure à la date de fin initiale + 1 jour (alors on peut ajouter 1 mois)
        if (delay_deadlineDate_startDate > (nb_delayForReservation + 1))
            throw new WSException("Vous ne pouvez étendre la durée du prêt qu'une seule fois !");

        calDeadLine.add(GregorianCalendar.DATE, nb_delayForReservation);
        lendingBook.setDeadlinedate(calDeadLine);
        lendingBookRepository.save(lendingBook);

        response.setDeadLineEndingBook(ConvertUtils.convertCalendarToXMLGregorianCalendar(lendingBook.getDeadlinedate()));
        response.setIdLendingBook(lendingBook.getId());
        logger.info("Extension du prêt " + lendingBook.getId() );

        return response;
    }

    @Override
    public ReturnLendingBookResponse returnLendingBook(ReturnLendingBookRequest request) throws WSException {

        ReturnLendingBookResponse response = new ReturnLendingBookResponse();

        Optional<LendingBook> lendingBookOptional = lendingBookRepository.findById(request.getId());
        if ( ! lendingBookOptional.isPresent() )
            throw new WSException("Prêt inexistant !");

        LendingBook lendingBook = lendingBookOptional.get();

        // update delivery date
        lendingBook.setDeliverydate(Calendar.getInstance());
        lendingBookRepository.save(lendingBook);

        // update number of copies available
        Book book = lendingBook.getBook();
        book.setNumberAvailable(book.getNumberAvailable() + 1);
        bookRepository.save(book);

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Retour du prêt réalisé");

        response.setServiceStatus(serviceStatus);

        logger.info("Retour du prêt " + lendingBook.getId() );

        return response;
    }

    //convert entities entity to ws entity
    private LendingBookWS convertLendingBookToLendingBookWS(LendingBook lendingBook) {

        LendingBookWS lendingBookWS = new LendingBookWS();

        BeanUtils.copyProperties(lendingBook, lendingBookWS);
        //convert Calendar (lendingBook) to XMLGregorianCalendar (lendingBookWS)
        lendingBookWS.setStartdate(ConvertUtils.convertCalendarToXMLGregorianCalendar(lendingBook.getStartdate()));
        lendingBookWS.setDeadlinedate(ConvertUtils.convertCalendarToXMLGregorianCalendar(lendingBook.getDeadlinedate()));
        lendingBookWS.setTitle(lendingBook.getBook().getTitle());
        lendingBookWS.setIdBook(lendingBook.getBook().getId());
        lendingBookWS.setIdAccount(lendingBook.getMember().getId());
        lendingBookWS.setEmail(lendingBook.getMember().getEmail());
        return lendingBookWS;
    }


    @Override
    public FindLendingBookResponse findLendingBook(FindLendingBookRequest request) {

        FindLendingBookResponse response = new FindLendingBookResponse();

        LendingBook lendingBookSearch = new LendingBook();
        lendingBookSearch.setId(request.getIdLending());
        lendingBookSearch.setIscancel(false);
        if (request.isCurrent()){
            lendingBookSearch.setDeliverydate(null);
        }else{
            lendingBookSearch.setDeliverydate(Calendar.getInstance());
        }

        Book bookSearch = new Book();
        bookSearch.setId(request.getIdBook());
        bookSearch.setTitle(request.getTitle());

        lendingBookSearch.setBook(bookSearch);

        Member memberSearch = new Member();
        memberSearch.setEmail(request.getEmail());

        lendingBookSearch.setMember(memberSearch);

        LendingBookSpecification lendingBookSpecification = new LendingBookSpecification(lendingBookSearch);

        List<LendingBook> lendingBookList = lendingBookRepository.findAll(lendingBookSpecification);

        for (LendingBook lendingBook: lendingBookList) {
            LendingBookWS lendingBookWS = convertLendingBookToLendingBookWS(lendingBook);
            response.getLendingBooks().add(lendingBookWS);
        }

        return response;
    }

    @Override
    public CancelLendingBookResponse cancelLendingBook(CancelLendingBookRequest request) throws WSException {
        CancelLendingBookResponse response = new CancelLendingBookResponse();

        Optional<LendingBook> lendingBookOptional = lendingBookRepository.findById(request.getId());
        if ( ! lendingBookOptional.isPresent() )
            throw new WSException("Prêt inexistant !");

        LendingBook lendingBook = lendingBookOptional.get();

        if ( lendingBook.getStartdate().before(Calendar.getInstance()))
            throw new WSException("Vous ne pouvez pas annuler un prêt supérieur à un jour !");

        // update number of copies available
        Book book = lendingBook.getBook();
        book.setNumberAvailable(book.getNumberAvailable() + 1);
        bookRepository.save(book);


        // update boolean cancel
        lendingBook.setIscancel(true);
        lendingBook.setDeliverydate(Calendar.getInstance());
        lendingBookRepository.save(lendingBook);

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Prêt annulé !");

        logger.info("Prêt " + lendingBook.getId() + " annulé.");

        response.setServiceStatus(serviceStatus);

        return response;
    }

}
