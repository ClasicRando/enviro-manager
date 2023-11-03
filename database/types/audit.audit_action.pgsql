/*
Audit log action

 - I = Insert
 - D = Delete
 - U = Update
 - T = Truncate
*/
create type audit.audit_action as enum('I','D','U','T');
