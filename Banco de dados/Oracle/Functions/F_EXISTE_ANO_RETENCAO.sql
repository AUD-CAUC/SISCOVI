create or replace function "F_EXISTE_ANO_RETENCAO" (pCodContrato NUMBER, pAno NUMBER) RETURN NUMBER
IS

  --Verifica se existe ao menos uma retenção feita para o ano passado
  --como argumento no contrato em questão.

  vRetorno NUMBER := 0;

BEGIN

  SELECT COUNT(tmr.cod)
    INTO vRetorno
    FROM tb_total_mensal_a_reter tmr
      JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato
      JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod
      JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato
      JOIN tb_contrato c ON c.cod = fc.cod_contrato
    WHERE c.cod = pCodContrato
      AND EXTRACT(year FROM tmr.data_referencia) = pAno;

  IF (vRetorno > 0) THEN

    RETURN vRetorno;

  END IF;

  RETURN NULL;

  EXCEPTION WHEN OTHERS THEN

    RETURN NULL;

END;
