create table if not exists em.countries (
    country_code text primary key check (data_check.check_not_blank_or_empty(country_code)),
    name text not null check (data_check.check_not_blank_or_empty(name))
);

call audit.audit_table('em.countries');
