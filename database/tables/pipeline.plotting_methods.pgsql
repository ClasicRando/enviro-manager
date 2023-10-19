create table if not exists pipeline.plotting_methods (
    run_id bigint not null references pipeline.pipeline_runs (run_id) match simple
        on update cascade
        on delete cascade,
    plotting_order smallint not null check (plotting_order > 0),
    plotting_method_id int not null references em.plotting_method_types (method_id) match simple
        on update cascade
        on delete cascade,
    st_id bigint not null references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    primary key (run_id, plotting_order)
);

call audit.audit_table('pipeline.plotting_methods');
