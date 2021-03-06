package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import com.oc.projet3.bibliows.dao.LendingBookSpecification;
import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
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

    @Autowired
    LendingBookRepository lendingBookRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WaitingListService waitingListService;
    @Autowired
    EmailService emailService;

    // application.properties get delay day for a book's reservation
    @Value("${delayDay_reserveBook:0}")
    int delayForReservation;

    private ServiceStatus serviceStatus = new ServiceStatus();


    @Override
    public LendingBookResponse addLendingBook(LendingBookRequest request) throws WSException {

        LendingBookResponse response = new LendingBookResponse();

        LendingBookWS lendingBookWS = createLendingBook(request.getBookId(), request.getAccountId());

        logger.info("PRET : {}, livre {}, compte {}", lendingBookWS.getId(), lendingBookWS.getTitle(), lendingBookWS.getEmail());

        response.setLendingBook(lendingBookWS);

        return response;

    }

    @Override
    public LendingBookWS createLendingBook(long bookId, long memberId) throws WSException {
        Book bookToLend = bookRepository.findById(bookId)
                .orElseThrow(() -> new WSException("Ce livre n'est pas disponible !"));

        Member member = memberRepository.findById(memberId).
                orElseThrow(() -> new WSException("Cet utilisateur n'existe pas !"));

        if (lendingBookRepository.countReservedBookByBookIdAndMemberId(bookId, memberId) > 0)
            throw new WSException("Vous avez déjà emprunté ce livre !");

        if (waitingListService.getOnWaitingListActiveByBookIdAndMemberId(bookToLend, member).isPresent()) {
            throw new WSException("Vous êtes sur liste d'attente !");
        }

        if (bookToLend.getNumberAvailable() == 0)
            throw new WSException("Ce livre n'est pas disponible !");

        LendingBook lendingBook = new LendingBook();
        lendingBook.setBook(bookToLend);
        lendingBook.setMember(member);
        lendingBook.setCanceled(false);

        // Le prêt commence tout de suite
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        lendingBook.setStartdate(calendar);

        // Ajout durée pour un prêt à la date de fin
        Calendar cal = (Calendar) calendar.clone();
        cal.add(GregorianCalendar.DATE, delayForReservation);
        lendingBook.setDeadlinedate(cal);
        lendingBook = lendingBookRepository.save(lendingBook);

        // decrement du nombre de livre disponinle
        bookToLend.setNumberAvailable(bookToLend.getNumberAvailable() - 1);
        bookRepository.save(bookToLend);

        LendingBookWS lendingBookWS = convertLendingBookToLendingBookWS(lendingBook);
        lendingBookWS.setTitle(lendingBook.getBook().getTitle());
        lendingBookWS.setIdBook(lendingBook.getBook().getId());
        lendingBookWS.setEmail(lendingBook.getMember().getEmail());
        lendingBookWS.setIdAccount(lendingBook.getMember().getId());

        return lendingBookWS;
    }

    @Override
    public ExtendLendingBookResponse extendLendingBook(ExtendLendingBookRequest request) throws WSException {
        ExtendLendingBookResponse response = new ExtendLendingBookResponse();

        Optional<LendingBook> lendingBookOptional = lendingBookRepository.findById(request.getId());
        if (!lendingBookOptional.isPresent())
            throw new WSException("Prêt inexistant !");

        LendingBook lendingBook = lendingBookOptional.get();
        if (lendingBook.getDeliverydate() != null)
            throw new WSException("Le livre a été rendu, extension impossible !");

        Calendar calStart = lendingBook.getStartdate();
        Calendar calDeadLine = lendingBook.getDeadlinedate();


        Calendar calCurrent = Calendar.getInstance();

        // si la date de fin est passée, on ne peut pas prolonger
        if (calDeadLine.getTime().getTime() - calCurrent.getTime().getTime() < 0)
            throw new WSException("La date de fin du prêt est dépassée. Vous ne pouvez pas étendre la durée du prêt !");

        // calcul de la difference en jour entre la date de fin de pret et la date de debut de prêt
        long diff = calDeadLine.getTime().getTime() - calStart.getTime().getTime();
        float delayDeadlineDateStartDate = (diff / (1000f * 60f * 60f * 24f));

        // si la date de fin est inférieure à la date de fin initiale + 1 jour (alors on peut ajouter 1 mois)
        if (delayDeadlineDateStartDate > (delayForReservation + 1))
            throw new WSException("Vous ne pouvez étendre la durée du prêt qu'une seule fois !");

        calDeadLine.add(GregorianCalendar.DATE, delayForReservation);
        lendingBook.setDeadlinedate(calDeadLine);
        lendingBookRepository.save(lendingBook);

        response.setDeadLineEndingBook(ConvertUtils.convertCalendarToXMLGregorianCalendar(lendingBook.getDeadlinedate()));
        response.setIdLendingBook(lendingBook.getId());
        logger.info("Extension du prêt {}", lendingBook.getId());

        return response;
    }

    @Override
    public ReturnLendingBookResponse returnLendingBook(ReturnLendingBookRequest request) throws WSException, MessagingException {

        ReturnLendingBookResponse response = new ReturnLendingBookResponse();

        LendingBook lendingBook = lendingBookRepository.findById(request.getId())
                .orElseThrow(() -> new WSException("Prêt inexistant !"));

        // update delivery date
        lendingBook.setDeliverydate(Calendar.getInstance());
        lendingBookRepository.save(lendingBook);

        // update number of copies available
        Book book = lendingBook.getBook();

        // alert first reservation
        WaitingList wl = waitingListService.findOlderWaitingListActiveByBook(lendingBook.getBook());
        if (wl != null) {
            waitingListService.setAlertReservation(wl);
            emailService.toWarnBookAvailable(wl);
            waitingListService.toCancel(wl);
            book.setReservedForMemberId(wl.getMember().getId());
        } else {
            book.setReservedForMemberId(null);
        }

        book.setNumberAvailable(book.getNumberAvailable() + 1);
        bookRepository.save(book);

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Retour du prêt réalisé");

        response.setServiceStatus(serviceStatus);

        logger.info("Retour du prêt {}", lendingBook.getId());

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

        lendingBookSearch.setCanceled(false);
        if (request.isCurrent()) {
            lendingBookSearch.setDeliverydate(null);
        } else {
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

        for (LendingBook lendingBook : lendingBookList) {
            LendingBookWS lendingBookWS = convertLendingBookToLendingBookWS(lendingBook);
            response.getLendingBooks().add(lendingBookWS);
        }

        return response;
    }

    @Override
    public CancelLendingBookResponse cancelLendingBook(CancelLendingBookRequest request) throws WSException {
        CancelLendingBookResponse response = new CancelLendingBookResponse();

        Optional<LendingBook> lendingBookOptional = lendingBookRepository.findById(request.getId());
        if (!lendingBookOptional.isPresent())
            throw new WSException("Prêt inexistant !");

        LendingBook lendingBook = lendingBookOptional.get();

        if (lendingBook.getStartdate().before(Calendar.getInstance()))
            throw new WSException("Vous ne pouvez pas annuler un prêt supérieur à un jour !");

        // update number of copies available
        Book book = lendingBook.getBook();
        book.setNumberAvailable(book.getNumberAvailable() + 1);
        bookRepository.save(book);


        // update boolean cancel
        lendingBook.setCanceled(true);
        lendingBook.setDeliverydate(Calendar.getInstance());
        lendingBookRepository.save(lendingBook);

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Prêt annulé !");

        logger.info("Prêt {} annulé.", lendingBook.getId());

        response.setServiceStatus(serviceStatus);

        return response;
    }

    @Override
    public LendingBook isAlreadyLentByMember(long idBook, long idMember) {
        Book book = bookRepository.getBookById(idBook);
        Member member = memberRepository.getOne(idMember);
        return lendingBookRepository.findLendingBookByBookAndMemberAndDeliverydateIsNull(book, member);
    }

    @Override
    public LendingBook getFirstBookAvailable(Book book) {
        return lendingBookRepository.getFirstByDeadline(book);
    }

    @Override
    public List<LendingBook> getActiveLendingByBook(Book book) {
        return lendingBookRepository.findLendingBookByBookAndCanceledFalseAndDeliverydateIsNull(book);
    }

}
