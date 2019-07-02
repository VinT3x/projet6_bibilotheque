create database biblio
  with owner postgres;

create SCHEMA IF NOT EXISTS public;

create sequence batch_step_execution_seq;

alter sequence batch_step_execution_seq owner to postgres;

create sequence batch_job_execution_seq;

alter sequence batch_job_execution_seq owner to postgres;

create sequence batch_job_seq;

alter sequence batch_job_seq owner to postgres;

create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to postgres;


create table if not exists categories
(
  category_id bigserial not null
    constraint categories_pkey
      primary key,
  label varchar(50) not null
);

alter table categories owner to postgres;

create table if not exists authors
(
  author_id     bigserial    not null
    constraint authors_pkey
      primary key,
  date_of_birth date,
  date_of_death date,
  fullname      varchar(100) not null,
  nationality   varchar(80)  not null,
  constraint ukeubf6dr96s2qdnfwdw22s1hex
    unique (fullname, date_of_birth)
);

alter table authors
  owner to postgres;

create table if not exists books
(
  book_id               bigserial     not null
    constraint books_pkey
      primary key,
  category_id           bigint  not null,
  date_official_release date          not null,
  number_available      integer       not null,
  number_of_copies      integer       not null
    constraint books_number_of_copies_check
      check (number_of_copies >= 1),
  number_of_page        integer       not null,
  summary               varchar(2000) not null,
  title                 varchar(200)  not null,
  author_id             bigint
    constraint fkfjixh2vym2cvfj3ufxj91jem7
      references authors,
  constraint ukc6m3v05i97jx7ka4ewig9y1oa
    unique (title, author_id)
);

alter table books
  owner to postgres;

create table if not exists members
(
  member_id bigint       not null
    constraint members_pkey
      primary key,
  active    boolean      not null,
  email     varchar(255) not null
    constraint uk_9d30a9u1qpg8eou0otgkwrp5d
      unique,
  firstname varchar(255) not null,
  lastname  varchar(255) not null,
  password  varchar(255) not null,
  role      varchar(255)
);

alter table members
  owner to postgres;

create table if not exists lendingbook
(
  lendingbook_id bigserial not null
    constraint lendingbook_pkey
      primary key,
  deadlinedate   date,
  deliverydate   date,
  iscancel       boolean   not null,
  startdate      timestamp,
  book_id        bigint
    constraint fk1ep4rrvju3lktf0uuirb3e972
      references books,
  member_id      bigint
    constraint fkn5yo1t8ew5y0nwn9m3ouebf22
      references members
);

alter table lendingbook
  owner to postgres;


