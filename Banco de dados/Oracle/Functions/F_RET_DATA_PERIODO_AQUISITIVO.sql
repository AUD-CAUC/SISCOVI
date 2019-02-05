create or replace function "F_RET_DATA_PERIODO_AQUISITIVO" (pCodTerceirizadoContrato NUMBER, pOperacao NUMBER) RETURN DATE
IS

  --Função que retorna o início ou fim do período aquisitivo.

  --pOperação: 
  --1 - Início do período aquisitivo.
  --2 - Fim do período aquisitivo.

  vDataInicio DATE;
  vDataFim DATE;
  vCodContrato NUMBER;

BEGIN

  --Seleciona a data de disponibilização do terceirizado no contrato.

  SELECT data_disponibilizacao,
         cod_contrato
    INTO vDataInicio,
         vCodContrato
    FROM tb_terceirizado_contrato
    WHERE cod = pCodTerceirizadoContrato;

  vDataFim := vDataInicio + 364;

  --Loop com limite de 10 anos (para controle de exceções) que verifica qual o período aquisitivo vigente.

  FOR i IN 1 .. 10 LOOP
    
    IF (F_SALDO_FERIAS (vCodContrato, pCodTerceirizadoContrato, vDataInicio, vDataFim) > 0) THEN

      EXIT;

    END IF;

    vDataInicio := vDataFim + 1;

    vDataFim := vDataInicio + 364;

  END LOOP;

  --Retorna o início do período aquisitivo válido (corrente).

  IF (pOperacao = 1) THEN

    RETURN vDataInicio;

  END IF;

  --Retorna o fim do período aquisitivo válido (corrente).

  IF (pOperacao = 2) THEN

    RETURN vDataFim;

  END IF; 

  RETURN NULL;

END;
