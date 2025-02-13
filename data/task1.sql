drop database if exists movies;

create database movies;

use movies;

create table imdb (
    imdb_id varchar(16) primary key,
    vote_average float default '0.0',
    vote_count int default '0',
    release_date date not null,
    revenue decimal(15, 2) default '1000000.00',
    budget decimal(15, 2) default '1000000.00',
    runtime int default '90',

    constraint chk_vote_average check(vote_average >= 0),
    constraint chk_vote_count check(vote_count >= 0),
    constraint chk_revenue check(revenue >= 0),
    constraint chk_budget check(budget >= 0),
    constraint chk_runtime check(runtime >= 0)

);

grant all privileges on movies.* to 'fred'@'%';
flush privileges;
