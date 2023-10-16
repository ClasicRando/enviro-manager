create schema if not exists em;

revoke all on schema em from public;
grant usage on schema em to em_web;
