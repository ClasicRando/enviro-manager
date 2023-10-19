/*
Update an existing user with the new password provided

Arguments:
username:
    Unique name of the user to update
new_password:
    New password to set for the specified user
*/
create or replace procedure em.reset_password(
    user_id uuid,
    new_password text
)
security definer
language sql
as $$
update em.users u
set password = crypt($2, gen_salt('bf'))
where u.user_id = $1
$$;

revoke all on procedure em.reset_password from public;
grant execute on procedure em.reset_password to em_web;
