create table if not exists pipeline.pr_workflow_runs (
    run_id bigint not null references pipeline.pipeline_runs(run_id) match simple
        on update cascade
        on delete cascade,
    pipeline_state text not null references pipeline.pipeline_states(code) match simple
        on update cascade
        on delete cascade,
    workflow_run_id bigint not null references workflow_engine.workflow_runs(workflow_run_id) match simple
        on update cascade
        on delete restrict,
    primary key (run_id, pipeline_state)
);

call audit.audit_table('pipeline.pr_workflow_runs');
grant select, insert, update, delete on table pipeline.pr_workflow_runs to em_web;
