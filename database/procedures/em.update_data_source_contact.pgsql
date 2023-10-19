create or replace procedure em.update_data_source_contact(
    contact_id bigint,
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
update em.data_source_contacts
set
    name = $3,
    email = $4,
    website = $5,
    type = $6,
    notes = $7
where
    contact_id = $1
    and ds_id = $2
$$;

revoke all on procedure em.update_data_source_contact from public;
grant execute on procedure em.update_data_source_contact to em_web;
