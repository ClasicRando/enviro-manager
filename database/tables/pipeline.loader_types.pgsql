create table if not exists pipeline.loader_types (
    loader_type_id int primary key,
    name text not null check(data_check.check_not_blank_or_empty(name)) unique
);

call audit.audit_table('pipeline.loader_types');
