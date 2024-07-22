create type workflow_engine.task_rule as
(
    name text,
    failed boolean,
    message text
);

grant usage on type workflow_engine.task_rule to em_web;
