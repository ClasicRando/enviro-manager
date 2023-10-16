/*
Revoke a role for a specified user.

Arguments:
uid:
    UUID specifying the user to revoke a role
role:
    Name of the role to revoke from the specified user
*/
create or replace procedure em.revoke_user_role(
    user_id uuid,
    role text
)
security definer
language sql
as $$
delete from em.user_roles ur
where
    ur.user_id = $1
    and ur.role = $2;
$$;

revoke all on procedure em.revoke_user_role from public;
grant execute on procedure em.revoke_user_role to em_web;
