create or replace function "F_EXISTE_MUDANCA_ESTATICO"(pCodContrato NUMBER, pMes NUMBER, pAno NUMBER, pRetroatividade NUMBER) RETURN BOOLEAN
IS
  
  --Função que retorna se em um dado mês existe ao menos um caso
  --de mudança de percentual estático que enseje mais de uma vigência.
  
  --pRetroatividade = 1 - Considera a retroatividade.
  --pRetroatividade = 2 - Desconsidera a retroatividade.
  
  vCount NUMBER := NULL;
  vRetroatividade BOOLEAN := FALSE;
 
BEGIN

  --Definição do modo de funcionamento da função.
  
  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(pCodContrato, NULL, pMes, pAno, 2);
  
  END IF;
  
  --Conta o número de percentuais da mesma rubrica vigentes no mês.
  
  SELECT COUNT(cod_rubrica)
    INTO vCount
    FROM (SELECT pe.cod_rubrica, COUNT(pe.cod) AS "CONTAGEM"
            FROM tb_percentual_estatico pe
            WHERE (((EXTRACT(month FROM pe.data_inicio) = pMes AND EXTRACT(year FROM pe.data_inicio) = pAno)
                   AND
                    (EXTRACT(month FROM data_aditamento) = pMes AND EXTRACT(year FROM data_aditamento) = pAno)
                   AND 
                    (TRUNC(data_aditamento) <= TRUNC(SYSDATE))) --Define a validade da convenção. 
                   OR
                   (EXTRACT(month FROM pe.data_fim) = pMes AND EXTRACT(year FROM pe.data_fim) = pAno))
            GROUP BY pe.cod_rubrica)
    WHERE CONTAGEM > 1;

  IF (vCount IS NOT NULL) THEN
  
    --Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO.
  
    IF (vCount > 0 AND vRetroatividade = FALSE) THEN

      RETURN TRUE;

    ELSE

      RETURN FALSE;

    END IF;

  END IF;

  RETURN FALSE;
  
  EXCEPTION 
  
    WHEN OTHERS THEN
  
    RAISE_APPLICATION_ERROR(-20003, 'Erro na execução da função F_MUNDANCA_PERCENTUAL_ESTATICO.');

END;
