create table if not exists workflow_engine.tasks (
    task_id bigint primary key generated always as identity,
    name text not null check(data_check.check_not_blank_or_empty(name)) unique,
    description text not null check(data_check.check_not_blank_or_empty(description))
);

call audit.audit_table('workflow_engine.tasks');
grant select, insert, update, delete on table workflow_engine.tasks to em_web;
