create or replace function "F_DIAS_TRABALHADOS_MES"(pCodFuncaoTerceirizado NUMBER, pMes NUMBER, pAno NUMBER) RETURN NUMBER
IS

--Função que retorna o número de dias que um terceirizado
--trabalhou em determinado mês em uma função.

  vDataInicio DATE;
  vDataFim DATE;
  vDataReferencia DATE;
  vFimDoMes DATE;
  vContagemDeDias NUMBER := 0;

BEGIN

  --Data de referência e fim do mês definidas como o primeiro e o último dia
  --do mês correspondente aos argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');
  
  IF (pMes != 2) THEN

    vFimDoMes := TO_DATE('30/' || pMes || '/' || pAno, 'dd/mm/yyyy');

  ELSE

    vFimDoMes := LAST_DAY(vDataReferencia);

  END IF;
 
  --Carregamento das datas de disponibilização e desligamento do terceirizado.

  SELECT data_inicio, 
         data_fim
    INTO vDataInicio,
	       vDataFim
    FROM tb_funcao_terceirizado
	WHERE cod = pCodFuncaoTerceirizado;
    
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
  
    IF (vDataInicio >= vDataReferencia AND vDataInicio <= vFimDoMes) THEN

      vContagemDeDias := (vFimDoMes - vDataInicio) + 1;
    
    END IF;
 
  END IF;
  
  --Caso possua data de desligamento.
  
  IF (vDataFim IS NOT NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência e a data de 
    --desligamento é superior ao último dia do mês referência então o
    --terceirizado trabalhou os 30 dias.
  
    IF (vDataInicio < vDataReferencia AND vDataFim > vFimDoMes) THEN
      
      RETURN 30;
      
    END IF;  
    
    --Se a data de disponibilização está no mês referência e a data de
    --desligamento é superior mês referência, então se verifica a quantidade
    --de dias trabalhados pelo terceirizado.
  
    IF (vDataInicio >= vDataReferencia 
        AND vDataInicio <= vFimDoMes
        AND vDataFim > vFimDoMes) THEN
    
      vContagemDeDias := (vFimDoMes - vDataInicio) + 1;
    
    END IF;
    
    --Se a data de disponibilização está no mês referência e também a data de
    --desligamento, então contam-se os dias trabalhados pelo terceirizado.
    
    IF (vDataInicio >= vDataReferencia 
        AND vDataInicio <= vFimDoMes
        AND vDataFim >= vDataReferencia
        AND vDataFim <= vFimDoMes) THEN
  
      vContagemDeDias := (vDataFim - vDataInicio) + 1;
    
    END IF;
    
    --Se a data da disponibilização for inferior ao mês de cálculo e 
    --o terceirizado tiver desligamento no mês referência, então contam-se
    --os dias trabalhados.
    
    IF (vDataInicio < vDataReferencia 
        AND vDataFim >= vDataReferencia
        AND vDataFim <= vFimDoMes) THEN
    
      vContagemDeDias := (vDataFim - vDataReferencia) + 1;
    
    END IF;
 
  END IF;

  --Para o mês de fevereiro se equaliza o número de dias contados.

  IF (EXTRACT(MONTH FROM vFimDoMes) = 2) THEN
  
    --Se o mês for de 28 dias então soma-se 2 a contagem.

    IF (EXTRACT(DAY FROM vFimDoMes) = 28) THEN

      vContagemDeDias := vContagemDeDias + 2;

    ELSE

      --Se o mês não for de 28 dias ele é de 29.
      --Caso tenham-se contados 29 dias no mês de 
      --29 então soma-se 1a contagem.
      
        vContagemDeDias := vContagemDeDias + 1;

    END IF;

  END IF;

  RETURN vContagemDeDias;
  
  EXCEPTION WHEN OTHERS THEN

    RETURN NULL;  

END;
