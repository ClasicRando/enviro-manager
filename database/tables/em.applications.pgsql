create table if not exists em.applications (
    id text primary key check(data_check.check_not_blank_or_empty(id)),
    name text not null check(data_check.check_not_blank_or_empty(name)) unique,
    description text not null check(data_check.check_not_blank_or_empty(description))
);

insert into em.applications as a (id, name, description) values
('pipe', 'Data Pipeline', 'Interface to execute and track ETL on source data')
on conflict
do update set
    name = excluded.name,
    description = excluded.description
where
    a.name != excluded.name
    or a.description != excluded.description;
