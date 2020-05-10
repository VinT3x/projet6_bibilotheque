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
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class WaitingListServiceImplTest {
    @Value("${delayDay_reserveBook:0}")
    int delayForReservation;

    @Autowired
    private WaitingListServiceImpl waitingListService;
    @MockBean
    private LendingBookRepository lendingBookRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private BookService bookService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private WaitingListRepository waitingListRepository;
    @MockBean
    private LendingBookService lendingBookService;
    @MockBean
    private EmailService emailService;


    @Test
    void addToWaitingListWithBookIdNotExist() {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingListWithMemberIdNotExist() {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();

        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(new Book()));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingListWithBookAlreadyLent() {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();
        request.setBookId(1L);
        request.setAccountId(1L);
        LendingBook lendingBook = new LendingBook();


        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(new Book()));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId())).thenReturn(lendingBook);

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingListWithBookAlreadyOnWaitingList() throws WSException {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();
        request.setBookId(1L);
        request.setAccountId(1L);
        LendingBook lendingBook = new LendingBook();
        Book book = new Book();
        Member member = new Member();


        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId())).thenReturn(null);
        Mockito.when(waitingListRepository.findOnWaitingListActiveByBookAndMember(book, member)).thenReturn(Optional.of(new WaitingList()));

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingListWithNoReservationAvailable() throws WSException {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();
        request.setBookId(1L);
        request.setAccountId(1L);
        LendingBook lendingBook = new LendingBook();
        Book book = new Book();
        book.setNumberReservationAvailable(0);
        Member member = new Member();


        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId())).thenReturn(null);
        Mockito.when(waitingListRepository.findOnWaitingListActiveByBookAndMember(book, member)).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingListWithLendingAvailable() throws WSException {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();
        request.setBookId(1L);
        request.setAccountId(1L);
        LendingBook lendingBook = new LendingBook();
        Book book = new Book();
        book.setNumberReservationAvailable(2);
        book.setNumberAvailable(1);
        Member member = new Member();


        //WHEN
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId())).thenReturn(null);
        Mockito.when(waitingListRepository.findOnWaitingListActiveByBookAndMember(book, member)).thenReturn(Optional.empty());
        Mockito.when(waitingListRepository.save(any())).thenReturn(null);

        //THEN
        assertThrows(WSException.class, () -> waitingListService.addToWaitingList(request));
    }

    @Test
    void addToWaitingList() throws WSException {
        //GIVEN
        AddToWaitingListRequest request = new AddToWaitingListRequest();
        request.setBookId(1L);
        request.setAccountId(1L);
        WaitingList waitingList = new WaitingList();
        waitingList.setId(1L);
        Book book = new Book();
        book.setId(1L);
        book.setNumberReservationAvailable(2);
        book.setNumberAvailable(0);
        book.setTitle("Titre test");
        waitingList.setBook(book);
        Member member = new Member();
        member.setId(1L);
        member.setEmail("email");
        waitingList.setMember(member);


        //WHEN
        Mockito.when(bookService.findById(anyLong())).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Mockito.when(lendingBookService.isAlreadyLentByMember(request.getBookId(), request.getAccountId())).thenReturn(null);
        Mockito.when(waitingListRepository.findOnWaitingListActiveByBookAndMember(book, member)).thenReturn(Optional.empty());
        Mockito.when(waitingListRepository.save(any())).thenReturn(waitingList);
        Mockito.when(bookRepository.save(any())).thenReturn(null);

        //THEN
        AddToWaitingListResponse response = waitingListService.addToWaitingList(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(member.getEmail(), response.getWaitingList().getEmail(), "email");

    }

    @Test
    void cancelToWaitingListWithWaitingListNotExist() {
        //GIVEN
        CancelToWaitingListRequest request = new CancelToWaitingListRequest();

        //WHEN
        Mockito.when(waitingListRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.cancelToWaitingList(request));
    }

    @Test
    void cancelToWaitingListWithBookNotExist() {
        //GIVEN
        CancelToWaitingListRequest request = new CancelToWaitingListRequest();

        //WHEN
        Mockito.when(waitingListRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(waitingListRepository.save(any(WaitingList.class))).thenReturn(null);
        Mockito.when(bookService.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.cancelToWaitingList(request));
    }

    @Test
    void cancelToWaitingList() throws MessagingException, WSException {
        //GIVEN
        CancelToWaitingListRequest request = new CancelToWaitingListRequest();
        WaitingList waitingList = new WaitingList();
        waitingList.setId(1L);
        Member member = new Member();
        member.setId(1L);
        waitingList.setMember(member);

        Book book = new Book();
        book.setId(1L);
        book.setNumberReservationAvailable(2);
        book.setReservedForMemberId(member.getId());
        waitingList.setBook(book);
        //WHEN
        Mockito.when(waitingListRepository.findById(any())).thenReturn(Optional.of(waitingList));
        Mockito.when(waitingListRepository.save(any(WaitingList.class))).thenReturn(null);
        Mockito.when(bookService.findById(anyLong())).thenReturn(Optional.of(book));
        Mockito.when(waitingListService.findOlderWaitingListActiveByBook(any())).thenReturn(waitingList);
        doNothing().when(emailService).toWarnBookAvailable(any());
        Mockito.when(bookRepository.save(any())).thenReturn(null);

        //THEN
        CancelToWaitingListResponse response = waitingListService.cancelToWaitingList(request);
        assertEquals(response.getServiceStatus().getStatusCode(), "SUCCESS");

    }


    @Test
    void retrieveToWaitingListWithWaitingListNotExist() {
        //GIVEN
        RetrieveToWaitingListRequest request = new RetrieveToWaitingListRequest();

        //WHEN
        Mockito.when(waitingListRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSException.class, () -> waitingListService.retrieveToWaitingList(request));
    }

}