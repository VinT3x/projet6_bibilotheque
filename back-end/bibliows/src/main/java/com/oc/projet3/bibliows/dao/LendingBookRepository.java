package com.oc.projet3.bibliows.dao;


import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.bibliows.entities.LendingBook;
import com.oc.projet3.bibliows.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface LendingBookRepository extends JpaRepository<LendingBook,Long>, JpaSpecificationExecutor<LendingBook>, LendingBookRepositoryCustom {

    @Query(value = "select count(*) from lendingbook rb where rb.book_id=:book_id and rb.member_id=:member_id and rb.deliverydate is null", nativeQuery = true)
    int countReservedBookByBookIdAndMemberId(@Param("book_id") Long book_id,@Param("member_id") Long member_id);

    List<LendingBook> findLendingBookByDeliverydateIsNullAndDeadlinedateBeforeOrderByDeadlinedateAsc(Calendar deadline);

    boolean existsLendingBookByBookIdAndMemberIdAndDeliverydateIsNull(long bookId, long memberId);


    LendingBook findLendingBookByBookAndMemberAndDeliverydateIsNull(Book book, Member member);

    List<LendingBook> findLendingBookByBookAndCanceledFalseAndDeliverydateIsNull(Book book);

}
