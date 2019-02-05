create or replace FUNCTION  "F_RETORNA_SENHA_CRIPTOGRAFADA" (p_senha IN VARCHAR2) RETURN VARCHAR2
IS

  v_senha_criptografada VARCHAR2(32) := NULL;

BEGIN
  
  IF p_senha IS NULL THEN
  
    RETURN NULL;
    
  END IF;
  
  v_senha_criptografada := RAWTOHEX(dbms_obfuscation_toolkit.md5(INPUT => utl_raw.cast_to_raw(p_senha)));  
  
  RETURN v_senha_criptografada;  
  
END;

