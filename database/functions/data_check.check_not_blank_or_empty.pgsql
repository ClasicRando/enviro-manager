create or replace function data_check.check_not_blank_or_empty(
    text
) returns boolean
language plpgsql
immutable
as $$
begin
    return coalesce($1,'x') !~ '^\s*$';
end;
$$;
