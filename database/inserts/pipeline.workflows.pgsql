insert into pipeline.workflows as p (name, workflow_definition_name, pipeline_state) values
('Default Collection', 'default-data-collection', 'collection'),
('Default Load', 'default-data-load', 'load'),
('Default Check', 'default-data-check', 'check'),
('Default QA', 'default-data-qa', 'qa');
