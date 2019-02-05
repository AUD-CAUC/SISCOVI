create or replace function "F_RETORNA_TIPO_RESTITUICAO" (pCodTipoResgate NUMBER) RETURN VARCHAR2
IS

  --Função que recebe o cod do tipo de restituição e 
  --retorna seu nome.

  vTipoRestituicao VARCHAR2(50);

BEGIN

  SELECT nome
    INTO vTipoRestituicao
    FROM tb_tipo_restituicao
    WHERE cod = pCodTipoResgate;

  RETURN vTipoRestituicao;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;

END;
