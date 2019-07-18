package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "authors", uniqueConstraints=@UniqueConstraint(columnNames={"fullname", "dateOfBirth"}))
public class Author {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter   @Setter
    @Column(name = "author_id", updatable = false, nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    @NotEmpty
    @Getter    @Setter
    private String fullname;

    @Temporal(TemporalType.DATE)
    @Getter    @Setter
    private Calendar dateOfBirth;

//    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Getter    @Setter
    private Calendar dateOfDeath;

    @Column(length = 80, nullable = false)
    @Getter    @Setter
    private String nationality;

    @OneToMany(mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Getter    @Setter
    private Set<Book> books= new HashSet<>();

    public Author(@NotEmpty String fullname, Calendar dateOfBirth, Calendar dateOfDeath, String nationality) {
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.nationality = nationality;
    }
}
