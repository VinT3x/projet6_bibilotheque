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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
// pour correction, il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class WaitingListServiceImpl implements WaitingListService {
    private static Logger logger = LogManager.getLogger(LendingBookServiceImpl.class);
    private final LendingBookService lendingBookService;
    private final BookService bookService;
    private final MemberService memberService;
    private final WaitingListRepository waitingListRepository;
    // application.properties get delay day for a book's reservation
    @Value("${delayDay_reserveBook:0}")
    int nb_delayForReservation;

    @Autowired
    public WaitingListServiceImpl(WaitingListRepository waitingListRepository, LendingBookService lendingBookService, BookService bookService, MemberService memberService){
        this.lendingBookService = lendingBookService;
        this.bookService = bookService;
        this.memberService = memberService;
        this.waitingListRepository = waitingListRepository;
    }


    @Override
    public AddToWaitingListResponse addToWaitingList(AddToWaitingListRequest request) throws WSException {
        AddToWaitingListResponse response = new AddToWaitingListResponse();

        Book book = bookService.findById(request.getBookId())
                .orElseThrow(() -> new WSException("Ce livre n'est pas disponible !"));

        Member member = memberService.findById(request.getAccountId())
                .orElseThrow(() -> new WSException("Cet utilisateur n'existe pas !"));

        if (lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId()))
            throw new WSException("Vous avez déjà emprunté ce livre !");

        if (isAlreadyReservedByMember(request.getBookId(), request.getAccountId()))
            throw new WSException("Vous avez déjà réservé ce livre !");

        if (book.getNumberReservationAvailable() == 0)
            throw new WSException("Le nombre maximun de réservation est atteint pour ce livre !");

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
        bookService.persist(book);

        WaitingListWS waitingListWS = convertWaitingListToWaitingListWS(waitingList);


        logger.info("RESERVATION : " + waitingList.getId() + ", livre " + book.getTitle() + ", compte " + member.getEmail());

        response.setWaitingList(waitingListWS);

        return response;
    }

    @Override
    public CancelToWaitingListResponse cancelToWaitingList(CancelToWaitingListRequest request) throws WSException {
        CancelToWaitingListResponse response = new CancelToWaitingListResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        WaitingList waitingList = waitingListRepository.findById(request.getId())
                .orElseThrow(() -> new WSException("Cette réservation n'existe pas !"));

        waitingList.setCanceled(true);
        waitingList = waitingListRepository.save(waitingList);

        // increment du nombre de livre disponinle réservé
        Book book = bookService.findById(waitingList.getBook().getId())
                .orElseThrow(() -> new WSException("Le livre associé à cette réservation n'existe pas !"));
        book.setNumberReservationAvailable(book.getNumberReservationAvailable() + 1);
        bookService.persist(book);

        WaitingListWS waitingListWS = convertWaitingListToWaitingListWS(waitingList);

        logger.info("RESERVATION ANNULATION : " + waitingList.getId() + ", livre " + book.getTitle() + ", compte " + waitingList.getMember().getEmail());

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
    public Optional<WaitingList> findOlderWaitingListActiveByBookIdAndMemberId(Long bookId, Long memberId) {
        return waitingListRepository.findFirstByReservationDateAndCanceledIsFalseAndRetrievedIsFalseOrderByReservationDateAsc(bookId, memberId);
    }

    @Override
    public List<WaitingList> findWaitingListCandidateToLendingBook() {
        return null;
    }

    @Override
    public WaitingList persist(WaitingList wl) {
        return waitingListRepository.save(wl);
    }

    @Override
    public WaitingList cancel(WaitingList wl) {
        wl.setCanceled(true);
        return waitingListRepository.save(wl);
    }

    @Override
    public List<WaitingList> findWaitingListToCancel(Calendar cal) {

        return waitingListRepository.findAllByAlertDateBeforeAndCanceledIsFalseAndRetrievedIsFalse(cal);
    }


    public boolean isAlreadyReservedByMember(long bookId, long memberId){
        return waitingListRepository.existsWaitingListByBookIdAndMemberIdAndCanceledIsFalseAndRetrievedIsFalse(bookId,memberId);
    }

    //convert entities entity to ws entity
    private WaitingListWS convertWaitingListToWaitingListWS(WaitingList waitingList) {

        WaitingListWS waitingListWS = new WaitingListWS();

        //convert Calendar (lendingBook) to XMLGregorianCalendar (lendingBookWS)
        waitingListWS.setReservationdate(ConvertUtils.convertCalendarToXMLGregorianCalendar(waitingList.getReservationDate()));
        waitingListWS.setTitle(waitingList.getBook().getTitle());
        waitingListWS.setIdBook(waitingList.getBook().getId());
        waitingListWS.setIdAccount(waitingList.getMember().getId());
        waitingListWS.setEmail(waitingList.getMember().getEmail());
        return waitingListWS;
    }
}
