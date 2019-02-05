create or replace function "F_RETORNA_DATA_INICIO_CONTAGEM" (pCodTerceirizadoContrato NUMBER) RETURN DATE
IS

  --Retorna a data de início para contagem do 13° de um funcionário.

  vMaxDataCalculo DATE;
  vDataRetorno DATE;

BEGIN

  --Seleciona a máxima data de início de contagem existente ou atribuir valor nulo a variável.

  BEGIN

    SELECT MAX(data_inicio_contagem)
      INTO vMaxDataCalculo
      FROM tb_restituicao_decimo_terceiro
      WHERE cod_terceirizado_contrato = pCodTerceirizadoContrato 
        AND parcela IN (0,2);

    vDataRetorno := TO_DATE('01/01/' || (EXTRACT(year FROM vMaxDataCalculo) + 1), 'dd/mm/yyyy');

    EXCEPTION WHEN OTHERS THEN

      vMaxDataCalculo := NULL;    

  END;

  --Caso o valor seja nulo, é atribuída a data de disponibilização para a variável de retorno.

  IF (vMaxDataCalculo IS NULL) THEN

    SELECT data_disponibilizacao
      INTO vDataRetorno
      FROM tb_terceirizado_contrato
      WHERE cod = pCodTerceirizadoContrato;

  END IF;

  RETURN vDataRetorno;

END;