create or replace function "F_RETORNA_PERCENTUAL_ANTERIOR" (pCodPercentual NUMBER) RETURN NUMBER
IS

--Retorna o código (cod) do percentual anterior ao cod do percentual passado.
--Entenda "passado" como referência.

  vCodPercentualAnterior NUMBER;
  vCodContrato NUMBER;
  vCodRubrica NUMBER;
  vDataReferencia DATE;
  vCount NUMBER := 0;
  
  Pragma Autonomous_Transaction;

BEGIN

  --Verificação de existência do percentual no contrato (para diferenciar dos percentuais estáticos).

  SELECT COUNT(cod)
    INTO vCount
    FROM tb_percentual_contrato
    WHERE cod = pCodPercentual;

  --Se vCount for maior que zero então o percentual existe no contrato.

  IF (vCount > 0) THEN

    --Define o contrato e a data referência com base no percentual referência.
  
    SELECT cod_contrato, 
           data_inicio, 
           cod_rubrica
      INTO vCodContrato, 
           vDataReferencia, 
           vCodRubrica
      FROM tb_percentual_contrato
      WHERE cod = pCodPercentual;
	
    --Seleciona o cod do percentual anterior com base na maior data de início
    --de percentual daquela rubrica, anterior ao percentual referência.
	
    SELECT cod
      INTO vCodPercentualAnterior
      FROM tb_percentual_contrato
      WHERE data_aditamento IS NOT NULL
        AND cod_contrato = vCodContrato
        AND cod_rubrica = vCodRubrica
        AND data_inicio = (SELECT MAX(data_inicio)
                             FROM tb_percentual_contrato
                             WHERE TO_DATE(TO_CHAR(data_inicio, 'dd/mm/yyyy'), 'dd/mm/yyyy') < TO_DATE(TO_CHAR(vDataReferencia, 'dd/mm/yyyy'), 'dd/mm/yyyy')
                               AND cod_contrato = vCodContrato
                               AND cod_rubrica = vCodRubrica
                               AND data_aditamento IS NOT NULL);

  ELSE

    --Define a data referência com base no percentual referência.
  
    SELECT data_inicio, 
           cod_rubrica
      INTO vDataReferencia, 
           vCodRubrica
      FROM tb_percentual_estatico
      WHERE cod = pCodPercentual;
	
    --Seleciona o cod do percentual anterior com base na maior data de início
    --de percentual daquela rubrica, anterior ao percentual referência.
	
    SELECT cod
      INTO vCodPercentualAnterior
      FROM tb_percentual_estatico
      WHERE data_aditamento IS NOT NULL
        AND cod_rubrica = vCodRubrica
        AND data_inicio = (SELECT MAX(data_inicio)
                             FROM tb_percentual_estatico
                             WHERE TO_DATE(TO_CHAR(data_inicio, 'dd/mm/yyyy'), 'dd/mm/yyyy') < TO_DATE(TO_CHAR(vDataReferencia, 'dd/mm/yyyy'), 'dd/mm/yyyy')
                               AND cod_rubrica = vCodRubrica
                               AND data_aditamento IS NOT NULL);

  END IF;                                

  RETURN vCodPercentualAnterior;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;

END;
