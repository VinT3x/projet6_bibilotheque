create database biblio
  with owner postgres;

create SCHEMA IF NOT EXISTS public;

create table if not exists batch_job_execution
(
  job_execution_id           bigint    not null
    constraint batch_job_execution_pkey
      primary key,
  version                    bigint,
  job_instance_id            bigint    not null
    constraint job_inst_exec_fk
      references batch_job_instance,
  create_time                timestamp not null,
  start_time                 timestamp,
  end_time                   timestamp,
  status                     varchar(10),
  exit_code                  varchar(2500),
  exit_message               varchar(2500),
  last_updated               timestamp,
  job_configuration_location varchar(2500)
);

alter table batch_job_execution
  owner to postgres;


create table if not exists batch_job_execution_context
(
  job_execution_id   bigint        not null
    constraint batch_job_execution_context_pkey
      primary key
    constraint job_exec_ctx_fk
      references batch_job_execution,
  short_context      varchar(2500) not null,
  serialized_context text
);

alter table batch_job_execution_context
  owner to postgres;

create table if not exists batch_job_execution_params
(
  job_execution_id bigint       not null
    constraint job_exec_params_fk
      references batch_job_execution,
  type_cd          varchar(6)   not null,
  key_name         varchar(100) not null,
  string_val       varchar(250),
  date_val         timestamp,
  long_val         bigint,
  double_val       double precision,
  identifying      char         not null
);

alter table batch_job_execution_params
  owner to postgres;

create table if not exists batch_job_instance
(
  job_instance_id bigint       not null
    constraint batch_job_instance_pkey
      primary key,
  version         bigint,
  job_name        varchar(100) not null,
  job_key         varchar(32)  not null,
  constraint job_inst_un
    unique (job_name, job_key)
);

alter table batch_job_instance
  owner to postgres;

create table if not exists batch_step_execution
(
  step_execution_id  bigint       not null
    constraint batch_step_execution_pkey
      primary key,
  version            bigint       not null,
  step_name          varchar(100) not null,
  job_execution_id   bigint       not null
    constraint job_exec_step_fk
      references batch_job_execution,
  start_time         timestamp    not null,
  end_time           timestamp,
  status             varchar(10),
  commit_count       bigint,
  read_count         bigint,
  filter_count       bigint,
  write_count        bigint,
  read_skip_count    bigint,
  write_skip_count   bigint,
  process_skip_count bigint,
  rollback_count     bigint,
  exit_code          varchar(2500),
  exit_message       varchar(2500),
  last_updated       timestamp
);

alter table batch_step_execution
  owner to postgres;

create table if not exists batch_step_execution_context
(
  step_execution_id  bigint        not null
    constraint batch_step_execution_context_pkey
      primary key
    constraint step_exec_ctx_fk
      references batch_step_execution,
  short_context      varchar(2500) not null,
  serialized_context text
);

alter table batch_step_execution_context
  owner to postgres;


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

create sequence categories_category_id_seq;

alter sequence categories_category_id_seq owner to postgres;


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

create sequence authors_author_id_seq;

alter sequence authors_author_id_seq owner to postgres;


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

create sequence books_book_id_seq;

alter sequence books_book_id_seq owner to postgres;


create table if not exists members
(
  member_id serial
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

-- create sequence members_member_id_seq;
--
-- alter sequence members_member_id_seq owner to postgres;


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

create sequence lendingbook_lendingbook_id_seq;

alter sequence lendingbook_lendingbook_id_seq owner to postgres;


