package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Calendar;


@Entity
@NoArgsConstructor
@Table(name = "books", uniqueConstraints=@UniqueConstraint(columnNames={"title", "author_id"}))
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    @Getter    @Setter
    private Long id;

    @Column(length = 200, nullable = false)
    @Getter    @Setter
    @NotEmpty
    private String title;

    @Column(length = 2000, nullable = false)
    @Getter    @Setter
    private String summary;

    @Column(nullable = false)
    @Min(1)
    @Getter    @Setter
    private int numberOfCopies;

    @Column(nullable = false)
    @Getter    @Setter
    private int numberAvailable;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Getter    @Setter
    private Calendar dateOfficialRelease;

    @Column(nullable = false)
    @Getter    @Setter
    private int numberOfPage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Getter    @Setter
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @Getter    @Setter
    private Author author;

}
