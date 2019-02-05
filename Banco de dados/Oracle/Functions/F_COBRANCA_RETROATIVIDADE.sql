create or replace function "F_COBRANCA_RETROATIVIDADE" (pCodContrato NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER) RETURN BOOLEAN
IS

  --Função que retorna se em um determinado mês
  --dever ser cobrada alguma retroatividade.
  
  --pOperacao = 1 - Retroatividade de remuneração.
  --pOperacao = 2 - Retroatividade de percentual.

  vRetroatividadeRemuneracao NUMBER := 0;
  vRetroatividadePercentual NUMBER := 0;
  vRetroPercentualEstatico NUMBER := 0;

BEGIN

  --Caso de busca de retroatividade na remuneração.

  IF (pOperacao = 1) THEN

    SELECT COUNT(rr.cod)
      INTO vRetroatividadeRemuneracao
      FROM tb_retroatividade_remuneracao rr
        JOIN tb_remuneracao_fun_con rcco ON rcco.cod = rr.cod_rem_funcao_contrato
        JOIN tb_funcao_contrato fc ON fc.cod = rcco.cod_funcao_contrato
      WHERE fc.cod_contrato = pCodContrato
        AND EXTRACT(month FROM data_cobranca) = pMes
        AND EXTRACT(year FROM data_cobranca) = pAno;

    IF (vRetroatividadeRemuneracao > 0) THEN

      RETURN TRUE;

    END IF;

  END IF;

  --Caso de busca de retroatividade nos percentuais.
  
  IF (pOperacao = 2) THEN
  
    SELECT COUNT(rpc.cod)
      INTO vRetroatividadePercentual
      FROM tb_retroatividade_percentual rpc
        JOIN tb_percentual_contrato pc ON pc.cod = rpc.cod_percentual_contrato
      WHERE pc.cod_contrato = pCodContrato
        AND EXTRACT(month FROM rpc.data_cobranca) = pMes
        AND EXTRACT(year FROM rpc.data_cobranca) = pAno;

    SELECT COUNT(rpe.cod)
      INTO vRetroPercentualEstatico
      FROM tb_retro_percentual_estatico rpe
      WHERE rpe.cod_contrato = pCodContrato
        AND EXTRACT(month FROM rpe.data_cobranca) = pMes
        AND EXTRACT(year FROM rpe.data_cobranca) = pAno;

    IF (vRetroatividadePercentual > 0 OR vRetroPercentualEstatico > 0) THEN

      RETURN TRUE;

    END IF;
 
  END IF;
 
  RETURN FALSE;  

END;
