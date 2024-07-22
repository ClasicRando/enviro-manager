create table if not exists workflow_engine.workflows (
    workflow_id int primary key generated always as identity,
    name text not null check(data_check.check_not_blank_or_empty(name)) unique,
    is_deprecated boolean not null default false,
    new_workflow int references workflow_engine.workflows(workflow_id) match simple
        on delete set null
        on update cascade
);

call audit.audit_table('workflow_engine.workflows');
grant select, insert, update, delete on table workflow_engine.workflows to em_web;
