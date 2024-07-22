create or replace function workflow_engine.workflow_run_create()
returns trigger
language plpgsql
as $$
begin
    execute format(
        'create table "workflow_engine".%I partition of workflow_engine.task_queue for values in (%L)',
        'task_queue_' || new.workflow_run_id,
        new.workflow_run_id
    );
    return null;
end;
$$;

create or replace function workflow_engine.workflow_run_status_event()
returns trigger
language plpgsql
as $$
declare
    v_job_id bigint;
begin
    if new.status = 'Scheduled' then
        perform pg_notify('wr_scheduled', '');
    elsif new.status = 'Canceled' then
        perform pg_notify('wr_canceled', '');
    end if;

    select j.job_id
    into v_job_id
    from workflow_engine.jobs j
    where j.current_workflow_run_id = new.workflow_run_id;

    if v_job_id is not null and new.status not in ('Scheduled', 'Running') then
        perform pg_notify('jobs', '');
    end if;
    return null;
end;
$$;

create table if not exists workflow_engine.workflow_runs (
    workflow_run_id bigint primary key,
    workflow_id bigint not null references workflow_engine.workflows(workflow_id) match simple
        on update cascade
        on delete set null,
    status workflow_engine.workflow_run_status not null default 'Waiting',
    progress smallint check(case when progress is not null then progress between 0 and 100 else true end)
);

call audit.audit_table('workflow_engine.workflow_runs');
grant select, insert, update, delete on table workflow_engine.workflow_runs to em_web;

create or replace trigger workflow_run_task_queue
    after insert
    on workflow_engine.workflow_runs
    for each row
    execute function workflow_engine.workflow_run_create();

create or replace trigger workflow_run_status
    after update of status
    on workflow_engine.workflow_runs
    for each row
    execute function workflow_engine.workflow_run_status_event();
