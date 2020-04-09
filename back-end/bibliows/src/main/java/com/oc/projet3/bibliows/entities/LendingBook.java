package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@NoArgsConstructor
@Table(name = "lendingbook")
public class LendingBook {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "lendingbook_id", nullable = false)
    @Getter    @Setter
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter    @Setter
    private Calendar startdate;

    @Temporal(TemporalType.DATE)
    @Getter    @Setter
    private Calendar deadlinedate;

    @Temporal(TemporalType.DATE)
    @Getter    @Setter
    private Calendar deliverydate;

    @Getter    @Setter
    private boolean canceled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @Getter    @Setter
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Getter    @Setter
    private Member member;

}
