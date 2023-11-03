create or replace function data_check.check_array_not_blank_or_empty(
    text[]
) returns boolean
language plpgsql
immutable
as $$
declare
    val text;
begin
    if $1 = '{}' then
        return false;
    end if;
    foreach val in array $1
    loop
        if not data_check.check_not_blank_or_empty(val) then
            return false;
        end if;
    end loop;
    return true;
end;
$$;
