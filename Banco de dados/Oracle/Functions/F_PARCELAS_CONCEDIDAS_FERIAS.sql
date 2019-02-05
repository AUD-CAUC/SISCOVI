create or replace function "F_PARCELAS_CONCEDIDAS_FERIAS" (pCodContrato NUMBER, pCodTerceirizadoContrato NUMBER, pDataInicio DATE, pDataFim DATE) RETURN NUMBER
IS

  --Função que retorna o número de parcelas de férias
  --concedidas a um funcionário em um determinado
  --contrato em um período aquisitivo específico.
    
  vParcelasConcedidas NUMBER := 0;

BEGIN

  SELECT COUNT(cod)
    INTO vParcelasConcedidas
    FROM tb_restituicao_ferias
    WHERE cod_terceirizado_contrato = pCodTerceirizadoContrato
      AND data_inicio_periodo_aquisitivo = pDataInicio
      AND data_fim_periodo_aquisitivo = pDataFim;

  RETURN vParcelasConcedidas;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;

END;
