-- Custom implementation of audit-trigger project, https://github.com/2ndQuadrant/audit-trigger
create schema if not exists audit;

revoke all on schema audit from public;
grant usage on schema audit to em_web;
