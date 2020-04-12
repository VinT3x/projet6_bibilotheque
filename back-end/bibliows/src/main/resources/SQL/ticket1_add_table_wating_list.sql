create table if not exists waiting_list
(
    id               bigserial not null
        constraint waiting_list_pkey
            primary key,
    alert_date       timestamp,
    canceled         boolean   not null,
    reservation_date timestamp,
    retrieved        boolean   not null,
    book_id          bigint
        constraint fklkdqohenfjoi6ifq3hsfxovtu
            references books,
    member_id        bigint
        constraint fk5xm2vgcsi7jr16i7oqoyrbfjv
            references members
);

alter table waiting_list owner to postgres;