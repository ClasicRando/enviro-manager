insert into pipeline.pipeline_states as ps (code, href, name, workflow_order) values
('collection', '/data/pipeline-state/collection', 'Data Collection', 1),
('load', '/data/pipeline-state/load', 'Data Loading', 2),
('check', '/data/pipeline-state/check', 'Load Checking', 3),
('qa', '/data/pipeline-state/qa', 'Load QA', 4),
('done', '/data/pipeline-state/done', 'Done', 5);
