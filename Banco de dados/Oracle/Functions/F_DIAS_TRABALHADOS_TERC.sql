create or replace function "F_DIAS_TRABALHADOS_TERC"(pCodTerceirizadoContrato NUMBER, pMes NUMBER, pAno NUMBER) RETURN NUMBER
IS

--Função que retorna o número de dias que um terceirizado
--trabalhou em determinado mês.

  vDataInicio DATE;
  vDataFim DATE;
  vDataReferencia DATE;

BEGIN

  --Data de referência definida como o primeiro dia do mês correspondente aos argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');
 
  --Carregamento das datas de disponibilização e desligamento do terceirizado.

  IF (F_EXISTE_MUDANCA_FUNCAO (pCodTerceirizadoContrato, pMes, pAno) = FALSE) THEN

    SELECT data_inicio,
           data_fim
      INTO vDataInicio,
           vDataFim
      FROM tb_funcao_terceirizado ft
      WHERE ft.cod_terceirizado_contrato = pCodTerceirizadoContrato
      AND (((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'))
           AND 
           (ft.data_fim >= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy')))
           OR
           ((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'))
            AND
            (ft.data_fim IS NULL)));

    ELSE

      SELECT data_inicio
        INTO vDataInicio     
        FROM tb_funcao_terceirizado ft
        WHERE ft.cod_terceirizado_contrato = pCodTerceirizadoContrato
          AND data_fim = (SELECT MIN(data_fim)
                            FROM tb_funcao_terceirizado
                            WHERE cod_terceirizado_contrato = pCodTerceirizadoContrato
                              AND EXTRACT(month FROM data_fim) = pMes
                              AND EXTRACT(year FROM data_fim) = pAno);

      SELECT data_fim
        INTO vDataFim     
        FROM tb_funcao_terceirizado ft
        WHERE ft.cod_terceirizado_contrato = pCodTerceirizadoContrato
          AND data_inicio = (SELECT MAX(data_inicio)
                               FROM tb_funcao_terceirizado
                               WHERE cod_terceirizado_contrato = pCodTerceirizadoContrato
                                 AND EXTRACT(month FROM data_inicio) = pMes
                                 AND EXTRACT(year FROM data_inicio) = pAno);

    END IF;
    
  --Caso não possua data de desligamento.  
   
  IF (vDataFim IS NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência então o
    --terceirizado trabalhou os 30 dias do mês referência pois, a data
    --referência é sempre o primeiro dia do mês.
  
    IF (vDataInicio < vDataReferencia) THEN
      
      RETURN 30;
      
    END IF;
    
    --Se a data de disponibilização está no mês referência enão se verifica
    --a quantidade de dias trabalhados pelo terceirizado.
  
    IF (vDataInicio >= vDataReferencia AND vDataInicio <= LAST_DAY(vDataReferencia)) THEN
  
      RETURN (LAST_DAY(vDataInicio) - vDataInicio) + 1;
    
    END IF;
 
  END IF;
  
  --Caso possua data de desligamento.
  
  IF (vDataFim IS NOT NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência e a data de 
    --desligamento é superior ao último dia do mês referência então o
    --terceirizado trabalhou os 30 dias.
  
    IF (vDataInicio < vDataReferencia AND vDataFim > LAST_DAY(vDataReferencia)) THEN
      
      RETURN 30;
      
    END IF;  
    
    --Se a data de disponibilização está no mês referência e a data de
    --desligamento é superior mês referência, então se verifica a quantidade
    --de dias trabalhados pelo terceirizado.
  
    IF (vDataInicio >= vDataReferencia 
        AND vDataInicio <= LAST_DAY(vDataReferencia)
        AND vDataFim > LAST_DAY(vDataReferencia)) THEN
    
      RETURN (LAST_DAY(vDataInicio) - vDataInicio) + 1;
    
    END IF;
    
    --Se a data de disponibilização está no mês referência e também a data de
    --desligamento, então contam-se os dias trabalhados pelo terceirizado.
    
    IF (vDataInicio >= vDataReferencia 
        AND vDataInicio <= LAST_DAY(vDataReferencia)
        AND vDataFim >= vDataReferencia
        AND vDataFim <= LAST_DAY(vDataReferencia)) THEN
  
      RETURN (vDataFim - vDataInicio) + 1;
    
    END IF;
    
    --Se a data da disponibilização for inferior ao mês de cálculo e 
    --o terceirizado tiver desligamento no mês referência, então contam-se
    --os dias trabalhados.
    
    IF (vDataInicio < vDataReferencia 
        AND vDataFim >= vDataReferencia
        AND vDataFim <= LAST_DAY(vDataReferencia)) THEN
    
      RETURN (vDataFim - vDataReferencia) + 1;
    
    END IF;
 
  END IF;
  
  EXCEPTION 
  
    WHEN NO_DATA_FOUND THEN

      RETURN NULL;  
      
    WHEN OTHERS THEN

      RETURN NULL;

END;
