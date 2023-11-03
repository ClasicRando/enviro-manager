create table if not exists em.provinces (
    prov_code text primary key check (data_check.check_not_blank_or_empty(prov_code)),
    country_code text not null references em.countries (country_code) match simple
        on delete restrict
        on update cascade,
    name text not null check (data_check.check_not_blank_or_empty(name))
);

call audit.audit_table('em.provinces');
