create or replace view em.v_data_source_contacts as
    select dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type, dsc.notes
    from em.data_source_contacts dsc;

grant select on em.v_data_source_contacts to em_web;
