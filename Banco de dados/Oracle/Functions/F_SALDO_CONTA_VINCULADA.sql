create or replace function "F_SALDO_CONTA_VINCULADA" (pCodTerceirizadoContrato NUMBER, pAno NUMBER, pOperacao NUMBER, pCodRubrica NUMBER) RETURN FLOAT
IS

  --Função que retorna um valor relacionado ao saldo da conta vinculada.

  --pOperacao = 1 - RETENÇÃO POR FUNCIONÁRIO
  --pOperacao = 2 - RESTITUIÇÃO FÉRIAS POR FUNCIONÁRIO
  --pOperacao = 3 - RESTITUICAO 13º POR FUNCIONÁRIO

  --Legenda de rúbricas:
  --1 - Férias
  --2 - Terço constitucional
  --3 - Décimo terceiro
  --4 - FGTS
  --5 - Multa do FGTS
  --6 - Penalidade do FGTS
  --7 - Incidência do submódulo 4.1 (corresponde a inciência de retenção)

  --pCodRubrica especiais:
  --100 - TOTAL
  --101 - Incidência de férias
  --102 - Incidência de terço de férias
  --103 - Incidência de décimo terceiro

  vFeriasRetido FLOAT := 0;
  vTercoConstitucionalRetido FLOAT := 0;
  vDecimoTerceiroRetido FLOAT := 0;
  vIncidenciaRetido FLOAT := 0;
  vMultaFGTSRetido FLOAT := 0;
  vTotalRetido FLOAT := 0;
  vFeriasRestituido FLOAT := 0;
  vTercoConstitucionalRestituido FLOAT := 0;
  vIncidenciaFeriasRestituido FLOAT := 0;
  vIncidenciaTercoRestituido FLOAT := 0;
  vDecimoTerceiroRestituido FLOAT := 0;
  vIncidencia13Restituido FLOAT := 0;
  vTotalRestituido FLOAT := 0;

