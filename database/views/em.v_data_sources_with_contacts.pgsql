create or replace view em.v_data_sources_with_contacts as
    with contacts as (
        select dsc.ds_id, array_agg(dsc.*)::em.data_source_contacts[] contacts
        from em.data_source_contacts dsc
        group by dsc.ds_id
    )
    select
        ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description, ds.files_location,
        ds.comments, ds.search_radius, ds.reporting_type, ds.record_warehouse_type, ds.assigned_user,
        ds.created_by, ds.created, ds.updated_by, ds.last_updated, ds.collection_workflow,
        ds.load_workflow, ds.check_workflow, ds.qa_workflow, c.contacts
    from em.v_data_sources ds
    left join contacts c on ds.ds_id = c.ds_id;

grant select on em.v_data_sources_with_contacts to em_web;
