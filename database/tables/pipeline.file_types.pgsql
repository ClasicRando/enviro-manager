create table if not exists pipeline.file_types (
    file_extension text primary key check(data_check.check_not_blank_or_empty(file_extension)),
    loader_type_id int not null references pipeline.loader_types (loader_type_id) match simple
        on update cascade
        on delete restrict
);

call audit.audit_table('pipeline.file_types');