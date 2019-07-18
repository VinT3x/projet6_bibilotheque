package com.oc.projet3.bibliows.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    @Getter    @Setter
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Getter    @Setter
    private String email;

    @Column(name = "password", nullable = false)
    @Length(min = 3, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    @Getter    @Setter
    private String password;

    @Column(name = "firstname", nullable = false)
    @NotEmpty(message = "*Please provide your firstname")
    @Getter    @Setter
    private String firstname;

    @Column(name = "lastname", nullable = false)
    @NotEmpty(message = "*Please provide your lastname")
    @Getter    @Setter
    private String lastname;

    @OneToMany(mappedBy="member")
    @Getter    @Setter
    private Set<LendingBook> lendingBooks = new HashSet<>();

    @Getter    @Setter
    private boolean active;

    @Getter    @Setter
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
