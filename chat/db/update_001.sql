create table rooms (
    id serial primary key,
    name varchar (2000) not null
);

create table roles (
    id serial primary key,
    name varchar (2000) not null
);

create table persons (
    id serial primary key,
    username varchar (2000) not null,
    password varchar (2000) not null,
    role_id int references roles(id)
);

create table messages (
    id serial primary key,
    text varchar (2000),
    created timestamp,
    room_id int references rooms(id) on delete cascade,
    person_id int references persons(id) on delete cascade
);

insert into rooms (name) values ('common');
insert into rooms (name) values ('vip');

insert into roles (name) values ('guest');
insert into roles (name) values ('user');
insert into roles (name) values ('moderator');