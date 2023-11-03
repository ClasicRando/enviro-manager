create or replace function em.create_data_source(
    code text,
    prov text,
    country text,
    description text,
    files_location text,
    prov_level boolean,
    comments text,
    assigned_user uuid,
    created_by uuid,
    search_radius double precision,
    record_warehouse_type smallint,
    reporting_type text,
    collection_workflow bigint,
    load_workflow bigint,
    check_workflow bigint,
    qa_workflow bigint
)
returns bigint
security definer
language sql
as $$
insert into em.data_sources (
    code, prov, country, description, files_location, prov_level, comments, assigned_user,
    created_by, search_radius, record_warehouse_type, reporting_type, collection_workflow,
    load_workflow, check_workflow, qa_workflow
) values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16)
returning ds_id
$$;

revoke all on function em.create_data_source from public;
grant execute on function em.create_data_source to em_web;
