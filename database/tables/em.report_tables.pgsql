create table if not exists em.report_tables (
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    sub_table_no smallint not null check (sub_table_no > 0),
    parent_table bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    report_label text not null check (data_check.check_not_blank_or_empty(report_label)),
    report_order smallint not null check (report_order > 0),
    key_fields text[] not null check(data_check.check_array_not_blank_or_empty(key_fields)),
    parent_key_fields text[] not null check(data_check.check_array_not_blank_or_empty(parent_key_fields)),
    check(array_length(key_fields, 1) = array_length(parent_key_fields, 1)),
    unique (st_id, sub_table_no)
);

call audit.audit_table('em.report_tables');
