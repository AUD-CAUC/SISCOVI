create or replace procedure "P_SISCOVI_DATABASE_DESTRUCTION"
AS

  --Procedimento que deleta todas as tabelas do banco de dados.
  
BEGIN

  FOR c IN (SELECT'DROP '||object_type||' '|| object_name || ' CASCADE CONSTRAINTS' AS delete_command FROM user_objects WHERE object_type IN ('TABLE')) LOOP

    EXECUTE IMMEDIATE (c.delete_command);
    DBMS_OUTPUT.PUT_LINE(c.delete_command);
    
  END LOOP;
  
  FOR d IN (SELECT'DROP '||object_type||' '|| object_name AS delete_command FROM user_objects WHERE object_type IN ('SEQUENCE')) LOOP

    EXECUTE IMMEDIATE (d.delete_command);
    DBMS_OUTPUT.PUT_LINE(d.delete_command);
    
  END LOOP;


  DBMS_OUTPUT.PUT_LINE('Script executado com sucesso');

END;
