create or replace function "F_RETORNA_PERCENTUAL_PERIODO" (pCodContrato NUMBER, pCodRubrica NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER, pRetroatividade NUMBER) RETURN FLOAT
IS

--O período passado deve ser exato, não podendo compreender
--aquele em que há dupla vigência.

  vPercentual FLOAT;
  vCodPercentual NUMBER;
  vRetroatividade BOOLEAN := FALSE;
  vDataReferencia DATE;
  vRetroatividadePercentual NUMBER := 0;
  vTipoPercentual NUMBER := 0;
  vValidadeRubrica NUMBER := 0;
  
  
  --pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual (contrato). 
  --pOperação = 2: Percentual encerrado do mês em que há dupla vigência (contrato).
  --pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
  --pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.
  
  --Legenda de rúbricas:
  --1 - Férias
  --2 - Terço constitucional
  --3 - Décimo terceiro
  --4 - FGTS
  --5 - Multa do FGTS
  --6 - Penalidade do FGTS
  --7 - Incidência do submódulo 4.1

BEGIN

  --Confirma a validade da rubrica passada.
  
  

  --Definição sobre a consideração da retroatividade.
  
  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(pCodContrato, NULL, pMes, pAno, 2);
    
  END IF;

  --Essa verificação determina se a rubrica passada pertence ao grupo de percentuais estáticos.

  SELECT COUNT(DISTINCT(pe.cod))
    INTO vTipoPercentual
    FROM tb_percentual_estatico pe
      JOIN tb_rubrica r ON r.cod = pe.cod_rubrica
    WHERE r.cod = pCodRubrica;
  
  --Definição da data referência.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'); 
  
  --Verificação de se a retroatividade está ligada ao percentual designado.
  
  IF (vRetroatividade = TRUE AND vTipoPercentual = 0) THEN
  
    SELECT COUNT(rp.cod)
      INTO vRetroatividadePercentual
      FROM tb_retroatividade_percentual rp
        JOIN tb_percentual_contrato pc ON pc.cod = rp.cod_percentual_contrato
        JOIN tb_rubrica r ON r.cod = pc.cod_rubrica
      WHERE pc.cod_contrato = pCodContrato
        AND r.cod = pCodRubrica
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rp.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rp.fim);
        
    IF (vRetroatividadePercentual = 0) THEN
    
      vRetroatividade := FALSE;
    
    END IF;
  
  END IF;

  IF (vRetroatividade = TRUE AND vTipoPercentual > 0) THEN
  
    SELECT COUNT(rpe.cod)
      INTO vRetroatividadePercentual
      FROM tb_retro_percentual_estatico rpe
        JOIN tb_percentual_estatico pe ON pe.cod = rpe.cod_percentual_estatico
        JOIN tb_rubrica r ON r.cod = pe.cod_rubrica
      WHERE rpe.cod_contrato = pCodContrato
        AND r.cod = pCodRubrica
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rpe.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rpe.fim);
        
    IF (vRetroatividadePercentual = 0) THEN
    
      vRetroatividade := FALSE;
    
    END IF;
  
  END IF;

  --Definição do percentual.

  --Busca realizada na tabela de percentuais do contrato.

  IF (pOperacao = 1 AND vTipoPercentual = 0) THEN

    SELECT pc.percentual, pc.cod
      INTO vPercentual, vCodPercentual
      FROM tb_percentual_contrato pc
        JOIN tb_rubrica r ON r.cod = pc.cod_rubrica
      WHERE pc.cod_contrato = pCodContrato --Contrato.
        AND r.cod = pCodRubrica --Rubrica.
        AND pc.data_aditamento IS NOT NULL --Aditamento.
        AND TRUNC(data_aditamento) <= TRUNC(SYSDATE)
        AND ((((TRUNC(pc.data_inicio) <= TRUNC(vDataReferencia)) --Início em mês anterior.
	         AND
		     (TRUNC(pc.data_inicio) <= TRUNC(LAST_DAY(vDataReferencia)))) --Início menor que o último dia do mês referência.
        AND (((TRUNC(pc.data_fim) >= TRUNC(vDataReferencia)) --Fim maior que a data referência.
	  	     AND 
		     (TRUNC(pc.data_fim) >= TRUNC(LAST_DAY(vDataReferencia)))) --Fim maior ou igual ao último dia do mês.
		      OR pc.data_fim IS NULL)) --Ou fim nulo.
             OR (EXTRACT(month FROM data_inicio) = EXTRACT(month FROM vDataReferencia) --Ou início no mês referência.
             AND EXTRACT(year FROM data_inicio) = EXTRACT(year FROM vDataReferencia)));

  END IF;

  --Busca realizada na tabela de percentuais estáticos.

  IF (pOperacao = 1 AND vTipoPercentual > 0) THEN

    SELECT pe.percentual, pe.cod
      INTO vPercentual, vCodPercentual
      FROM tb_percentual_estatico pe
        JOIN tb_rubrica r ON r.cod = pe.cod_rubrica
      WHERE r.cod = pCodRubrica --Rubrica.
        AND pe.data_aditamento IS NOT NULL --Aditamento.
        AND TRUNC(pe.data_aditamento) <= TRUNC(SYSDATE)
        AND ((((TRUNC(pe.data_inicio) <= TRUNC(vDataReferencia)) --Início em mês anterior.
	         AND
		     (TRUNC(pe.data_inicio) <= TRUNC(LAST_DAY(vDataReferencia)))) --Início menor que o último dia do mês referência.
        AND (((TRUNC(pe.data_fim) >= TRUNC(vDataReferencia)) --Fim maior que a data referência.
	  	     AND 
		     (TRUNC(pe.data_fim) >= TRUNC(LAST_DAY(vDataReferencia)))) --Fim maior ou igual ao último dia do mês.
		      OR pe.data_fim IS NULL)) --Ou fim nulo.
             OR (EXTRACT(month FROM pe.data_inicio) = EXTRACT(month FROM vDataReferencia) --Ou início no mês referência.
             AND EXTRACT(year FROM pe.data_inicio) = EXTRACT(year FROM vDataReferencia)));

  END IF;

  --Busca realizada na tabela de percentuais do contrato.
  
  IF (pOperacao = 2 AND vTipoPercentual = 0) THEN

    SELECT pc.percentual
      INTO vPercentual 
      FROM tb_percentual_contrato pc
        JOIN tb_rubrica r ON r.cod = pc.cod_rubrica
      WHERE pc.cod_contrato = pCodContrato --Contrato.
        AND r.cod = pCodRubrica --Rubrica.
        AND pc.data_aditamento IS NOT NULL --Aditamento.
        AND (EXTRACT(month FROM data_fim) = EXTRACT(month FROM vDataReferencia) --Fim no mês referência.
             AND EXTRACT(year FROM data_fim) = EXTRACT(year FROM vDataReferencia));

  END IF;

  --Busca realizada na tabela de percentuais estáticos.

  IF (pOperacao = 2 AND vTipoPercentual > 0) THEN

    SELECT pe.percentual
      INTO vPercentual 
      FROM tb_percentual_estatico pe
        JOIN tb_rubrica r ON r.cod = pe.cod_rubrica
      WHERE r.cod = pCodRubrica --Rubrica.
        AND pe.data_aditamento IS NOT NULL --Aditamento.
        AND (EXTRACT(month FROM pe.data_fim) = EXTRACT(month FROM vDataReferencia) --Fim no mês referência.
             AND EXTRACT(year FROM pe.data_fim) = EXTRACT(year FROM vDataReferencia));

  END IF;

  IF (pOperacao = 1 AND vRetroatividade = TRUE) THEN
  
    vCodPercentual := F_RETORNA_PERCENTUAL_ANTERIOR(vCodPercentual);
    
    SELECT percentual
      INTO vPercentual
      FROM tb_percentual_contrato
      WHERE cod = vCodPercentual;
  
  END IF;

  RETURN vPercentual;
  
  EXCEPTION WHEN NO_DATA_FOUND THEN
  /*
    IF(pOperacao = 2 AND pRetroatividade = 1) THEN
    
      vPercentual := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, pCodRubrica, pMes, pAno, 1, 1);
      
      RETURN vPercentual;
      
    END IF;
        
    IF (pOperacao = 1 AND pRetroatividade = 1) THEN
      
      vPercentual := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, pCodRubrica, pMes, pAno, 2, 1);
      
      RETURN vPercentual;
      
    END IF;
    
    IF(pOperacao = 2 AND pRetroatividade = 2) THEN
    
      vPercentual := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, pCodRubrica, pMes, pAno, 1, 2);
      
      RETURN vPercentual;
      
    END IF;
        
    IF (pOperacao = 1 AND pRetroatividade = 2) THEN
      
      vPercentual := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, pCodRubrica, pMes, pAno, 2, 2);
      
      RETURN vPercentual;
      
    ELSE
    
      RETURN NULL;
    
    END IF;

    */

    RETURN NULL;

END;