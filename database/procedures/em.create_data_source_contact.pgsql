create or replace procedure em.create_data_source_contact(
    name text,
    email text,
    website text,
    type text,
    notes text,
    ds_id bigint
)
security definer
language sql
as $$
insert into em.data_source_contacts (name, email, website, type, notes, ds_id)
values ($1, $2, $3, $4, $5, $6)
$$;

revoke all on procedure em.create_data_source_contact from public;
grant execute on procedure em.create_data_source_contact to em_web;
