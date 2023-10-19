create table if not exists pipeline.generated_table_columns (
    gtc_id bigint primary key generated always as identity,
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    generation_expression jsonb not null check(generation_expression != '{}'::jsonb),
    name text not null check(data_check.check_not_blank_or_empty(name)),
    label text not null check(data_check.check_not_blank_or_empty(label)),
    report_order smallint not null check (report_order > 0),
    report_group smallint not null check (report_group > 0),
    unique (st_id, name)
);

call audit.audit_table('pipeline.generated_table_columns');
