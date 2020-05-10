package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@NoArgsConstructor
@Table(name = "waitingList")
public class WaitingList {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter    @Setter
    private Calendar reservationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter    @Setter
    private Calendar alertDate;

    @Getter    @Setter
    private boolean canceled;

    @Getter    @Setter
    private boolean retrieved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @Getter    @Setter
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Getter    @Setter
    private Member member;
}
