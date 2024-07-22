create or replace view workflow_engine.v_workflow_runs as
    select wr.workflow_run_id, w.workflow_id, w.name, wr.status, wr.progress
    from workflow_engine.workflow_runs wr
    join workflow_engine.workflows w on wr.workflow_id = w.workflow_id;

grant select on workflow_engine.v_workflow_runs to em_web;
