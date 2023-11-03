create table if not exists pipeline.record_warehouse_types (
    id smallint primary key generated always as identity,
    name text not null check(data_check.check_not_blank_or_empty(name)),
    description text not null check(data_check.check_not_blank_or_empty(description))
);

call audit.audit_table('pipeline.record_warehouse_types');
