create schema if not exists workflow_engine;

revoke all on schema workflow_engine from public;
grant usage on schema workflow_engine to em_web;
