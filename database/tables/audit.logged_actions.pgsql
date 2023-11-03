-- History of auditable actions on audited tables, from audit.if_modified_func()
create table if not exists audit.logged_actions (
    -- Unique identifier for each auditable event
    event_id bigint primary key generated always as identity,
    -- Database schema audited table for this event is in
    schema_name text not null,
    -- Non-schema-qualified table name of table event occurred in
    table_name text not null,
    -- Table OID. Changes with drop/create. Get with 'tablename'::regclass
    relid oid not null,
    -- Login / session user whose statement caused the audited event
    session_user_name text,
    -- Transaction start timestamp for tx in which audited event occurred
    action_tstamp_tx timestamp with time zone not null,
    -- Statement start timestamp for tx in which audited event occurred
    action_tstamp_stm timestamp with time zone not null,
    -- Wall clock time at which audited event's trigger call occurred
    action_tstamp_clk timestamp with time zone not null,
    -- Identifier of transaction that made the change. May wrap, but unique paired with
    -- action_tstamp_tx.
    transaction_id bigint,
    -- Application name set when this audit event occurred. Can be changed in-session by client.
    application_name text,
    -- IP address of client that issued query. Null for unix domain socket.
    client_addr inet,
    -- Remote peer IP port address of client that issued query. Undefined for unix socket.
    client_port integer,
    -- Top-level query that caused this auditable event. May be more than one statement.
    client_query text,
    -- Action type; I = insert, D = delete, U = update, T = truncate
    action audit.audit_action not null,
    -- Record value. Null for statement-level trigger. For INSERT this is the new tuple. For DELETE
    -- and UPDATE it is the old tuple.
    row_data jsonb,
    -- New values of fields changed by UPDATE. Null except for row-level UPDATE events.
    changed_fields jsonb,
    -- 't' if audit event is from an FOR EACH STATEMENT trigger, 'f' for FOR EACH ROW
    statement_only boolean not null,
    -- UUID of the user who execute the change
    user_id uuid
);

create index if not exists logged_actions_relid_idx on audit.logged_actions(relid);
create index if not exists logged_actions_action_tstamp_tx_stm_idx
    on audit.logged_actions(action_tstamp_stm);
create index if not exists logged_actions_action_idx on audit.logged_actions(action);
