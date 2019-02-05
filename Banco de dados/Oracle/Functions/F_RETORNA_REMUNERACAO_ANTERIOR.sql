create or replace function "F_RETORNA_REMUNERACAO_ANTERIOR" (pCodRemuneracao NUMBER) RETURN NUMBER
IS

--Retorna o código (cod) da remuneração anterior ao cod da remuneração passada.
--Entenda "passada" como referência.

  vCodRemuneracaoAnterior NUMBER;
  vCodFuncaoContrato NUMBER;
  vDataReferencia DATE;

BEGIN

  --Define o cargo e a data referência com base na convenção passada.
  
  SELECT cod_funcao_contrato, data_inicio
    INTO vCodFuncaoContrato, vDataReferencia 
    FROM tb_remuneracao_fun_con
    WHERE cod = pCodRemuneracao;
	
  --Seleciona o cod da conveção anterior com base na maior data de início
  --de conveção daquele cargo, anterior a convenção passada.
	
  SELECT cod
    INTO vCodRemuneracaoAnterior
    FROM tb_remuneracao_fun_con
    WHERE data_aditamento IS NOT NULL
      AND cod_funcao_contrato = vCodFuncaoContrato
      AND data_inicio = (SELECT MAX(data_inicio)
                           FROM tb_remuneracao_fun_con
                           WHERE TO_DATE(TO_CHAR(data_inicio, 'dd/mm/yyyy'), 'dd/mm/yyyy') < TO_DATE(TO_CHAR(vDataReferencia, 'dd/mm/yyyy'), 'dd/mm/yyyy')
                             AND cod_funcao_contrato = vCodFuncaoContrato
                             AND data_aditamento IS NOT NULL);

  RETURN vCodRemuneracaoAnterior;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;

END;
