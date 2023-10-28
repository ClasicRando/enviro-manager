create or replace view pipeline.v_record_warehouse_types as
    select rwt.id, rwt.name, rwt.description
    from pipeline.record_warehouse_types rwt;

grant select on pipeline.v_record_warehouse_types to em_web;
