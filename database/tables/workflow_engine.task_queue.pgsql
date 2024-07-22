create or replace function workflow_engine.task_queue_record_archive()
returns trigger
language plpgsql
as $$
begin
    insert into workflow_engine.task_queue_archive (
        workflow_run_id,task_order,task_id,status,parameters,output,
        rules,task_start,task_end,progress
    )
    select
        tq.workflow_run_id, tq.task_order, tq.task_id, tq.status, tq.parameters, tq.output,
        tq.rules, tq.task_start, tq.task_end, progress
    from old_table tq;
    return null;
end;
$$;

create table if not exists workflow_engine.task_queue (
    workflow_run_id bigint not null references workflow_engine.workflow_runs(workflow_run_id) match simple
        on delete restrict
        on update cascade,
    task_order int not null,
    task_id bigint not null references workflow.tasks(task_id) match simple
        on delete restrict
        on update cascade,
    status workflow_engine.task_status not null default 'Waiting',
    input_parameters jsonb,
    output_parameters jsonb,
    output text check(data_check.check_not_blank_or_empty(output)),
    rules workflow_engine.task_rule[] check(case when rules is not null then rules != '{}' else true end),
    task_start timestamp without time zone,
    task_end timestamp without time zone,
    progress smallint check(case when progress is not null then progress between 0 and 100 else true end),
    primary key (workflow_run_id, task_order),
    check (
        case
            when task_start is not null and task_end is not null then task_end > task_start
            when task_start is null and task_end is null then true
            else task_start is not null
        end
    )
) partition by list(workflow_run_id);

grant select, insert, update, delete on workflow_engine.task_queue to em_web;

create or replace trigger record_delete
    after delete
    on workflow_engine.task_queue
    referencing old table as old_table
    for each statement
    execute function workflow_engine.task_queue_record_archive();
