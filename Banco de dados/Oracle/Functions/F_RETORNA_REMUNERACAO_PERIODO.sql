create or replace function "F_RETORNA_REMUNERACAO_PERIODO"(pCodFuncaoContrato NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER, pRetroatividade NUMBER) RETURN FLOAT
IS

--Função que recupera o valor da remuneração vigente para o cargo de um
--contrato em um determinado perído com vigência de mais de uma convenção.

  vRemuneracao FLOAT := 0;
  vDataReferencia DATE;
  vCodContrato NUMBER;
  vCodRemuneracaoCargoContrato NUMBER;
  vRetroatividade BOOLEAN := FALSE;
  vDataAditamento DATE;
  

  --Operação 1: Remuneração do mês em que há somente uma remuneração ou remuneração atual. 
  --Operação 2: Remuneração encerrada do mês em que há mais de uma remuneração.
  --pRetroatividade 1: Considera a retroatividade.
  --pRetroatividade 2: Desconsidera os períodos de retroatividade.

BEGIN

  --Definição do cod_contrato.
  
  SELECT cod_contrato
    INTO vCodContrato
    FROM tb_funcao_contrato
    WHERE cod = pCodFuncaoContrato;   

  --Definição sobre a consideração da retroatividade.
  
  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(vCodContrato, pCodFuncaoContrato, pMes, pAno, 1);
    
  END IF;

  --Definição da data referência.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'); 

  --Definição do percentual.

  IF (pOperacao = 1) THEN

    SELECT remuneracao, cod, data_aditamento
      INTO vRemuneracao, vCodRemuneracaoCargoContrato, vDataAditamento
      FROM tb_remuneracao_fun_con 
      WHERE cod_funcao_contrato = pCodFuncaoContrato 
	      AND data_aditamento IS NOT NULL
        AND TRUNC(data_aditamento) <= TRUNC(SYSDATE)
        AND (((((TRUNC(data_inicio) <= TRUNC(vDataReferencia))
	           AND
	  	       (TRUNC(data_inicio) <= TRUNC(LAST_DAY(vDataReferencia)))))
             AND( 
             ((((TRUNC(data_fim) >= TRUNC(vDataReferencia))
		         AND 
		         (TRUNC(data_fim) >= TRUNC(LAST_DAY(vDataReferencia))))
		         OR data_fim IS NULL)))
             OR (EXTRACT(month FROM data_inicio) = EXTRACT(month FROM vDataReferencia) --Ou início no mês referência.
               AND EXTRACT(year FROM data_inicio) = EXTRACT(year FROM vDataReferencia))));
     
  END IF;
  
  IF (pOperacao = 2) THEN

    SELECT remuneracao 
      INTO vRemuneracao
      FROM tb_remuneracao_fun_con 
      WHERE cod_funcao_contrato = pCodFuncaoContrato 
	    AND data_aditamento IS NOT NULL
        AND (EXTRACT(month FROM data_fim) = EXTRACT(month FROM vDataReferencia) --Ou início no mês referência.
             AND EXTRACT(year FROM data_fim) = EXTRACT(year FROM vDataReferencia));

  END IF;
  
  
  
  IF (pOperacao = 1 AND vRetroatividade = TRUE) THEN
  
    vCodRemuneracaoCargoContrato := F_RETORNA_REMUNERACAO_ANTERIOR(vCodRemuneracaoCargoContrato);
    
    SELECT remuneracao
      INTO vRemuneracao
      FROM tb_remuneracao_fun_con
      WHERE cod = vCodRemuneracaoCargoContrato;
  
  END IF;

  RETURN vRemuneracao;
  
  EXCEPTION WHEN NO_DATA_FOUND THEN
  
    IF (pOperacao = 1 AND pRetroatividade = 1) THEN
    
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(pCodFuncaoContrato, pMes, pAno, 2, 1);
      
      RETURN vRemuneracao;
      
    END IF;
    
    IF (pOperacao = 1 AND pRetroatividade = 2) THEN
    
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(pCodFuncaoContrato, pMes, pAno, 2, 2);
      
      RETURN vRemuneracao;
    
    ELSE
    
      RETURN NULL;
      
    END IF;  

END;