create or replace procedure em.add_user_role(
    user_id uuid,
    role text
)
security definer
language sql
as $$
insert into em.user_roles(user_id, role)
select u.user_id, $2 role
from em.users u
where u.user_id = $1
on conflict (user_id, role) do nothing;
$$;

revoke all on procedure em.add_user_role from public;
grant execute on procedure em.add_user_role to em_web;

comment on procedure em.add_user_role IS $$
Add a role to a user's list of roles. Note, if the user already has the role, nothing happens.

Arguments:
uid:
    ID specifying the user to add a new role
role:
    Name of the role to add to the specified user
$$;
