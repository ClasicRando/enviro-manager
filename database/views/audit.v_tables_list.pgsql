-- View showing all tables with auditing set up. Ordered by schema, then table.
create or replace view audit.v_tables_list as
    select distinct triggers.trigger_schema as schema, triggers.event_object_table AS audited_table
    from information_schema.triggers
    where triggers.trigger_name::text in ('audit_trigger_row'::text, 'audit_trigger_stm'::text)
    order by 1, 2;
