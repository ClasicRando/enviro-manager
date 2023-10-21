create table if not exists pipeline.source_table_columns (
    stc_id bigint primary key generated always as identity,
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    column_index smallint not null check (column_index >= 0),
    name text not null,
    data_type text not null
);

call audit.audit_table('pipeline.source_table_columns');
