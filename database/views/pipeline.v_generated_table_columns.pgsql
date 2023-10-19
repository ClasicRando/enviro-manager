create or replace view pipeline.v_generated_table_columns as
    select
        gtc.gtc_id, gtc.st_id, gtc.generation_expression, gtc.name, gtc.label, gtc.report_order,
        gtc.report_group
    from pipeline.generated_table_columns gtc;

grant select on pipeline.v_generated_table_columns to em_web;
