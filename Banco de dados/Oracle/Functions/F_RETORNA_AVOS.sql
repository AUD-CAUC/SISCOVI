create or replace function "F_RETORNA_AVOS" (pValor FLOAT) RETURN NUMBER
IS

  vRetorno NUMBER := NULL;

BEGIN
  
  IF (pValor > 9) THEN
  
    vRetorno := 11;
    
  ELSE 
    
    IF (pValor > 9) THEN 
    
      vRetorno := 12;

    END IF;
          
  END IF;

  RETURN vRetorno;

END;