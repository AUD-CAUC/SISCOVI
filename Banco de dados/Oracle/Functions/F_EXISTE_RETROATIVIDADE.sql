create or replace function "F_EXISTE_RETROATIVIDADE" (pCodContrato NUMBER, pCodFuncaoContrato NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER) RETURN BOOLEAN
IS

  --Função que retorna se em um determinado mês existe situação de retroatividade.
  --pOperacao = 1 - Retroatividade para remuneração.
  --pOperacao = 2 - Retroatividade para percentual do contrato ou estático.

  vRetroatividade NUMBER := 0;
  vRetroatividade2 NUMBER := 0;
  vDataReferencia DATE;
  
BEGIN

  --Definição da data referência como primeiro dia do m~es de acordo com os argumentos passados.
  
  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'); 

  --Caso de busca de retroatividade na remuneração.
    
  IF (pOperacao = 1) THEN

    --Verifica se o mês se encontra dentro de um período de retroatividade.

    SELECT COUNT(rr.cod)
      INTO vRetroatividade
      FROM tb_retroatividade_remuneracao rr
        JOIN tb_remuneracao_fun_con rfc ON rfc.cod = rr.cod_rem_funcao_contrato
      WHERE rfc.cod_funcao_contrato = pCodFuncaoContrato
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rr.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rr.fim);  
            
  END IF;

  --Caso de busca de retroatividade em percentuais.
  
  IF (pOperacao = 2) THEN
  
    SELECT COUNT(rp.cod)
      INTO vRetroatividade
      FROM tb_retroatividade_percentual rp
        JOIN tb_percentual_contrato pc ON pc.cod = rp.cod_percentual_contrato
      WHERE pc.cod_contrato = pCodContrato
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rp.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rp.fim);

    SELECT COUNT(rpe.cod)
      INTO vRetroatividade2
      FROM tb_retro_percentual_estatico rpe
      WHERE rpe.cod_contrato = pCodContrato
        AND TRUNC(vDataReferencia) >= TRUNC(LAST_DAY(ADD_MONTHS(rpe.inicio, -1)) + 1)
        AND TRUNC(vDataReferencia) <= TRUNC(rpe.fim);    
  
  END IF;

  IF (vRetroatividade > 0 OR vRetroatividade2 > 0) THEN

    RETURN TRUE;

  ELSE

    RETURN FALSE;

  END IF;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;
  
END;
