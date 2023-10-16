/*
Create a new user with the provided details, returning the new user uid if successful.

Arguments:
full_name:
    Full name of the new user
username:
    Username of the new user
password:
    Password of the new user, validated as the first step
*/
create or replace function em.create_user(
    full_name text,
    username text,
    password text
)
returns uuid
security definer
language sql
as $$
insert into users.users as u (full_name,username,password)
values($1,$2,crypt($3, gen_salt('bf')))
returning u.user_id
$$;

revoke all on function em.create_user from public;
grant execute on function em.create_user to em_web;
