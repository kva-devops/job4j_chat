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

insert into persons (username, password, role_id) values ('user', '$2a$12$XHFq4kc8DM1EVk5hvxJ.Xejm2eJc1rrAAEYVx9ueyIO1BqlpDrdqS', 1);
insert into persons (username, password, role_id) values ('root', '$2a$10$goKzdvoPy9dV/V9G.cOtH.l2FseWkt7.wh2j1KpbBNaW5IGJC1Zo.', 1);
