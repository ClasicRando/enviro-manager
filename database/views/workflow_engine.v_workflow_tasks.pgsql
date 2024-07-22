create or replace view workflow_engine.v_workflow_tasks as
    select
        wt.workflow_id, wt.task_order, wt.task_id, t.name, t.description,
        wt.default_input_parameters
    from workflow_engine.workflow_tasks wt
    join workflow_engine.tasks t on wt.task_id = t.task_id;

grant select on workflow_engine.v_workflow_tasks to em_web;
