create schema if not exists pipeline;

revoke all on schema pipeline from public;
grant usage on schema pipeline to em_web;
