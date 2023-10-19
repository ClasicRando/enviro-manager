create table if not exists pipeline.workflows (
    id bigint primary key generated always as identity,
    name text not null check(data_check.check_not_blank_or_empty(name)),
    workflow_definition_name text not null check(data_check.check_not_blank_or_empty(workflow_definition_name)),
    pipeline_state text not null references pipeline.pipeline_states (code) match simple
        on update cascade
        on delete restrict
);

call audit.audit_table('pipeline.workflows');
