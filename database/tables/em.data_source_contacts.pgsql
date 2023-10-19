create table if not exists em.data_source_contacts (
    contact_id bigint primary key generated always as identity,
    ds_id bigint not null references em.data_sources (ds_id) match simple
        on update cascade
        on delete cascade,
    name text not null check(data_check.check_not_blank_or_empty(name)),
    email text not null check(data_check.check_not_blank_or_empty(email)),
    website text check(data_check.check_not_blank_or_empty(website)),
    type text check(data_check.check_not_blank_or_empty(type)),
    notes text check(data_check.check_not_blank_or_empty(notes))
);

call audit.audit_table('em.data_source_contacts');
