create or replace function "F_EXISTE_DUPLO_PERCENTUAL"(pCodContrato NUMBER, pCodRubrica NUMBER, pMes NUMBER, pAno NUMBER, pRetroatividade NUMBER) RETURN BOOLEAN
IS
  
  --Função que retorna se em um dado mês existe um caso de cálculo parcial
  --por existirem dois percentuais da mesma rubrica vigentes no mesmo mês.
  
  --pRetroatividade = 1 - Considera a retroatividade.
  --pRetroatividade = 2 - Desconsidera a retroatividade.

  --Legenda de rúbricas:
  --1 - Férias
  --2 - Terço constitucional
  --3 - Décimo terceiro
  --4 - FGTS
  --5 - Multa do FGTS
  --6 - Penalidade do FGTS
  --7 - Incidência do submódulo 4.1
  
  vCount NUMBER := NULL;  --Percentual contrato.
  vCount2 NUMBER := NULL; -- Percentual estático.
  vRetroatividade BOOLEAN := FALSE;
 
BEGIN

  --Definição do modo de funcionamento da função.
  
  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(pCodContrato, NULL, pMes, pAno, 2);
  
  END IF;

  --Conta o número de percentuais da mesma rubrica vigentes no mês.
  
  SELECT COUNT(pc.cod)
    INTO vCount
    FROM tb_percentual_contrato pc
      JOIN tb_rubrica r ON r.cod = pc.cod_rubrica
    WHERE pc.cod_contrato = pCodContrato
      AND data_aditamento IS NOT NULL
      AND r.cod = pCodRubrica
      AND (((EXTRACT(month FROM pc.data_inicio) = pMes AND EXTRACT(year FROM pc.data_inicio) = pAno)
           AND
           (EXTRACT(month FROM data_aditamento) = pMes AND EXTRACT(year FROM data_aditamento) = pAno)
           AND 
           (TRUNC(data_aditamento) <= TRUNC(SYSDATE))) --Define a validade da convenção. 
           OR
           (EXTRACT(month FROM pc.data_fim) = pMes AND EXTRACT(year FROM pc.data_fim) = pAno));

  SELECT COUNT(pe.cod)
    INTO vCount2
    FROM tb_percentual_estatico pe
      JOIN tb_rubrica r ON r.cod = pe.cod_rubrica
    WHERE pe.data_aditamento IS NOT NULL
      AND r.cod = pCodRubrica
      AND (((EXTRACT(month FROM pe.data_inicio) = pMes AND EXTRACT(year FROM pe.data_inicio) = pAno)
           AND
           (EXTRACT(month FROM data_aditamento) = pMes AND EXTRACT(year FROM data_aditamento) = pAno)
           AND 
           (TRUNC(data_aditamento) <= TRUNC(SYSDATE))) --Define a validade da convenção. 
           OR
           (EXTRACT(month FROM pe.data_fim) = pMes AND EXTRACT(year FROM pe.data_fim) = pAno));
           
  IF (vCount IS NOT NULL OR vCount2 IS NOT NULL) THEN
  
    --Se houverem dois percentuais da mesma rubrica no mês passado retorna VERDADEIRO.
  
    IF ((vCount = 2 AND vRetroatividade = FALSE) OR (vCount2 = 2 AND vRetroatividade = FALSE)) THEN

      RETURN TRUE;

    ELSE

      RETURN FALSE;

    END IF;

  END IF;

  RETURN FALSE;
  
  EXCEPTION WHEN OTHERS THEN
  
    RETURN NULL;

END;
