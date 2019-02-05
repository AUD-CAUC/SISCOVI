create or replace function "F_EXISTE_DUPLA_CONVENCAO"(pCodFuncaoContrato NUMBER, pMes NUMBER, pAno NUMBER, pRetroatividade NUMBER) RETURN BOOLEAN
IS
  
  --Função que retorna se em um dado mês existe um caso de cálculo parcial
  --por existirem duas convenções vigentes no mesmo mês. 

  --pRetroatividade = 1 - Considera a retroatividade.
  --pRetroatividade = 2 - Desconsidera a retroatividade.
  
  vCount NUMBER;
  vCodContrato NUMBER;
  vRetroatividade BOOLEAN := FALSE;
 
BEGIN

  --Definição do modo de funcionamento da função.
  
  IF (pRetroatividade = 1) THEN
  
    vRetroatividade := F_EXISTE_RETROATIVIDADE(vCodContrato, pCodFuncaoContrato, pMes, pAno, 1);
  
  END IF;  

  --Define o código do contrato.
  
  SELECT cod_contrato
    INTO vCodContrato
    FROM tb_funcao_contrato
    WHERE cod = pCodFuncaoContrato;
  
  --Conta o número de convenções vigentes no mês.
  
  SELECT COUNT(cod)
    INTO vCount
    FROM tb_remuneracao_fun_con
    WHERE data_aditamento IS NOT NULL
      AND cod_funcao_contrato = pCodFuncaoContrato
      AND (((EXTRACT(month FROM data_inicio) = pMes AND EXTRACT(year FROM data_inicio) = pAno)
           AND
           (EXTRACT(month FROM data_aditamento) = pMes AND EXTRACT(year FROM data_aditamento) = pAno)
           AND 
           (TRUNC(data_aditamento) <= TRUNC(SYSDATE))) --Define a validade da convenção.
           OR
           ((EXTRACT(month FROM data_fim) = pMes AND EXTRACT(year FROM data_fim) = pAno)));

  IF (vCount IS NOT NULL) THEN
  
    --Se houverem duas convenções no mês passado retorna VERDADEIRO.
  
    IF (vCount = 2 AND vRetroatividade = FALSE) THEN

      RETURN TRUE;

    ELSE

      RETURN FALSE;

    END IF;

  END IF;

  RETURN FALSE;
  
  EXCEPTION WHEN OTHERS THEN
  
    RETURN NULL;

END;