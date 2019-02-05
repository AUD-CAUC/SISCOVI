create or replace function "F_DIAS_TRABALHADOS_PERIOODO"(pCodFuncaoTerceirizado NUMBER, pDataInicio DATE, pDataFim DATE) RETURN NUMBER
IS

  --Função que retorna o número de dias que um terceirizado
  --trabalhou em uma função em um determinado mês.

  vDataInicio DATE;
  vDataFim DATE;
  vDiasTrabalhados NUMBER := 0;

BEGIN

  --Carregamento das datas de disponibilização e desligamento do terceirizado na função.

  SELECT data_inicio, 
         data_fim
    INTO vDataInicio,
	       vDataFim
    FROM tb_funcao_terceirizado
	WHERE cod = pCodFuncaoTerceirizado;
    
  --Caso não possua data de desligamento.  
   
  IF (vDataFim IS NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência então o
    --terceirizado trabalhou o período completo, a data
    --referência é sempre a data inicio argumento da função.
  
    IF (vDataInicio < pDataInicio) THEN
      
      vDiasTrabalhados := ((pDataFim - pDataInicio) + 1);
      
    END IF;
    
    --Se a data de disponibilização está no mês referência enão se verifica
    --a quantidade de dias trabalhados pelo terceirizado no período.
  
    IF (vDataInicio >= pDataInicio AND vDataInicio <= pDataFim) THEN
  
      vDiasTrabalhados := (pDataFim - vDataInicio) + 1;
    
    END IF;
 
  END IF;
  
  --Caso possua data de desligamento.
  
  IF (vDataFim IS NOT NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência e a data de 
    --desligamento é superior ao último dia do mês referência então o
    --terceirizado trabalhou os 30 dias.
  
    IF (vDataInicio < pDataInicio AND vDataFim > pDataFim) THEN
      
      vDiasTrabalhados := ((pDataFim - pDataInicio) + 1);
      
    END IF;  
    
    --Se a data de disponibilização está no mês referência e a data de
    --desligamento é superior mês referência, então se verifica a quantidade
    --de dias trabalhados pelo terceirizado.
  
    IF (vDataInicio >= pDataInicio 
        AND vDataInicio <= pDataFim
        AND vDataFim > pDataFim) THEN
    
      vDiasTrabalhados := (pDataFim - vDataInicio) + 1;
    
    END IF;
    
    --Se a data de disponibilização está no mês referência e também a data de
    --desligamento, então contam-se os dias trabalhados pelo terceirizado.
    
    IF (vDataInicio >= pDataInicio 
        AND vDataInicio <= pDataFim
        AND vDataFim >= pDataInicio
        AND vDataFim <= pDataFim) THEN
  
      vDiasTrabalhados := (vDataFim - vDataInicio) + 1;
    
    END IF;
    
    --Se a data da disponibilização for inferior ao mês de cálculo e 
    --o terceirizado tiver desligamento no mês referência, então contam-se
    --os dias trabalhados.
    
    IF (vDataInicio < pDataInicio 
        AND vDataFim >= pDataInicio
        AND vDataFim <= pDataFim) THEN
    
      vDiasTrabalhados := (vDataFim - pDataInicio) + 1;
    
    END IF;
 
  END IF;

  --Para o mês de fevereiro se equaliza o número de dias contados.

  IF (EXTRACT(MONTH FROM pDataFim) = 2) THEN
  
    --Se o mês for de 28 dias então soma-se 2 a contagem.

    IF (EXTRACT(DAY FROM pDataFim) = 28) THEN

      vDiasTrabalhados := vDiasTrabalhados + 2;

    ELSE

      --Se o mês não for de 28 dias ele é de 29.
      --Caso tenham-se contados 29 dias no mês de 
      --29 então soma-se 1a contagem.
      
        vDiasTrabalhados := vDiasTrabalhados + 1;

    END IF;

  END IF;

  RETURN vDiasTrabalhados;
  
  EXCEPTION WHEN OTHERS THEN

    RETURN NULL;  

END;