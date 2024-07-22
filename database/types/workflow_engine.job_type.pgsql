create type workflow_engine.job_type as enum (
    'Scheduled',
    'Interval'
);

grant usage on type workflow_engine.job_type to em_web;
