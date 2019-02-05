create or replace function "F_RET_PERCENTUAL_ESTATICO" (pCodContrato NUMBER, pCodRubrica NUMBER, pDataInicio DATE, pDataFim DATE, pRetroatividade NUMBER) RETURN FLOAT
IS

  --Função que retorna o percentual de um contrato em um período específico.

  vPercentual FLOAT := NULL;
  vCodPercentual NUMBER := NULL;
  vRubricaCheck NUMBER := 0;
  vRetroatividade BOOLEAN := FALSE;
  vRetroatividadePercentual NUMBER := 0;
  vDataReferencia DATE;
  vAno NUMBER;
  vMes NUMBER;

  vRubricaInexistente EXCEPTION;

  --pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual (contrato). 
  --pOperação = 2: Percentual encerrado do mês em que há dupla vigência (contrato).
  --pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
  --pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.

  --Legenda de rúbricas (usar esses códigos):
  --1 - Férias (percentual contrato)
  --2 - Terço constitucional (percentual contrato)
  --3 - Décimo terceiro (percentual contrato)
  --4 - FGTS (percentual estático)
  --5 - Multa do FGTS (percentual estático)
  --6 - Penalidade do FGTS (percentual estático)
  --7 - Incidência do submódulo 4.1 (percentual contrato)

BEGIN

  --Confere se o cod da rubrica passada existe.
  SELECT COUNT(DISTINCT(pe.cod_rubrica))
    INTO vRubricaCheck
    FROM tb_percentual_estatico pe
    WHERE pe.cod_rubrica = pCodRubrica;

  IF (vRubricaCheck = 0) THEN

    RAISE vRubricaInexistente;

  END IF;
/*
  --Definição da data referência.

  vMes := EXTRACT(month FROM pDataInicio);
  vAno := EXTRACT(month FROM pDataFim);

  vDataReferencia := TO_DATE('01/' || vMes || '/' || vAno, 'dd/mm/yyyy'); 

  --Confere o status de retroatividade para o período.

  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(pCodContrato, NULL, vMes, vAno, 2);
    
  END IF;

  --Se o status de retroatividade para o período for TRUE
  --então ele verifica se a rubrica do argumento deu origem
  --a retroatividade.

  IF (vRetroatividade = TRUE) THEN
  
    SELECT COUNT(rp.cod)
      INTO vRetroatividadePercentual
      FROM tb_retroatividade_percentual rp
        JOIN tb_percentual_contrato pc ON pc.cod = rp.cod_percentual_contrato
      WHERE pc.cod_contrato = pCodContrato
        AND pc.cod_rubrica = pCodRubrica
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rp.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rp.fim);
        
    IF (vRetroatividadePercentual = 0) THEN
    
      vRetroatividade := FALSE;
    
    END IF;
  
  END IF;
*/
  --Definição do percentual.
  --Busca realizada na tabela de percentuais do contrato.

  SELECT cod,
         percentual
    INTO vCodPercentual,
         vPercentual
    FROM tb_percentual_estatico
    WHERE data_aditamento IS NOT NULL
      AND cod_rubrica = pCodRubrica
      AND data_inicio <= pDataInicio
      AND (data_fim >= pDataFim
           OR
           data_fim IS NULL);
/*
  --Em caso de consideração da retroatividade se retorna o percentual anterior.

  IF (pOperacao = 1 AND vRetroatividade = TRUE) THEN
  
    vCodPercentual := F_RETORNA_PERCENTUAL_ANTERIOR(vCodPercentual);
    
    SELECT percentual
      INTO vPercentual
      FROM tb_percentual_contrato
      WHERE cod = vCodPercentual;
  
  END IF;
*/
  RETURN vPercentual;

  EXCEPTION 

    WHEN vRubricaInexistente THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução da função F_RET_PERCENTUAL_ESTATICO: Código da rubrica é inexistente.');

    WHEN NO_DATA_FOUND THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução da função F_RET_PERCENTUAL_ESTATICO: Nenhum dado encontrado.');

    WHEN OTHERS THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução da função F_RET_PERCENTUAL_ESTATICO.');

END;
