create or replace procedure em.create_data_source_contact(
    ds_id bigint,
    name text,
    email text,
    website text,
    type text,
    notes text
)
security definer
language sql
as $$
insert into em.data_source_contacts (ds_id, name, email, website, type, notes)
values ($1, $2, $3, $4, $5, $6)
$$;

revoke all on procedure em.create_data_source_contact from public;
grant execute on procedure em.create_data_source_contact to em_web;
