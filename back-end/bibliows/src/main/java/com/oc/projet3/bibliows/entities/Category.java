package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "categories")
public class Category {

    public Category(@NotEmpty String label) {
        this.label = label;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    @Getter    @Setter
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    @NotEmpty
    @Getter    @Setter
    private String label;

    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Getter    @Setter
    private Set<Book> books= new HashSet<>();
}

