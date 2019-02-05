create or replace function "F_RET_NUMERO_DIAS_MES_PARCIAL" (pCodFuncaoContrato NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER) RETURN NUMBER
IS

  --Função que retorna o número de dias trabalhados
  --correspondentes a metade de um percentual ou 
  --remuneração em um mês de mudança.
  --Ex.: 13 dias primeirametade rem. e 17 dias segunda
  --metada da rem..

  vRetorno NUMBER;
  vDataReferencia DATE;
  vDataFimPercentual DATE;
  vDataInicioPercentual DATE;
  vCodContrato NUMBER;
  
  --Operação 1: Primeira metade da remuneração.
  --Operação 2: Segunda metade da remuneração.
  --Operação 3: Primeira metade do percentual.
  --Operação 4: Segunda metade do percentual.

BEGIN

  --Definição da data referência como primeiro dia do mês de acordo com os argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');

  --Carrega o código do contrato.
  
  SELECT cod_contrato
    INTO vCodContrato
    FROM tb_funcao_contrato
    WHERE cod = pCodFuncaoContrato;
  
  --Primeira metade da remuneração.
   
  IF (pOperacao = 1) THEN
  
    SELECT (data_fim - vDataReferencia) + 1
      INTO vRetorno
      FROM tb_remuneracao_fun_con
      WHERE data_aditamento IS NOT NULL
        AND cod_funcao_contrato = pCodFuncaoContrato
        AND EXTRACT(month FROM data_fim) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM data_fim) = EXTRACT(year FROM vDataReferencia);
        
  END IF;
  
  --Segunda metade da remuneração.
  
  IF (pOperacao = 2) THEN
  
    SELECT (LAST_DAY(vDataReferencia) - data_inicio) + 1
      INTO vRetorno
      FROM tb_remuneracao_fun_con
      WHERE data_aditamento IS NOT NULL
        AND cod_funcao_contrato = pCodFuncaoContrato
        AND EXTRACT(month FROM data_inicio) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM data_inicio) = EXTRACT(year FROM vDataReferencia);
        
    IF (EXTRACT(day FROM LAST_DAY(vDataReferencia)) = 31) THEN
  
      vRetorno := vRetorno - 1;
  
    END IF;
    
  END IF;
  
  --Primeira metade do percentual.
    
  IF (pOperacao = 3) THEN
  
    SELECT MIN(pc.data_fim)
      INTO vDataFimPercentual
      FROM tb_percentual_contrato pc
      WHERE cod_contrato = vCodContrato
        AND pc.data_aditamento IS NOT NULL        
        AND EXTRACT(month FROM pc.data_fim) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM pc.data_fim) = EXTRACT(year FROM vDataReferencia);
        
    vRetorno := (vDataFimPercentual - vDataReferencia) + 1;
        
  END IF;
  
  --Segunda metade do percentual.
  
  IF (pOperacao = 4) THEN
  
    SELECT MAX(pc.data_inicio)
      INTO vDataInicioPercentual
      FROM tb_percentual_contrato pc
      WHERE cod_contrato = vCodContrato
        AND pc.data_aditamento IS NOT NULL
        AND EXTRACT(month FROM pc.data_inicio) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM pc.data_inicio) = EXTRACT(year FROM vDataReferencia);
        
    vRetorno := (LAST_DAY(vDataReferencia) - vDataInicioPercentual) + 1;
        
    IF (EXTRACT(day FROM LAST_DAY(vDataReferencia)) = 31) THEN
  
      vRetorno := vRetorno - 1;
  
    END IF;
        
  END IF;

  RETURN vRetorno;

END;
