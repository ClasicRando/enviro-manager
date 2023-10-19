create table if not exists em.applications (
    id text primary key check(data_check.check_not_blank_or_empty(id)),
    name text not null check(data_check.check_not_blank_or_empty(name)) unique,
    description text not null check(data_check.check_not_blank_or_empty(description))
);
