package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.WaitingListRepository;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class WaitingListServiceImpl implements WaitingListService {
    private static Logger logger = LogManager.getLogger(WaitingListServiceImpl.class);


    @Value("${delayHourForRetrieveBook:0}")
    int delayHourForRetrieveBook;

    @Autowired
    LendingBookService lendingBookService;
    @Autowired
    BookService bookService;
    @Autowired
    MemberService memberService;
    @Autowired
    WaitingListRepository waitingListRepository;
    @Autowired
    EmailService emailService;
    // application.properties get delay day for a book's reservation
    @Value("${delayDay_reserveBook:0}")
    int nb_delayForReservation;




    @Override
    public AddToWaitingListResponse addToWaitingList(AddToWaitingListRequest request) throws WSException {
        AddToWaitingListResponse response = new AddToWaitingListResponse();

        Book book = bookService.findById(request.getBookId())
                .orElseThrow(() -> new WSException("Ce livre n'est pas disponible !"));

        Member member = memberService.findById(request.getAccountId())
                .orElseThrow(() -> new WSException("Cet utilisateur n'existe pas !"));

        LendingBook lb = lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId());
        if ( lb!= null)
            throw new WSException("Vous avez déjà emprunté ce livre !");

        if (isAlreadyReservedByMember(request.getBookId(), request.getAccountId()))
            throw new WSException("Vous avez déjà réservé ce livre !");

        if (book.getNumberReservationAvailable() == 0)
            throw new WSException("Le nombre maximun de réservation est atteint pour ce livre !");

        if (book.getNumberAvailable() > 0)
            throw new WSException("Vous ne pouvez pas faire de réservation, il y a des livres disponibles !");

        WaitingList waitingList = new WaitingList();
        waitingList.setMember(member);
        waitingList.setBook(book);
        waitingList.setCanceled(false);
        waitingList.setRetrieved(false);


        // date de réservation
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        waitingList.setReservationDate(calendar);

        waitingList = waitingListRepository.save(waitingList);

        // decrement du nombre de livre disponinle réservé
        book.setNumberReservationAvailable(book.getNumberReservationAvailable() - 1);

        // si le premier à être sur listre d'attente, on met à jour l'entité book
        if(!waitingListRepository.existsWaitingListsByBook(book)){
            book.setReservedForMemberId(member.getId());
        }
        bookService.persist(book);

        WaitingListWS waitingListWS = convertWaitingListToWaitingListWS(waitingList);


        logger.info("RESERVATION : " + waitingList.getId() + ", livre " + book.getTitle() + ", compte " + member.getEmail());

        response.setWaitingList(waitingListWS);

        return response;
    }

    @Override
    public CancelToWaitingListResponse cancelToWaitingList(CancelToWaitingListRequest request) throws WSException, MessagingException {
        CancelToWaitingListResponse response = new CancelToWaitingListResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        WaitingList waitingList = waitingListRepository.findById(request.getId())
                .orElseThrow(() -> new WSException("Cette réservation n'existe pas !"));

        waitingList.setCanceled(true);
        waitingListRepository.save(waitingList);

        // increment du nombre de livre disponinle réservé
        Book book = bookService.findById(waitingList.getBook().getId())
                .orElseThrow(() -> new WSException("Le livre associé à cette réservation n'existe pas !"));
        book.setNumberReservationAvailable(book.getNumberReservationAvailable() + 1);

        // mise à jour de l'entité book
        // et envoie de l'email au suivant

        if (book.getReservedForMemberId() != null && book.getReservedForMemberId().longValue() == waitingList.getMember().getId()){
            WaitingList nextWl = this.findOlderWaitingListActiveByBook(waitingList.getBook());
            book.setReservedForMemberId(nextWl.getMember().getId());
            emailService.toWarnBookAvailable(nextWl);
        }
        bookService.persist(book);

        logger.info("RESERVATION ANNULATION : {}, livre {}, compte {}",waitingList.getId(), book.getTitle(), waitingList.getMember().getEmail());

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Réservation annulée !");

        response.setServiceStatus(serviceStatus);

        return response;
    }

    @Override
    public RetrieveToWaitingListResponse retrieveToWaitingList(RetrieveToWaitingListRequest request) throws WSException {
        RetrieveToWaitingListResponse response = new RetrieveToWaitingListResponse();
        WaitingList waitingList = waitingListRepository.findById(request.getWaitingListId())
                .orElseThrow(() -> new WSException("Cette réservation n'existe pas !"));

        waitingList.setRetrieved(true);

        // creation d'un lendingbook
        LendingBookWS lbWS = lendingBookService.createLendingBook(waitingList.getBook().getId(), waitingList.getMember().getId());
        response.setLendingBook(lbWS);

        return response;
    }

    @Override
    public WaitingList findOlderWaitingListActiveByBook(Book book) {
        return waitingListRepository.findAllCandidateReservationForLoan(book);
    }

    @Override
    public List<WaitingList> findWaitingListCandidateToLendingBook() {
        List<WaitingList> waitingLists = new ArrayList<>();
        bookService.findBookAvailableToLoan().forEach(book ->
                waitingLists.add(waitingListRepository.findAllCandidateReservationForLoan(book)
                ));
        return  waitingLists;

    }

    @Override
    public WaitingList persist(WaitingList wl) {
        return waitingListRepository.save(wl);
    }

    @Override
    public WaitingList toCancel(WaitingList wl) {
        wl.setCanceled(true);
        return waitingListRepository.save(wl);
    }

    @Override
    public Optional<WaitingList> getOnWaitingListActiveByBookIdAndMemberId(Book book,Member member) {
        return waitingListRepository.getOnWaitingListActiveByBookAndMember(book,member);
    }

    @Override
    public List<WaitingList> findWaitingListToCancel(Calendar cal) {

        return waitingListRepository.findAllByAlertDateBeforeAndCanceledIsFalseAndRetrievedIsFalse(cal);
    }


    public boolean isAlreadyReservedByMember(long bookId, long memberId) throws WSException {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new WSException("Livre inexistant !"));

        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new WSException("Compte inexistant !"));

        return waitingListRepository.findOnWaitingListActiveByBookAndMember(book, member).isPresent();
    }

    //convert entities entity to ws entity
    private WaitingListWS convertWaitingListToWaitingListWS(WaitingList waitingList) {

        WaitingListWS waitingListWS = new WaitingListWS();

        //convert Calendar (lendingBook) to XMLGregorianCalendar (lendingBookWS)
        waitingListWS.setReservationdate(ConvertUtils.convertCalendarToXMLGregorianCalendar(waitingList.getReservationDate()));
        waitingListWS.setTitle(waitingList.getBook().getTitle());
        waitingListWS.setId(waitingList.getId());
        waitingListWS.setIdBook(waitingList.getBook().getId());
        waitingListWS.setIdAccount(waitingList.getMember().getId());
        waitingListWS.setEmail(waitingList.getMember().getEmail());
        return waitingListWS;
    }

    @Override
    public void setAlertReservation(WaitingList wl){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY,delayHourForRetrieveBook);
        wl.setAlertDate(calendar);
        persist(wl);
    }

    @Override
    public FindWaitingListResponse findWaitingListByUser(String email) throws WSException {
        FindWaitingListResponse findWaitingListResponse = new FindWaitingListResponse();

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new WSException("Cet utilisateur n'existe pas !"));

        List<WaitingList> waitingLists = waitingListRepository.findOnWaitingListActiveByMember(member);

        for (WaitingList wl:waitingLists) {
            WaitingListWS waitingListWS = convertWaitingListToWaitingListWS(wl);
            findWaitingListResponse.getWaitingList().add(waitingListWS);
        }

        return findWaitingListResponse;
    }
}
