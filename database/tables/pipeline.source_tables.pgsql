create table if not exists pipeline.source_tables (
    st_id bigint primary key generated always as identity,
    run_id bigint not null references pipeline.pipeline_runs (run_id) match simple
        on update cascade
        on delete cascade,
    table_name text not null check (table_name ~ '^[0-9A-Z_]+$'),
    file_name text not null check (file_name like '%.%'),
    analyze_table boolean not null default true,
    load_table boolean not null default true,
    qualified boolean not null default true,
    delimiter varchar(1) check (data_check.check_not_blank_or_empty(delimiter::text)),
    encoding text not null default 'utf8'::text check (data_check.check_not_blank_or_empty(encoding)),
    record_count integer not null default 0,
    file_id text not null check (file_id ~ '^F\d+$'),
    url text check (data_check.check_not_blank_or_empty(url)),
    comments text check (data_check.check_not_blank_or_empty(comments)),
    collect_type text not null references pipeline.collect_types (name) match simple
        on update cascade
        on delete restrict,
    collection_task_def text check(data_check.check_not_blank_or_empty(collection_task_def)),
    loader_type_id int not null references pipeline.loader_types (loader_type_id) match simple
        on update cascade
        on delete restrict,
    unique (run_id, file_id),
    unique (run_id, table_name)
);

call audit.audit_table('pipeline.source_tables');
