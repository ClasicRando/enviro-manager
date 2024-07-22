create table if not exists workflow_engine.task_queue_archive (
    workflow_run_id bigint not null,
    task_order int not null,
    task_id bigint not null,
    status workflow_engine.task_status not null,
    input_parameters jsonb,
    output_parameters jsonb,
    output text,
    rules workflow_engine.task_rule[],
    task_start timestamp without time zone,
    task_end timestamp without time zone,
    progress smallint
);

grant select on workflow_engine.task_queue_archive to em_web;

create index if not exists wr_id
on workflow_engine.task_queue_archive(workflow_run_id);
create index if not exists wr_id_task_ord
on workflow_engine.task_queue_archive(workflow_run_id,task_order);
create index if not exists task
on workflow_engine.task_queue_archive(task_id);
