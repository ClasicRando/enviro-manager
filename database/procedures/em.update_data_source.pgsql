create or replace procedure em.update_data_source(
    ds_id bigint,
    code text,
    prov text,
    country text,
    description text,
    files_location text,
    prov_level boolean,
    comments text,
    assigned_user uuid,
    updated_by uuid,
    search_radius double precision,
    record_warehouse_type smallint,
    reporting_type text,
    collection_workflow bigint,
    load_workflow bigint,
    check_workflow bigint,
    qa_workflow bigint
)
security definer
language sql
as $$
update em.data_sources
set
    code = $2,
    prov = $3,
    country = $4,
    description = $5,
    files_location = $6,
    prov_level = $7,
    comments = $8,
    assigned_user = $9,
    last_updated = timezone('utc'::text, now()),
    updated_by = $10,
    search_radius = $11,
    record_warehouse_type = $12,
    reporting_type = $13,
    collection_workflow = $14,
    load_workflow = $15,
    check_workflow = $16,
    qa_workflow = $17
where ds_id = $1
$$;

revoke all on procedure em.update_data_source from public;
grant execute on procedure em.update_data_source to em_web;