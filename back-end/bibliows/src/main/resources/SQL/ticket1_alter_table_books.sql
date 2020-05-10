alter table books
    add column number_reservation_available integer not null,
    add column reserved_for_member_id bigint,
    add column number_of_copies_for_reservation integer not null default 0;

UPDATE public.books
SET number_of_copies_for_reservation=number_of_copies * 2,
    number_reservation_available=number_of_copies * 2
WHERE book_id is not null;