BEGIN

  --Definição dos valores relacionados a retenção por funcionário.

  IF (pOperacao = 1) THEN

    SELECT SUM(tmr.ferias + DECODE(rtm.ferias, NULL, 0, rtm.ferias)) AS "Férias retido",
           SUM(tmr.terco_constitucional + DECODE(rtm.terco_constitucional, NULL, 0, rtm.terco_constitucional))  AS "Abono de férias retido",
           SUM(tmr.decimo_terceiro + DECODE(rtm.decimo_terceiro, NULL, 0, rtm.decimo_terceiro)) AS "Décimo terceiro retido",
           SUM(tmr.incidencia_submodulo_4_1 + DECODE(rtm.incidencia_submodulo_4_1, NULL, 0, rtm.incidencia_submodulo_4_1)) AS "Incid. do submód. 4.1 retido",
           SUM(tmr.multa_fgts + DECODE(rtm.multa_fgts, NULL, 0, rtm.multa_fgts)) AS "Multa do FGTS retido",
           SUM(tmr.total + DECODE(rtm.total, NULL, 0, rtm.total)) AS "Total retido"
      INTO vFeriasRetido,
           vTercoConstitucionalRetido,
           vDecimoTerceiroRetido,
           vIncidenciaRetido,
           vMultaFGTSRetido,
           vTotalRetido 
      FROM tb_total_mensal_a_reter tmr
        JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato
        LEFT JOIN tb_retroatividade_total_mensal rtm ON rtm.cod_total_mensal_a_reter = tmr.cod
      WHERE EXTRACT(year FROM tmr.data_referencia) = pAno
        AND tc.cod = pCodTerceirizadoContrato;

  END IF;

  --Definição dos valores relacionados a restituição de férias por funcionário.

  IF (pOperacao = 2) THEN

    SELECT SUM(rf.valor_ferias) AS "Férias restituído",
           SUM(rf.valor_terco_constitucional) AS "1/3 constitucional restituído",
           SUM(rf.incid_submod_4_1_ferias) AS "Incid. de férias restituído",
           SUM(rf.incid_submod_4_1_terco) AS "Incod. de terço restituído",
           SUM(rf.valor_ferias + rf.valor_terco_constitucional + rf.incid_submod_4_1_ferias + rf.incid_submod_4_1_terco) AS "Total restituído"
      INTO vFeriasRestituido,
           vTercoConstitucionalRestituido,
           vIncidenciaFeriasRestituido,
           vIncidenciaTercoRestituido,
           vTotalRestituido 
      FROM tb_restituicao_ferias rf
        JOIN tb_terceirizado_contrato tc ON tc.cod = rf.cod_terceirizado_contrato
      WHERE EXTRACT(year FROM rf.data_inicio_periodo_aquisitivo) = pAno
        AND tc.cod = pCodTerceirizadoContrato;

  END IF;
  
  --Definição dos valores relacionados a restituição de décimo terceiro por funcionário.
  
  IF (pOperacao = 3) THEN

    SELECT SUM(rdt.valor) AS "Décimo terceiro restituído",
           SUM(rdt.incidencia_submodulo_4_1) AS "Incid. de 13° restituído",
           SUM(rdt.valor + rdt.incidencia_submodulo_4_1) AS "Total restituído"
      INTO vDecimoTerceiroRestituido,
           vIncidencia13Restituido,
           vTotalRestituido
      FROM tb_restituicao_decimo_terceiro rdt
        JOIN tb_terceirizado_contrato tc ON tc.cod = rdt.cod_terceirizado_contrato
      WHERE EXTRACT(year FROM rdt.data_inicio_contagem) = pAno
        AND tc.cod = pCodTerceirizadoContrato;

  END IF;

  --Retorno do valor de férias retido.

  IF (pOperacao = 1 AND pCodRubrica = 1) THEN
  
    IF (vFeriasRetido IS NULL) THEN vFeriasRetido := 0; END IF;
  
    RETURN vFeriasRetido;
  
  END IF;
 
  --Retorno do valor de terço constitucional retido.
  
  IF (pOperacao = 1 AND pCodRubrica = 2) THEN
  
    IF (vTercoConstitucionalRetido IS NULL) THEN vTercoConstitucionalRetido := 0; END IF;
  
    RETURN vTercoConstitucionalRetido;
  
  END IF;
  
  --Retorno do valor de décimo terceiro retido.

  IF (pOperacao = 1 AND pCodRubrica = 3) THEN
  
    IF (vDecimoTerceiroRetido IS NULL) THEN vDecimoTerceiroRetido := 0; END IF;
  
    RETURN vDecimoTerceiroRetido;
  
  END IF;
  
  --Retorno do valor de incidência retido.
  
  IF (pOperacao = 1 AND pCodRubrica = 7) THEN
  
    IF (vIncidenciaRetido IS NULL) THEN vIncidenciaRetido := 0; END IF;
  
    RETURN vIncidenciaRetido;
  
  END IF;
  
  --Retorno do valor de multa do FGTS retido.
  
  IF (pOperacao = 1 AND pCodRubrica = 5) THEN
  
    IF (vMultaFGTSRetido IS NULL) THEN vMultaFGTSRetido := 0; END IF;
  
    RETURN vMultaFGTSRetido;
  
  END IF;
  
  --Retorno do valor total retido.
  
  IF (pOperacao = 1 AND pCodRubrica = 100) THEN
  
    IF (vTotalRetido IS NULL) THEN vTotalRetido := 0; END IF;
  
    RETURN vTotalRetido;
  
  END IF;
  
  --Retorno do valor de férias restituído.
  
  IF (pOperacao = 2 AND pCodRubrica = 1) THEN
  
    IF (vFeriasRestituido IS NULL) THEN vFeriasRestituido := 0; END IF;
  
    RETURN vFeriasRestituido;
  
  END IF;
  
  --Retorno do valor de terço constitucional restituído.
  
  IF (pOperacao = 2 AND pCodRubrica = 2) THEN
  
    IF (vTercoConstitucionalRestituido IS NULL) THEN vTercoConstitucionalRestituido := 0; END IF;
  
    RETURN vTercoConstitucionalRestituido;
  
  END IF;
  
  --Retorno do valor de incidência sobre férias restituído.
  
  IF (pOperacao = 2 AND pCodRubrica = 101) THEN
  
    IF (vIncidenciaFeriasRestituido IS NULL) THEN vIncidenciaFeriasRestituido := 0; END IF;
  
    RETURN vIncidenciaFeriasRestituido;
  
  END IF;
  
  --Retorno do valor de incidência sobre férias restituído.
  
  IF (pOperacao = 2 AND pCodRubrica = 102) THEN
  
    IF (vIncidenciaTercoRestituido IS NULL) THEN vIncidenciaTercoRestituido := 0; END IF;
  
    RETURN vIncidenciaTercoRestituido;
  
  END IF;
  
  --Retorno do valor total restituído de férias.
  
  IF (pOperacao = 2 AND pCodRubrica = 100) THEN
  
    IF (vTotalRestituido IS NULL) THEN vTotalRestituido := 0; END IF;
  
    RETURN vTotalRestituido;
  
  END IF;
  
  --Retorno do valor de décimo terceiro restituído.
  
  IF (pOperacao = 3 AND pCodRubrica = 3) THEN
  
    IF (vDecimoTerceiroRestituido IS NULL) THEN vDecimoTerceiroRestituido := 0; END IF;
  
    RETURN vDecimoTerceiroRestituido;
  
  END IF;
  
  --Retorno do valor de incidência de décimo terceiro restituído.
  
  IF (pOperacao = 3 AND pCodRubrica = 103) THEN
  
    IF (vIncidencia13Restituido IS NULL) THEN vIncidencia13Restituido := 0; END IF;
  
    RETURN vIncidencia13Restituido;
  
  END IF;
  
  --Retorno do valor total restituído de férias.
  
  IF (pOperacao = 3 AND pCodRubrica = 100) THEN
  
    IF (vTotalRestituido IS NULL) THEN vTotalRestituido := 0; END IF;
  
    RETURN vTotalRestituido;
  
  END IF;
  
  
  EXCEPTION WHEN OTHERS THEN
  
    RETURN -1;

END;
