create table if not exists em.plotting_method_types (
    method_id int primary key,
    name text not null check(data_check.check_not_blank_or_empty(name)) unique
);

call audit.audit_table('em.plotting_method_types');
