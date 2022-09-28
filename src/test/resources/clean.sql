/*
insert into users (user_name, user_email) values ( 'A', 'A@gmail.com' );
insert into users (user_name, user_email) values ( 'B', 'B@gmail.com' );

insert into requests(request_description, requestor_id, created) values ( 'request 1', 1, timestamp without time zone '2000-01-01 12:00:00');
insert into requests(request_description, requestor_id, created) values ( 'request 2', 2, timestamp without time zone '2100-01-01 12:00:00')*/

drop table BOOKINGS cascade;
drop table COMMENTS cascade;
drop table ITEMS cascade;
drop table REQUESTS cascade;
drop table USERS cascade;
