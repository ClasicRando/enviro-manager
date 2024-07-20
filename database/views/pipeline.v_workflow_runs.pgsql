create or replace view pipeline.v_workflow_runs as
    select wr.conductor_workflow_id, w.id, w.name, w.workflow_definition_name
    from pipeline.workflow_runs wr
    join pipeline.workflows w on wr.workflow_id = w.id;

grant select on pipeline.v_workflow_runs to em_web;
