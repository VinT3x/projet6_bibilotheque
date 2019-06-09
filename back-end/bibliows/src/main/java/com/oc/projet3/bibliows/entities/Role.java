package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;


    @Getter
    @Setter
    private String name;

//    @ManyToMany(mappedBy = "roles")
//    @Getter    @Setter
//    private Set<Member> members = new HashSet<>();
}
