create table if not exists users
(
    user_id    bigint primary key generated by default as identity,
    user_name  varchar(200)        not null,
    user_email varchar(200) unique not null
);

create table if not exists items
(
    item_id          bigint primary key generated by default as identity,
    item_name        varchar(200)                                        not null,
    item_description varchar(1000)                                       not null,
    is_available     boolean                                             not null,
    owner_id         bigint references users (user_id) on delete cascade not null,
    request_id       bigint
);

create table if not exists bookings
(
    booking_id bigint primary key generated by default as identity,
    start_date timestamp without time zone                         not null,
    end_date   timestamp without time zone                         not null,
    item_id    bigint references items (item_id) on delete cascade not null,
    booker_id  bigint references users (user_id) on delete cascade not null,
    status     int                                                 not null
);

create table if not exists requests
(
    request_id          bigint primary key generated by default as identity,
    request_description varchar(1000),
    requestor_id        bigint references users (user_id) not null
);

create table if not exists comments
(
    comment_id bigint primary key generated by default as identity,
    text        varchar(2000)                                       not null,
    item_id     bigint references items (item_id) on delete cascade not null,
    author_id    bigint references users (user_id) on delete cascade not null,
    created timestamp without time zone not null
);
