
create SCHEMA IF NOT EXISTS public;

create table if not exists authors
(
    author_id bigserial not null
        constraint authors_pkey
            primary key,
    date_of_birth date,
    date_of_death date,
    fullname varchar(100) not null,
    nationality varchar(80) not null,
    constraint ukeubf6dr96s2qdnfwdw22s1hex
        unique (fullname, date_of_birth)
);


create table if not exists categories
(
    category_id bigserial not null
        constraint categories_pkey
            primary key,
    label varchar(50) not null
        constraint uk_1ybnws5535892mnjo42rdi0fl
            unique
);


create table if not exists books
(
    book_id bigserial not null
        constraint books_pkey
            primary key,
    date_official_release date not null,
    number_available integer not null,
    number_of_copies integer not null
        constraint books_number_of_copies_check
            check (number_of_copies >= 1),
    number_of_page integer not null,
    number_reservation_available integer not null,
    reserved_for_member_id bigint,
    number_of_copies_for_reservation integer,
    summary varchar(2000) not null,
    title varchar(200) not null,
    author_id bigint
        constraint fkfjixh2vym2cvfj3ufxj91jem7
            references authors,
    category_id bigint
        constraint fkleqa3hhc0uhfvurq6mil47xk0
            references categories,
    constraint ukc6m3v05i97jx7ka4ewig9y1oa
        unique (title, author_id)
);

create table if not exists members
(
    member_id bigint not null
        constraint members_pkey
            primary key,
    active boolean not null,
    email varchar(255) not null
        constraint uk_9d30a9u1qpg8eou0otgkwrp5d
            unique,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    password varchar(255) not null,
    role varchar(255)
);


create table if not exists lendingbook
(
    lendingbook_id bigserial not null
        constraint lendingbook_pkey
            primary key,
    deadlinedate date,
    deliverydate date,
    canceled boolean not null,
    startdate timestamp,
    book_id bigint
        constraint fk1ep4rrvju3lktf0uuirb3e972
            references books,
    member_id bigint
        constraint fkn5yo1t8ew5y0nwn9m3ouebf22
            references members
);


create table if not exists roles
(
    role_id serial not null
        constraint roles_pkey
            primary key,
    name varchar(255)
);


create table if not exists waiting_list
(
    id bigserial not null
        constraint waiting_list_pkey
            primary key,
    alert_date timestamp,
    canceled boolean not null,
    reservation_date timestamp,
    retrieved boolean not null,
    book_id bigint
        constraint fklkdqohenfjoi6ifq3hsfxovtu
            references books,
    member_id bigint
        constraint fk5xm2vgcsi7jr16i7oqoyrbfjv
            references members
);

