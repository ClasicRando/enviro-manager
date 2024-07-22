create type workflow_engine.task_status as enum (
    'Waiting',
    'Running',
    'Paused',
    'Failed',
    'Rule Broken',
    'Complete',
    'Canceled'
);

grant usage on type workflow_engine.task_status to em_web;
