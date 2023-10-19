create table if not exists em.user_roles (
    user_id uuid not null references em.users(user_id)
        on update cascade
        on delete cascade,
    role text not null check (data_check.check_not_blank_or_empty(role)),
    primary key (user_id, role)
);

call audit.audit_table('em.user_roles');
