create table if not exists pipeline.workflow_runs (
    conductor_workflow_id uuid primary key,
    workflow_id bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete set null
);

call audit.audit_table('pipeline.workflow_runs');
