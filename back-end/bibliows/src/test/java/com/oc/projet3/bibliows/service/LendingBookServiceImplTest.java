package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.BookRepository;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import com.oc.projet3.bibliows.dao.MemberRepository;
import com.oc.projet3.bibliows.dao.WaitingListRepository;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.entities.Member;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.mail.MessagingException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@SpringBootTest
class LendingBookServiceImplTest {

    @Value("${delayDay_reserveBook:0}")
    int delayForReservation;
    @Autowired
    private LendingBookService lendingBookService;
    @MockBean
    private LendingBookRepository lendingBookRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private WaitingListRepository waitingListRepository;
    @MockBean
    private WaitingListService waitingListService;

    @Test
    void createLendingBookWithBookNotExist() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.createLendingBook(bookId, memberId));
    }

    @Test
    void createLendingBookWithMemberNotExist() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;
        Book book = new Book();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.createLendingBook(bookId, memberId));
    }

    @Test
    void createLendingBookButLendingInProgress() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;
        Book book = new Book();
        Member member = new Member();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        // si le livre a déjà été emprunté
        Mockito.when(lendingBookRepository.countReservedBookByBookIdAndMemberId(anyLong(), anyLong())).thenReturn(1);

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.createLendingBook(bookId, memberId));
    }

    @Test
    void createLendingBookButOnWaitingList() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;
        Book book = new Book();
        Member member = new Member();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookRepository.countReservedBookByBookIdAndMemberId(anyLong(), anyLong())).thenReturn(0);
        // si sur liste d'attente
        Mockito.when(waitingListRepository.getOnWaitingListActiveByBookAndMember(any(), any())).thenReturn(Optional.of(new WaitingList()));

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.createLendingBook(bookId, memberId));
    }

    @Test
    void createLendingBookButNotAvailable() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;
        Book book = new Book();
        book.setNumberAvailable(0);
        Member member = new Member();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookRepository.countReservedBookByBookIdAndMemberId(anyLong(), anyLong())).thenReturn(0);
        Mockito.when(waitingListRepository.getOnWaitingListActiveByBookAndMember(any(), any())).thenReturn(Optional.empty());


        //THEN
        assertThrows(WSException.class, () -> lendingBookService.createLendingBook(bookId, memberId));
    }

    @Test
    void createLendingBook() throws WSException {
        //GIVEN
        Long bookId = 1L;
        Long memberId = 1L;
        Book book = new Book();
        book.setTitle("test");
        book.setNumberAvailable(1);
        Member member = new Member();
        member.setEmail("test@test.com");

        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(2L);
        lendingBook.setBook(book);
        lendingBook.setMember(member);
        lendingBook.setCanceled(false);

        // Le prêt commence tout de suite
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        lendingBook.setStartdate(calendar);

        // Ajout durée pour un prêt à la date de fin
        Calendar cal = (Calendar) calendar.clone();
        cal.add(GregorianCalendar.DATE, 48);
        lendingBook.setDeadlinedate(cal);

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookRepository.countReservedBookByBookIdAndMemberId(anyLong(), anyLong())).thenReturn(0);
        Mockito.when(waitingListRepository.getOnWaitingListActiveByBookAndMember(any(), any())).thenReturn(Optional.empty());
        Mockito.when(lendingBookRepository.save(any(LendingBook.class))).thenReturn(lendingBook);


        //THEN
        LendingBookWS response = lendingBookService.createLendingBook(1L, 1L);
        Mockito.verify(lendingBookRepository, times(1)).save(any(LendingBook.class));
        assertEquals(response.getTitle(), lendingBook.getBook().getTitle());
        assertEquals(response.getEmail(), lendingBook.getMember().getEmail());
    }

    @Test
    void extendLendingBookWithLendingBookNotExist() {
        //GIVEN
        ExtendLendingBookRequest request = new ExtendLendingBookRequest();
        request.setId(1L);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.extendLendingBook(request));
    }

    @Test
    void extendLendingBookWithLendingBookAlreadyExtended() {
        //GIVEN
        ExtendLendingBookRequest request = new ExtendLendingBookRequest();
        request.setId(1L);

        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(1L);
        Calendar cal = new GregorianCalendar(2020, 0, 28);
        lendingBook.setDeliverydate(cal);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.extendLendingBook(request));
    }

    @Test
    void extendLendingBookWithLendingBookAlreadyBroughtBack() {
        //GIVEN
        ExtendLendingBookRequest request = new ExtendLendingBookRequest();
        request.setId(1L);

        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(1L);
        Calendar calStart = new GregorianCalendar(2020, 0, 28);
        lendingBook.setStartdate(calStart);
        Calendar calDeadLine = new GregorianCalendar(2020, 3, 28);
        lendingBook.setDeadlinedate(calDeadLine);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.extendLendingBook(request));
    }

    @Test
    void extendLendingBookWithDeadLineDatePassed() {
        //GIVEN
        ExtendLendingBookRequest request = new ExtendLendingBookRequest();
        request.setId(1L);

        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(1L);
        Calendar calStart = new GregorianCalendar(2019, 11, 28);
        lendingBook.setStartdate(calStart);
        Calendar calDeadLine = new GregorianCalendar(2020, 2, 28);
        lendingBook.setDeadlinedate(calDeadLine);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.extendLendingBook(request));
    }

    @Test
    void extendLendingBook() throws WSException, DatatypeConfigurationException {
        //GIVEN
        ExtendLendingBookRequest request = new ExtendLendingBookRequest();
        request.setId(1L);

        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(1L);
        Calendar calStart = Calendar.getInstance();
        lendingBook.setStartdate(calStart);
        Calendar calDeadLine = new GregorianCalendar(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH) + 1);
        lendingBook.setDeadlinedate(calDeadLine);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));
        Mockito.when(lendingBookRepository.save(any())).thenReturn(null);

        //THEN
        ExtendLendingBookResponse response = lendingBookService.extendLendingBook(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calDeadLineTest = new GregorianCalendar(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH) + 1);
        calDeadLineTest.add(GregorianCalendar.DAY_OF_MONTH, delayForReservation);

        assertEquals(response.getDeadLineEndingBook().toString(), sdf.format(calDeadLineTest.getTime()));
    }

    @Test
    void returnLendingBookWithLendingBookNotExist() {
        //GIVEN
        ReturnLendingBookRequest request = new ReturnLendingBookRequest();
        request.setId(1L);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.returnLendingBook(request));
    }

    @Test
    void returnLendingBook() throws WSException, MessagingException {
        //GIVEN
        ReturnLendingBookRequest request = new ReturnLendingBookRequest();
        request.setId(1L);
        LendingBook lendingBook = new LendingBook();
        lendingBook.setId(1L);
        Book book = new Book();
        book.setId(1L);
        lendingBook.setBook(book);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));
        Mockito.when(bookRepository.save(any())).thenReturn(null);
        Mockito.when(waitingListService.findOlderWaitingListActiveByBook(any())).thenReturn(null);

        //THEN
        int numberAvailableBefore = lendingBook.getBook().getNumberAvailable();
        ReturnLendingBookResponse response = lendingBookService.returnLendingBook(request);

        assertEquals(response.getServiceStatus().getStatusCode(), "SUCCESS");
        assertEquals(numberAvailableBefore + 1, lendingBook.getBook().getNumberAvailable());
    }

    @Test
    void cancelLendingBookWithLendingBookNotExist() {
        //GIVEN
        CancelLendingBookRequest request = new CancelLendingBookRequest();
        request.setId(1L);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.cancelLendingBook(request));
    }

    @Test
    void cancelLendingBookNoPossibleAfterOneDay() {
        //GIVEN
        CancelLendingBookRequest request = new CancelLendingBookRequest();
        request.setId(1L);
        LendingBook lendingBook = new LendingBook();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH,-1);
        lendingBook.setStartdate(startDate);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));

        //THEN
        assertThrows(WSException.class, () -> lendingBookService.cancelLendingBook(request));
    }

    @Test
    void cancelLendingBook() throws WSException {
        //GIVEN
        CancelLendingBookRequest request = new CancelLendingBookRequest();
        request.setId(1L);
        LendingBook lendingBook = new LendingBook();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH,1);
        lendingBook.setStartdate(startDate);
        Book book = new Book();
        book.setId(1L);
        lendingBook.setBook(book);

        //WHEN
        Mockito.when(lendingBookRepository.findById(any())).thenReturn(Optional.of(lendingBook));
        Mockito.when(bookRepository.save(any())).thenReturn(null);
        Mockito.when(lendingBookRepository.save(any())).thenReturn(null);

        //THEN
        int numberAvailableBefore = lendingBook.getBook().getNumberAvailable();
        CancelLendingBookResponse response = lendingBookService.cancelLendingBook(request);

        assertEquals(response.getServiceStatus().getStatusCode(), "SUCCESS");
        assertEquals(numberAvailableBefore + 1, lendingBook.getBook().getNumberAvailable());
    }
}