create table if not exists pipeline.source_table_relationships (
    st_id bigint primary key references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    parent_st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete restrict,
    key_fields text[] not null check(data_check.check_array_not_blank_or_empty(key_fields)),
    parent_key_fields text[] not null check(data_check.check_array_not_blank_or_empty(parent_key_fields)),
    check(st_id != parent_st_id),
    check(array_length(key_fields, 1) = array_length(parent_key_fields, 1))
);

call audit.audit_table('pipeline.plotting_fields');
