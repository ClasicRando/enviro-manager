create type workflow_engine.schedule_entry as
(
    day_of_week smallint,
    time_of_day time without time zone
);

grant usage on type workflow_engine.schedule_entry to em_web;
