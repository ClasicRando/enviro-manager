insert into pipeline.record_warehouse_types as rwt (name, description) values
('Current','Only keeps the most current data set. All non-matched records are deleted'),
('Retain','Production contains most recent data set but non-matching records are moved to historical source'),
('Historical','Keep all records. Current data set updates production records but non-matched records are retained in source');
