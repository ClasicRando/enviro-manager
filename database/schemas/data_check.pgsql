create schema if not exists data_check;

revoke all on schema data_check from public;
grant usage on schema data_check to em_web;
