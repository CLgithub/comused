select username,lockwait,status,machine,program from v$session where sid in
     (select session_id from v$locked_object);
--sql
select sql_text from v$sql where hash_value in
    (select sql_hash_value from v$session where sid in (select session_id from v$locked_object));
    
SELECT s.username,l.OBJECT_ID,l.SESSION_ID,s.SERIAL#, l.ORACLE_USERNAME,
     l.OS_USER_NAME,l.PROCESS FROM V$LOCKED_OBJECT l,V$SESSION S
     WHERE l.SESSION_ID=S.SID;
     
 alter system kill session '1519,233'