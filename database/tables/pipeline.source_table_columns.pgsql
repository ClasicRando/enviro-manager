create table if not exists pipeline.source_table_columns (
    stc_id bigint primary key generated always as identity,
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    column_index int not null,
    name text not null,
    data_type text not null,
    max_length int not null,
    label text not null,
    report_order smallint not null check (report_order > 0),
    report_group smallint not null check (report_group > 0),
    unique (st_id, name)
);

call audit.audit_table('pipeline.source_table_columns');
