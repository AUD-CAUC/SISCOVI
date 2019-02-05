create or replace function "F_RET_REMUNERACAO_PERIODO"(pCodFuncaoContrato NUMBER, pDataInicio DATE, pDataFim DATE, pRetroatividade NUMBER) RETURN FLOAT
IS

--Função que recupera o valor da remuneração vigente para uma função de um
--contrato em um determinado perído de vigência.

  vRemuneracao FLOAT := 0;
  vDataReferencia DATE;
  vCodContrato NUMBER;
  vCodRemuneracaoFunCon NUMBER;
  vRetroatividade BOOLEAN := FALSE;
  vDataAditamento DATE;

  --Operação 1: Remuneração do mês em que não há dupla vigência ou remuneração atual. 
  --Operação 2: Remuneração encerrada do mês em que há dupla vigência.
  --pRetroatividade 1: Considera a retroatividade.
  --pRetroatividade 2: Desconsidera os períodos de retroatividade.

BEGIN

  --Definição do cod_contrato.
  
  SELECT cod_contrato
    INTO vCodContrato
    FROM tb_funcao_contrato
    WHERE cod = pCodFuncaoContrato;   

  --Definição da remuneração.
  
  SELECT cod,
         remuneracao
    INTO vCodRemuneracaoFunCon,
         vRemuneracao
    FROM tb_remuneracao_fun_con
    WHERE data_aditamento IS NOT NULL
      AND cod_funcao_contrato = pCodFuncaoContrato
      AND data_inicio <= pDataInicio
      AND (data_fim >= pDataFim
           OR
           data_fim IS NULL);
/*  
  IF (pOperacao = 1 AND vRetroatividade = TRUE) THEN
  
    vCodRemuneracaoFunCon := F_RETORNA_REMUNERACAO_ANTERIOR(vCodRemuneracaoFunCon);
    
    SELECT remuneracao
      INTO vRemuneracao
      FROM tb_remuneracao_fun_con
      WHERE cod = vCodRemuneracaoFunCon;
  
  END IF;
*/
  RETURN vRemuneracao;
  
  EXCEPTION 
  
    WHEN NO_DATA_FOUND THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução da função F_RET_REMUNERACAO_PERIODO: Nenhum dado encontrado.');

    WHEN OTHERS THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução da função F_RET_REMUNERACAO_PERIODO.');

END;