create table if not exists em.data_sources (
    ds_id bigint primary key generated always as identity,
    code text not null,
    prov text references em.provinces (prov_code) match simple
        on update cascade
        on delete restrict,
    country text references em.countries (country_code) match simple
        on update cascade
        on delete restrict,
    description text not null check (data_check.check_not_blank_or_empty(description)),
    files_location text not null check (data_check.check_not_blank_or_empty(files_location)),
    prov_level boolean not null,
    comments text check (data_check.check_not_blank_or_empty(comments)),
    assigned_user uuid not null references em.users (user_id) match simple
        on update cascade
        on delete restrict,
    created_by uuid not null references em.users (user_id) match simple
        on update cascade
        on delete restrict,
    last_updated timestamp with time zone,
    updated_by uuid references em.users (user_id) match simple
        on update cascade
        on delete restrict,
    search_radius double precision not null,
    record_warehouse_type smallint not null references pipeline.record_warehouse_types (id) match simple
        on update cascade
        on delete restrict,
    reporting_type text not null check (data_check.check_not_blank_or_empty(reporting_type)),
    created timestamp with time zone not null default timezone('utc'::text, now()),
    collection_workflow bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete restrict,
    load_workflow bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete restrict,
    check_workflow bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete restrict,
    qa_workflow bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete restrict,
    check (case when prov_level then country is null else data_check.check_not_blank_or_empty(country) end),
    check (case when prov_level then data_check.check_not_blank_or_empty(prov) else prov is null end)
);

call audit.audit_table('em.data_sources');
