create table if not exists em.users (
    user_id uuid primary key default gen_random_uuid(),
    full_name text not null check (data_check.check_not_blank_or_empty(full_name)),
    username text not null check (data_check.check_not_blank_or_empty(username)) unique,
    password text not null check (data_check.check_not_blank_or_empty(password)),
    enabled boolean not null default true
);

call audit.audit_table('em.users');
grant select, insert, update on em.users to em_web;
