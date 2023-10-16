create table if not exists pipeline.pipeline_states (
    code text primary key check(data_check.check_not_blank_or_empty(code)),
    name text not null check(data_check.check_not_blank_or_empty(name)) unique,
    href text not null check(data_check.check_not_blank_or_empty(href)) unique,
    workflow_order smallint not null unique
);

call audit.audit_table('pipeline.pipeline_states');
