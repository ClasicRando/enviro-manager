/*
Add a list of roles to the user. Note, if a role already exists then nothing will happen

Arguments:
uid:
    UUID specifying the user to add a new role
roles:
    Roles to be added to the user as an array of role names
*/
create or replace procedure em.add_user_roles(
    user_id uuid,
    roles text[]
)
security definer
language sql
as $$
insert into em.user_roles(user_id, role)
select u.user_id, r.description
from em.users u
cross join unnest($2) r(description)
where u.user_id = $1
on conflict (user_id, role) do nothing;
$$;

revoke all on procedure em.add_user_roles from public;
grant execute on procedure em.add_user_roles to em_web;
