create or replace function "F_MASCARAR_CNPJ" (pCnpj IN VARCHAR2) RETURN VARCHAR2
IS

  --Função que retorna a string do CNPJ com a máscara.

  vRetorno VARCHAR2(18);

BEGIN

  IF(LENGTH(pCnpj) <> 14)THEN

    RETURN NULL;

  END IF;

  vRetorno := substr(pCnpj,1,2) || '.' || substr(pCnpj,3,3) || '.' || substr(pCnpj,6,3) || '/' || substr(pCnpj,9,4) || '-' || substr(pCnpj,13,2);

  RETURN vRetorno;

  EXCEPTION WHEN OTHERS THEN

    RETURN NULL;

END;
