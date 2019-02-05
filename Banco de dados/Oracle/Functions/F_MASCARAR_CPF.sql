create or replace function "F_MASCARAR_CPF" (pCpf IN VARCHAR2) RETURN VARCHAR2
IS

  --Função que retorna a string do CPF com a máscara.

  vRetorno VARCHAR2(14);

BEGIN

  IF(LENGTH(pCpf) <> 11)THEN

    RETURN NULL;

  END IF;

  vRetorno := substr(pCpf,1,3) || '.' || substr(pCpf,4,3) || '.' || substr(pCpf,7,3) || '-' || substr(pCpf,10,2);

  RETURN vRetorno;

  EXCEPTION WHEN OTHERS THEN

    RETURN NULL;

END;
