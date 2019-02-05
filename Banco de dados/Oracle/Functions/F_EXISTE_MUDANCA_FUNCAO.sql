create or replace function "F_EXISTE_MUDANCA_FUNCAO" (pCodTerceirizadoContrato NUMBER, pMes NUMBER, pAno NUMBER) RETURN BOOLEAN
IS

  --Define se um terceirizado teve alterações em seu cargo em um determinado mês.

  vDataReferencia DATE := NULL;
  vNumeroRegistros NUMBER := NULL;
  vRetorno BOOLEAN := FALSE;

BEGIN

  --Definição da data referência como primeiro dia do m~es de acordo com os argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pANo, 'dd/mm/yyyy');

  --Contagem do número de cargos ocupados por um determinado terceirizado no mês referência.

  SELECT COUNT(cod)
    INTO vNumeroRegistros
    FROM tb_funcao_terceirizado
    WHERE cod_terceirizado_contrato = pCodTerceirizadoContrato
      AND ((EXTRACT(month FROM data_inicio) = pMes AND EXTRACT(year FROM data_inicio) = pAno)
           OR
           (EXTRACT(month FROM data_fim) = pMes AND EXTRACT(year FROM data_fim) = pAno));

  IF (vNumeroRegistros IS NOT NULL AND vNumeroRegistros > 1) THEN

    vRetorno := TRUE;
  
  ELSE

    vRetorno := FALSE;

  END IF;

  RETURN vRetorno;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    RETURN NULL;

END;
