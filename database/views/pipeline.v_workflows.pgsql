create or replace view pipeline.v_workflows as
    select w.id, w.name, w.workflow_definition_name, ps.name as pipeline_state
    from pipeline.workflows w
    join pipeline.pipeline_states ps on w.pipeline_state = ps.code;

grant select on pipeline.v_workflows to em_web;
