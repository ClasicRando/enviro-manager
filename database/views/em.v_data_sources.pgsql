create or replace view em.v_data_sources as
    select
        ds.ds_id, ds.code, ds.prov, coalesce(p.country_code, ds.country) country, ds.prov_level,
        ds.description, ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
        wt.name record_warehouse_type, au.full_name assigned_user, cu.full_name created_by,
        ds.created, uu.full_name updated_by, ds.last_updated, cw1.name collection_workflow,
        lw.name load_workflow, cw2.name check_workflow, qw.name qa_workflow
    from em.data_sources ds
    left join em.provinces p on ds.prov = p.prov_code
    left join em.users au on ds.assigned_user = au.user_id
    left join em.users cu on ds.created_by = cu.user_id
    left join em.users uu on ds.updated_by = uu.user_id
    left join pipeline.record_warehouse_types wt on ds.record_warehouse_type = wt.id
    left join pipeline.workflows cw1 on ds.collection_workflow = cw1.id
    left join pipeline.workflows lw on ds.load_workflow = lw.id
    left join pipeline.workflows cw2 on ds.check_workflow = cw2.id
    left join pipeline.workflows qw on ds.qa_workflow = qw.id;

grant select on em.v_data_sources to em_web;
