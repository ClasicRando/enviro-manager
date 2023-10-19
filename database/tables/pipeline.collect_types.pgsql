create table if not exists pipeline.collect_types (
    name text primary key check (data_check.check_not_blank_or_empty(name)),
    description text not null check (data_check.check_not_blank_or_empty(description))
);

call audit.audit_table('pipeline.collect_types');
