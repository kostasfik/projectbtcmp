CREATE TABLE IF NOT EXISTS public.users
(
    id       bigint NOT NULL,
    name     TEXT   NOT NULL,
    email    TEXT   NOT NULL,
    password TEXT   NOT NULL,
    address TEXT,
    PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS public.products
(
    id bigint not null,
    name text not null,
    description text,
    stock DECIMAL(18, 5),
    price DECIMAL(18, 5),
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.orders
(
    number text not null,
    date text,
    status text,
    PRIMARY KEY (number)
);

INSERT INTO public.users (id, name, email, password, address)
VALUES (1, 'Chris', 'csekas@ctrlspace.dev', '123456', null),
       (2, 'Tasos', 'tasos@ctrlspace.dev', '123456', null);


INSERT INTO public.products (id, name, description, stock, price)
VALUES (1, 'Laptop', 'Acer Aspire 5', 10, 500.0),
       (2, 'Smartphone', 'Samsung Galaxy S21', 5, 1000.0),
       (3, 'Tablet', 'Apple iPad Pro', 3, 800.0),
       (4, 'Smartwatch', 'Apple Watch Series 6', 7, 400.0);

INSERT INTO public.orders(number, date, status)
VALUES ('ORD123','18/08','NEW'),
       ('ORD124', '19/08','NEW');

select *
from public.products
where stock < 6
order by stock asc
    limit 1;



