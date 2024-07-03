create database data_jpa;

use data_jpa;

create table categories
(
    id   bigint       not null auto_increment,
    name varchar(100) not null,
    primary key (id)
) engine = innodb;

select *
from categories;

create table products
(
    id          bigint       not null auto_increment,
    name        varchar(100) not null,
    price       bigint       not null,
    category_id bigint       not null,
    primary key (id),
    foreign key fk_products_categories (category_id) references categories (id)
) engine innodb;

select *
from products;

alter table categories
    add column created_date       timestamp,
    add column last_modified_date timestamp;

select *
from categories;