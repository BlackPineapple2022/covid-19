create schema if not exists covid collate utf8mb4_0900_ai_ci;

create table if not exists country
(
    COUNTRY_ID bigint auto_increment
        primary key,
    COUNTRY_NAME varchar(255) null
);

create table if not exists country_detail
(
    COUNTRY_DETAIL_ID bigint not null
        primary key,
    POPULATION bigint null
);

create table if not exists covidstat
(
    COVIDSTAT_ID bigint not null
        primary key,
    CONFIRMED bigint null,
    DATE datetime null,
    DEATH bigint null,
    RECOVERED bigint null,
    COUNTRY_ID bigint null,
    constraint FK51f7o7vt21uqmahln0uvmdyae
        foreign key (COUNTRY_ID) references country (COUNTRY_ID)
);

create table if not exists hibernate_sequence
(
    next_val bigint null
);

