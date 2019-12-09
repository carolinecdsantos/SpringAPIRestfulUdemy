CREATE table users (
    id serial,
    name varchar(50),
    password varchar,
    email varchar(100),
    primary key(id)
);

CREATE table wallet(
    id serial,
    name varchar(60),
    value numeric(10,2),
    primary key (id)
);

CREATE table users_wallet(
    id serial,
    wallet_id integer,
    user_id integer,
    primary key(id),
    foreign key(user_id) references users(id),
    foreign key(wallet_id) references wallet(id)
);

CREATE table wallet_items(
    id serial,
    wallet_id integer,
    date  date,
    type varchar(2),
    description varchar (500),
    value numeric (10,2),
    primary key(id),
    foreign key (wallet_id) references wallet(id)
);