create or replace procedure em.delete_data_source_contact(
    contact_id bigint,
    ds_id bigint
)
security definer
language sql
as $$
delete from em.data_source_contacts
where
    contact_id = $1
    and ds_id = $2
$$;

revoke all on procedure em.delete_data_source_contact from public;
grant execute on procedure em.delete_data_source_contact to em_web;
