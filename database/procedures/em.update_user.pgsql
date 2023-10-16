/*
Update an existing user with new username and/or full name provided

Arguments:
uid:
    UUID of the user to update
new_username:
    New username to set for the specified user
new_full_name:
    New full name to set for the specified user
*/
create or replace procedure em.update_user(
    user_id uuid,
    new_username text,
    new_full_name text
)
security definer
language sql
as $$
update em.users u
set
    username = coalesce(nullif(trim($2), ''), username),
    full_name = coalesce(nullif(trim($3), ''),full_name)
where u.user_id = $1
$$;

revoke all on procedure em.update_user from public;
grant execute on procedure em.update_user to em_web;
