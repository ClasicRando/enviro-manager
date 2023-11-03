create table if not exists em.report_table_columns (
    rtc_id bigint primary key generated always as identity,
    rt_id bigint not null references em.report_tables (rt_id) match simple
        on update cascade
        on delete cascade,
    stc_id bigint references pipeline.source_table_columns (stc_id) match simple
        on update cascade
        on delete cascade,
    gtc_id bigint references pipeline.generated_table_columns (gtc_id) match simple
        on update cascade
        on delete cascade,
    report_label text not null check (data_check.check_not_blank_or_empty(report_label)),
    report_order smallint not null check (report_order > 0),
    check (case when stc_id is not null then gtc_id is null else true end),
    check (case when gtc_id is not null then stc_id is null else true end)
);

call audit.audit_table('em.report_table_columns');
