create or replace function "F_FUNC_RETENCAO_INTEGRAL"(pCodFuncaoTerceirizado NUMBER, pMes NUMBER, pAno NUMBER) RETURN BOOLEAN
IS

--Função que retorna se um terceirizado trabalhou período integral (30 dias)
--ou não em um determinado mês.

  vDataInicio DATE;
  vDataFim DATE;
  vDataReferencia DATE;
  vCodTerceirizadoContrato NUMBER;
  vContagemDeDias NUMBER := 0;
  vFimDoMes DATE;

BEGIN

  --Data de referência e fim do mês definidas como o primeiro e o último dia
  --do mês correspondente aos argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');
  
  IF (pMes != 2) THEN

    vFimDoMes := TO_DATE('30/' || pMes || '/' || pAno, 'dd/mm/yyyy');

  ELSE

    vFimDoMes := LAST_DAY(vDataReferencia);

  END IF;

  --Carrega o cod_terceirizado_contrato.
 
  SELECT cod_terceirizado_contrato
    INTO vCodTerceirizadoContrato
    FROM tb_funcao_terceirizado
  	WHERE cod = pCodFuncaoTerceirizado;

  --Carregamento das datas de disponibilização e desligamento do terceirizado.

  IF (F_EXISTE_MUDANCA_FUNCAO (vCodTerceirizadoContrato, pMes, pAno) = FALSE) THEN

    SELECT data_inicio,
           data_fim
      INTO vDataInicio,
           vDataFim
      FROM tb_funcao_terceirizado ft
      WHERE ft.cod_terceirizado_contrato = vCodTerceirizadoContrato
      AND (((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'))
           AND 
           (ft.data_fim >= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy')))
           OR
           ((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy'))
            AND
            (ft.data_fim IS NULL)));

    --Caso não possua data de desligamento.  

    IF (vDataFim IS NULL) THEN
  
      --Se a data de disponibilização é inferior a data referência então o
      --funcionário trabalhou os 30 dias do mês referência.
  
      IF (vDataInicio < vDataReferencia) THEN
      
       RETURN TRUE;
      
      END IF;
    
      --Se a data de disponibilização está no mês referência então se verifica
      --a quantidade de dias trabalhados pelo funcionário.
  
      IF (vDataInicio >= vDataReferencia AND vDataInicio <= vFimDoMes) THEN

        vContagemDeDias := ((vFimDoMes - vDataInicio) + 1);
  
      END IF;
 
    END IF;
  
    --Caso possua data de desligamento.
  
    IF (vDataFim IS NOT NULL) THEN
  
      --Se a data de disponibilização é inferior a data referência e a data de 
      --desligamento é superior ao último dia do mês referência então o
      --funcionário trabalhou os 30 dias.
  
      IF (vDataInicio < vDataReferencia AND vDataFim > vFimDoMes) THEN
      
        RETURN TRUE;
      
      END IF;  
    
      --Se a data de disponibilização está no mês referência e a data de
      --desligamento é superior ao mês referência, então se verifica a quantidade
      --de dias trabalhados pelo funcionário.
  
      IF (vDataInicio >= vDataReferencia 
          AND vDataInicio <= vFimDoMes
          AND vDataFim > vFimDoMes) THEN
    
        vContagemDeDias := ((vFimDoMes - vDataInicio) + 1);
    
      END IF;
    
      --Se a data de disponibilização está no mês referência e também a data de
      --desligamento, então contam-se os dias trabalhados pelo funcionário.

      IF (vDataInicio >= vDataReferencia 
         AND vDataInicio <= vFimDoMes
         AND vDataFim >= vDataReferencia
         AND vDataFim <= vFimDoMes) THEN
    
        vContagemDeDias := ((vDataFim - vDataInicio) + 1);
    
      END IF;
    
      --Se a data da disponibilização for inferior ao mês de cálculo e 
      --o funcionário tiver desligamento no mês referência, então contam-se
      --os dias trabalhados.
    
      IF (vDataInicio < vDataReferencia 
          AND vDataFim >= vDataReferencia
          AND vDataFim <= vFimDoMes) THEN
    
        vContagemDeDias := ((vDataFim - vDataReferencia) + 1);
    
      END IF;
 
    END IF;

  ELSE

    DECLARE 

            --Cursor com todas as datas de início do mês referência.
    
            CURSOR d1 IS SELECT ft.data_inicio AS data_inicio
                           FROM tb_funcao_terceirizado ft
                           WHERE ft.cod_terceirizado_contrato = vCodTerceirizadoContrato
                             AND ((EXTRACT(MONTH FROM ft.data_inicio) = pMes
                                   AND
                                   EXTRACT (YEAR FROM ft.data_inicio) = pAno)
                                  OR
                                  (EXTRACT(MONTH FROM ft.data_fim) = pMes
                                   AND
                                   EXTRACT (YEAR FROM ft.data_fim) = pAno))
                         ORDER BY 1 ASC;

            --Cursor com todas as datas de fim do mês referência.

            CURSOR d2 IS SELECT ft.data_fim AS data_fim
                           FROM tb_funcao_terceirizado ft
                           WHERE ft.cod_terceirizado_contrato = vCodTerceirizadoContrato
                             AND ((EXTRACT(MONTH FROM ft.data_inicio) = pMes
                                   AND
                                   EXTRACT (YEAR FROM ft.data_inicio) = pAno)
                                  OR
                                  (EXTRACT(MONTH FROM ft.data_fim) = pMes
                                   AND
                                   EXTRACT (YEAR FROM ft.data_fim) = pAno))
                         ORDER BY 1 ASC;
                          
    BEGIN

      OPEN d1;
      OPEN d2;

      --Contagem dos dias trabalhados no mês.

      FOR i IN d1 LOOP

        vDataInicio := i.data_inicio;
         
        FETCH d2 INTO vDataFim;

        IF (vDataInicio < vDataReferencia) THEN

          vDataInicio := vDataReferencia;

        END IF;

        IF (vDataFim IS NULL OR vDataFim > vFimDoMes) THEN

          vDataFim := vFimDoMes;

        END IF;

        vContagemDeDias := vContagemDeDias + ((vDataFim - vDataInicio) + 1);

      END LOOP;
    
    END; 

  END IF;

  --Para o mês de fevereiro se equaliza o número de dias contados.

  IF (pMes = 2) THEN
  
    --Caso tenha-se contado mais de de 27 dias.

    IF (vContagemDeDias >= 28) THEN

      --Se o mês for de 28 dias então soma-se 2 a contagem.

      IF (EXTRACT(DAY FROM vFimDoMes) = 28) THEN

        vContagemDeDias := vContagemDeDias + 2;

      ELSE

        --Se o mês não for de 28 dias ele é de 29.
        --Caso tenham-se contados 29 dias no mês de 
        --29 então soma-se 1a contagem.

        IF (vContagemDeDias = 29) THEN
      
          vContagemDeDias := vContagemDeDias + 1;

        END IF;

      END IF;

    END IF;

  END IF;

  IF (vContagemDeDias >= 30) THEN

    RETURN TRUE;

  END IF;

  RETURN FALSE;  

END;
