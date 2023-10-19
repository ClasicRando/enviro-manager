create table if not exists pipeline.plotting_fields (
    st_id bigint primary key references pipeline.source_tables (st_id) match simple
        on update cascade
        on delete cascade,
    merge_key text check(data_check.check_not_blank_or_empty(merge_key)),
    name text check(data_check.check_not_blank_or_empty(name)),
    address_line1 text check(data_check.check_not_blank_or_empty(address_line1)),
    address_line2 text check(data_check.check_not_blank_or_empty(address_line2)),
    city text check(data_check.check_not_blank_or_empty(city)),
    alternate_cities text[] check(data_check.check_array_not_blank_or_empty(alternate_cities)),
    mail_code text check(data_check.check_not_blank_or_empty(mail_code)),
    latitude text check(data_check.check_not_blank_or_empty(latitude)),
    longitude text check(data_check.check_not_blank_or_empty(longitude)),
    prov text check(data_check.check_not_blank_or_empty(prov)),
    clean_address text check(data_check.check_not_blank_or_empty(clean_address)),
    clean_city text check(data_check.check_not_blank_or_empty(clean_city))
);

call audit.audit_table('pipeline.plotting_fields');
