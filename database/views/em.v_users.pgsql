create or replace view em.v_users as
    with user_roles as (
        select ur.user_id, array_agg(ur.role)::text[] roles
        from em.user_roles ur
        group by ur.user_id
    )
    select u.user_id, u.username, u.full_name, coalesce(ur.roles,'{}'::text[]) roles
    from em.users u
    left join user_roles ur
    on u.user_id = ur.user_id;

grant select on em.v_users to em_web;
