create table if not exists pipeline.pipeline_runs (
    run_id bigint primary key generated always as identity,
    ds_id bigint not null references em.data_sources (ds_id) match simple
        on update cascade
        on delete cascade,
    record_date date not null,
    collection_user_id uuid references em.users (user_id) match simple
        on update cascade
        on delete set null,
    load_user_id uuid references em.users (user_id) match simple
        on update cascade
        on delete set null,
    check_user_id uuid references em.users (user_id) match simple
        on update cascade
        on delete set null,
    qa_user_id uuid references em.users (user_id) match simple
        on update cascade
        on delete set null,
    current_pipeline_state text not null references pipeline.pipeline_states (code) match simple
        on update cascade
        on delete set null,
    collection_workflow_id bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete set null,
    load_workflow_id bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete set null,
    check_workflow_id bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete set null,
    qa_workflow_id bigint not null references pipeline.workflows (id) match simple
        on update cascade
        on delete set null,
    is_active boolean not null default false,
    production_count int not null default 0 check(production_count >= 0),
    staging_count int not null default 0 check(staging_count >= 0),
    match_count int not null default 0 check(match_count >= 0),
    new_count int not null default 0 check(new_count >= 0),
    plotting_stats jsonb not null default '{}',
    has_child_table boolean not null default false,
    merge_type pipeline.merge_type not null
);

call audit.audit_table('pipeline.pipeline_runs');
