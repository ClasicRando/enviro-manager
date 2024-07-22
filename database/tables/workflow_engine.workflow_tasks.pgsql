create table if not exists workflow_engine.workflow_tasks (
    workflow_id int not null references workflow_engine.workflows(workflow_id) match simple
        on delete restrict
        on update cascade,
    task_order integer not null,
    task_id bigint not null references workflow_engine.tasks(task_id) match simple
        on delete restrict
        on update cascade,
    default_input_parameters jsonb,
    constraint workflow_tasks_pk primary key(workflow_id, task_order)
);

call audit.audit_table('workflow_engine.workflow_tasks');
grant select, insert, update, delete on table workflow_engine.workflow_tasks to em_web;
