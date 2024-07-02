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