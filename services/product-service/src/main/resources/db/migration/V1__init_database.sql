create table if not exists category
(
    id integer not null primary key,

    description varchar(255),

    name varchar(255)

);

create table if not exists product
(

    id integer not null primary key,

    description varchar(255),

    name varchar(255),

    available_quantity double precision not null,

    price numeric(38, 2),

    category_id integer
            constraint fk1mlasdalksjfakjsfy references category

);

-- Sequences in databases are used to generate unique numeric values, often for primary key columns.
-- They ensure that each value is unique and typically increment by a specified amount each time a new value is generated.
create sequence if not exists category_seq increment by 50;

create sequence if not exists product_seq increment by 50;