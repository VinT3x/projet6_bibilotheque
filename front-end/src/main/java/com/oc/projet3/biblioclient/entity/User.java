package com.oc.projet3.biblioclient.entity;

public class User {

    private String email;
    private String password;
    private String lastname;
    private String firstname;
    private Long idMember;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String lastname, String firstname, Long id) {
        this.email = email;
        this.password = password;
        this.lastname = lastname;
        this.firstname = firstname;
        this.idMember = id;
    }

    public User(String email, String password, Long idMember) {
        this.email = email;
        this.password = password;
        this.idMember = idMember;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Long getIdMember() {
        return idMember;
    }

    public void setIdMember(Long id) {
        this.idMember = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", idMember=" + idMember +
                '}';
    }
}
