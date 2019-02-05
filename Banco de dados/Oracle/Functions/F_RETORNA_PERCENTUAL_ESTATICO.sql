create or replace function "F_RETORNA_PERCENTUAL_ESTATICO" (pCodContrato NUMBER, pCodRubrica NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER, pRetroatividade NUMBER) RETURN FLOAT
IS

  --Função que retorna o percentual estático em um período específico.

  vPercentual FLOAT := NULL;
  vCodPercentual NUMBER := NULL;
  vRubricaCheck NUMBER := 0;
  vRetroatividade BOOLEAN := FALSE;
  vRetroatividadePercentual NUMBER := 0;
  vDataReferencia DATE;

  vRubricaInexistente EXCEPTION;

  --pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual. 
  --pOperação = 2: Percentual encerrado do mês em que há dupla vigência.
  --pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
  --pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.

  --Legenda de rúbricas (usar esses códigos):
  --1 - Férias
  --2 - Terço constitucional
  --3 - Décimo terceiro
  --4 - FGTS
  --5 - Multa do FGTS
  --6 - Penalidade do FGTS
  --7 - Incidência do submódulo 4.1

BEGIN

  --Confere se o cod da rubrica passada existe.
  SELECT COUNT(DISTINCT(cod))
    INTO vRubricaCheck
    FROM tb_rubrica
    WHERE cod = pCodRubrica;

  IF (vRubricaCheck = 0) THEN

    RAISE vRubricaInexistente;

  END IF;

  --Definição da data referência.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'); 

  --Confere o status de retroatividade para o período.

  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(pCodContrato, NULL, pMes, pAno, 2);
    
  END IF;

  --Se o status de retroatividade para o período for TRUE
  --então ele verifica se a rubrica do argumento deu origem
  --a retroatividade.

  IF (vRetroatividade = TRUE) THEN
  
    SELECT COUNT(rpe.cod)
      INTO vRetroatividadePercentual
      FROM tb_retro_percentual_estatico rpe
        JOIN tb_percentual_estatico pe ON pe.cod = rpe.cod_percentual_estatico
      WHERE pe.cod_rubrica = pCodRubrica
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rpe.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rpe.fim);
        
    IF (vRetroatividadePercentual = 0) THEN
    
      vRetroatividade := FALSE;
    
    END IF;
  
  END IF;

  --Definição do percentual.
  --Busca realizada na tabela de percentuais estáticos.

  IF (pOperacao = 1) THEN

    SELECT pe.percentual, pe.cod
      INTO vPercentual, vCodPercentual
      FROM tb_percentual_estatico pe
      WHERE pe.cod_rubrica = pCodRubrica 
        AND pe.data_aditamento IS NOT NULL 
        AND TRUNC(pe.data_aditamento) <= TRUNC(SYSDATE)
        AND ((((TRUNC(pe.data_inicio) <= TRUNC(vDataReferencia)) 
	            AND
		       (TRUNC(pe.data_inicio) <= TRUNC(LAST_DAY(vDataReferencia)))) 
        AND (((TRUNC(pe.data_fim) >= TRUNC(vDataReferencia)) 
	  	       AND 
		      (TRUNC(pe.data_fim) >= TRUNC(LAST_DAY(vDataReferencia)))) 
		     OR pe.data_fim IS NULL)) 
             OR (EXTRACT(month FROM data_inicio) = EXTRACT(month FROM vDataReferencia)
             AND EXTRACT(year FROM data_inicio) = EXTRACT(year FROM vDataReferencia)));

  END IF;

  --Busca realizada na tabela de percentuais estáticos para percentual com data fim
  --no mês de referência.
  
  IF (pOperacao = 2) THEN

    SELECT pe.percentual
      INTO vPercentual 
      FROM tb_percentual_estatico pe
      WHERE pe.cod_rubrica = pCodRubrica 
        AND pe.data_aditamento IS NOT NULL 
        AND (EXTRACT(month FROM data_fim) = EXTRACT(month FROM vDataReferencia) 
             AND 
             EXTRACT(year FROM data_fim) = EXTRACT(year FROM vDataReferencia));

  END IF;

  --Em caso de consideração da retroatividade se retorna o percentual anterior.

  IF (pOperacao = 1 AND vRetroatividade = TRUE) THEN
  
    vCodPercentual := F_RETORNA_PERCENTUAL_ANTERIOR(vCodPercentual);
    
    SELECT percentual
      INTO vPercentual
      FROM tb_percentual_estatico
      WHERE cod = vCodPercentual;
  
  END IF;

  RETURN vPercentual;

  EXCEPTION 

    WHEN vRubricaInexistente THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução da função F_RETORNA_PERCENTUAL_CONTRATO: Código da rubrica é inexistente.');

    WHEN NO_DATA_FOUND THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução da função F_RETORNA_PERCENTUAL_CONTRATO: Nenhum dado encontrado.');

    WHEN OTHERS THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução da função F_RETORNA_PERCENTUAL_CONTRATO.');

END;
