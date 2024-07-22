create or replace function workflow_engine.job_change()
returns trigger
language plpgsql
as $$
begin
    perform pg_notify('jobs', '');
    return null;
end;
$$;

create table if not exists workflow_engine.jobs (
    job_id bigint primary key generated always as identity,
    workflow_id bigint not null references workflow_engine.workflows(workflow_id) match simple
        on delete restrict
        on update cascade,
    job_type workflow_engine.job_type not null,
    maintainer text not null check(data_check.check_not_blank_or_empty(maintainer)),
    job_interval interval check(
        case
            when job_type = 'Interval'::workflow_engine.job_type
                then job_interval is not null and job_interval > interval '0 second'
            else job_interval is null
        end
    ),
    job_schedule workflow_engine.schedule_entry[] check(
        case
            when job_type = 'Scheduled'::workflow_engine.job_type
                then job_schedule is not null and job_schedule != '{}'
            else job_schedule is null
        end
    ),
    is_active boolean not null default false,
    next_run timestamp without time zone not null check(next_run > now() at time zone 'UTC'),
    current_workflow_run_id bigint references workflow_engine.workflow_runs(workflow_run_id) match simple
        on delete restrict
        on update cascade
);

drop trigger if exists job_change_trig on workflow_engine.jobs;
create trigger job_change_trig
    after update or insert or delete
    on workflow_engine.jobs
    for each statement
    execute function workflow_engine.job_change();

call audit.audit_table('workflow_engine.jobs');
grant select, insert, update, delete on workflow_engine.jobs to em_web;
