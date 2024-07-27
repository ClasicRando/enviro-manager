create or replace view pipeline.v_pipeline_runs as
    select
        pr.run_id, ds.ds_id, ds.code as data_source_code, pr.record_date,
        u1.full_name as collection_user, u2.full_name as load_user,
        u3.full_name as check_user, u4.full_name as qa_user,
        ps.name as current_pipeline_state,
        pr.is_active, pr.production_count, pr.staging_count, pr.match_count, pr.new_count,
        pr.plotting_stats, pr.has_child_table, pr.merge_type
    from pipeline.pipeline_runs pr
    join em.data_sources ds on pr.ds_id = ds.ds_id
    join pipeline.pipeline_states ps on pr.current_pipeline_state = ps.code
    left join em.users u1 on pr.collection_user_id = u1.user_id
    left join em.users u2 on pr.load_user_id = u2.user_id
    left join em.users u3 on pr.check_user_id = u3.user_id
    left join em.users u4 on pr.qa_user_id = u4.user_id;

grant select on pipeline.v_pipeline_runs to em_web;
