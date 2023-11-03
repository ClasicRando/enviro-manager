create table if not exists em.report_tables (
    rt_id bigint primary key generated always as identity,
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    sub_table_no smallint not null check (sub_table_no > 0),
    parent_table_id bigint references em.report_tables (rt_id) match simple
        on update cascade
        on delete cascade,
    report_label text not null check (data_check.check_not_blank_or_empty(report_label)),
    report_order smallint not null check (report_order > 0),
    key_fields text[] check(data_check.check_array_not_blank_or_empty(key_fields)),
    parent_key_fields text[] check(data_check.check_array_not_blank_or_empty(parent_key_fields)),
    check(case when parent_table is not null then key_fields is not null else true end),
    check(case when parent_table is not null then parent_key_fields is not null else true end),
    check(
        case
            when parent_table is null then true
            else array_length(key_fields, 1) = array_length(parent_key_fields, 1)
        end
    ),
    unique (st_id, sub_table_no)
);

call audit.audit_table('em.report_tables');
