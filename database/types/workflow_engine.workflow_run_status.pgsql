create type workflow_engine.workflow_run_status as enum (
    'Waiting',
    'Scheduled',
    'Running',
    'Paused',
    'Failed',
    'Complete',
    'Canceled'
);

grant usage on type workflow_engine.workflow_run_status to em_web;
