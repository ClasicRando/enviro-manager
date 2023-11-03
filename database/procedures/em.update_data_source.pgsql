create or replace procedure em.update_data_source(
    ds_id bigint,
    description text,
    files_location text,
    comments text,
    assigned_user text,
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
    description = $2,
    files_location = $3,
    comments = case when trim(coalesce($4,'')) = '' then null else $4 end,
    assigned_user = (select u.user_id from em.users u where u.username = $5),
    last_updated = timezone('utc'::text, now()),
    updated_by = $6,
    search_radius = $7,
    record_warehouse_type = $8,
    reporting_type = $9,
    collection_workflow = $10,
    load_workflow = $11,
    check_workflow = $12,
    qa_workflow = $13
where ds_id = $1
$$;

revoke all on procedure em.update_data_source from public;
grant execute on procedure em.update_data_source to em_web;
