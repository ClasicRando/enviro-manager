/*
Validates that the credentials passed in match a user. If the user is found, then it returns the
user ID.

Arguments:
username:
    Username of the user to validate
password:
    Password of the user to validate
*/
create or replace function em.validate_user(
    username text,
    password text
)
returns uuid
immutable
security definer
language sql
as $$
select u2.user_id
from em.users u2
where
    u2.username = $1
    and u2.password = crypt($2, u2.password)
$$;

revoke all on function em.validate_user from public;
grant execute on function em.validate_user to we_web;
