create or replace function "F_DIAS_TRABALHADOS_MES_PARCIAL"(pCodFuncaoContrato NUMBER, pCodFuncaoTerceirizado NUMBER, pMes NUMBER, pAno NUMBER, pOperacao NUMBER) RETURN NUMBER
IS

--Função que retorna o número de dias que um terceirizado
--trabalhou em determinado período do mês em um contrato
--que passou por alterações na remuneração ou nos percentuais.

  vRetorno NUMBER := 0;
  vDataReferencia DATE;
  vDataInicio DATE;
  vDataFim DATE;
  vCodContrato NUMBER;
  vDataInicioFuncao DATE;
  vDataFimFuncao DATE;
  
  --Operação 1: Primeira metade da remuneração.
  --Operação 2: Segunda metade da remuneração.
  --Operação 3: Primeira metade do percentual.
  --Operação 4: Segunda metade do percentual.

BEGIN

  --Definição da data de referência como primeiro dia do mês de acordo com os argumentos passados.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');

  --Carregamento do código do contrato.
  
  SELECT cod_contrato
    INTO vCodContrato
    FROM tb_funcao_contrato
    WHERE cod = pCodFuncaoContrato;

  --Carregamento das datas de disponibilização e desligamento do terceirizado.

  SELECT data_inicio, 
         data_fim
    INTO vDataInicioFuncao,
	       vDataFimFuncao
    FROM tb_funcao_terceirizado
	WHERE cod = pCodFuncaoTerceirizado;
  
  --Primeira metade da remuneração (a remuneração anterior tem data fim naquele mês).
   
  IF (pOperacao = 1) THEN
  
    SELECT data_fim,
           vDataReferencia
      INTO vDataFim,
           vDataInicio
      FROM tb_remuneracao_fun_con
      WHERE data_aditamento IS NOT NULL
        AND cod_funcao_contrato = pCodFuncaoContrato
        AND EXTRACT(month FROM data_fim) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM data_fim) = EXTRACT(year FROM vDataReferencia);
        
  END IF;
  
  --Segunda metade da remuneração (a remuneração mais recente tem data inicio naquele mês).
  
  IF (pOperacao = 2) THEN
  
    SELECT LAST_DAY(vDataReferencia),
           MAX(data_inicio)
      INTO vDataFim,
           vDataInicio
      FROM tb_remuneracao_fun_con
      WHERE data_aditamento IS NOT NULL
        AND cod_funcao_contrato = pCodFuncaoContrato
        AND EXTRACT(month FROM data_inicio) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM data_inicio) = EXTRACT(year FROM vDataReferencia);

    IF (EXTRACT(day FROM LAST_DAY(vDataFim)) = 31) THEN
  
      vDataFim := vDataFim - 1;
  
    END IF;
             
  END IF;
  
  --Primeira metade do percentual (último percentual não tem data fim).
    
  IF (pOperacao = 3) THEN
  
    SELECT MIN(pc.data_fim),
           vDataReferencia
      INTO vDataFim,
           vDataInicio
      FROM tb_percentual_contrato pc
      WHERE cod_contrato = vCodContrato
        AND pc.data_aditamento IS NOT NULL        
        AND EXTRACT(month FROM pc.data_fim) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM pc.data_fim) = EXTRACT(year FROM vDataReferencia);
        
  END IF;
  
  --Segunda metade do percentual.
  
  IF (pOperacao = 4) THEN
  
    SELECT MAX(pc.data_inicio),
           LAST_DAY(vDataReferencia)
      INTO vDataInicio,
           vDataFim
      FROM tb_percentual_contrato pc
      WHERE cod_contrato = vCodContrato
        AND pc.data_aditamento IS NOT NULL
        AND EXTRACT(month FROM pc.data_inicio) = EXTRACT(month FROM vDataReferencia)
        AND EXTRACT(year FROM pc.data_inicio) = EXTRACT(year FROM vDataReferencia);

    --Ajuste do último dia para que o mês contenha apenas 30 dias.

    IF (EXTRACT(day FROM LAST_DAY(vDataFim)) = 31) THEN
  
      vDataFim := vDataFim - 1;
  
    END IF;
        
  END IF;

  --Definição do número de dias trabalhados para o caso de primeira metade do mês.

  IF (pOperacao IN (1,3)) THEN

    --Se a data de desligamento é nula.

    IF (vDataFimFuncao IS NULL) THEN

      --Se a disponibilização é inferior a data referência.

      IF (vDataInicioFuncao < vDataReferencia) THEN
      
        vRetorno := (vDataFim - vDataReferencia) + 1;
      
      END IF;

      --Se a data de disponilização está dentro do período.

      IF (vDataInicioFuncao >= vDataReferencia AND vDataInicioFuncao <= vDataFim) THEN
  
        vRetorno := (vDataFim - vDataInicioFuncao) + 1;
    
      END IF;

    END IF;

    --Caso exista uma data de desligamento do terceirizado.

    IF (vDataFimFuncao IS NOT NULL) THEN
  
    --Se a data de disponibilização é inferior à data referência e a data de 
    --desligamento é superior ao último dia do mês referência então o
    --terceirizado trabalhou os dias entre o inicio do mês e a data fim.
  
    IF (vDataInicioFuncao < vDataReferencia AND vDataFimFuncao > vDataFim) THEN
      
      vRetorno := (vDataFim - vDataReferencia) + 1;
      
    END IF;  
    
    --Se a data de disponibilização está no mês referência e a data de
    --desligamento é superior mês referência, então se verifica a quantidade
    --de dias trabalhados pelo terceirizado entre a data fim e a disponibilização.
  
    IF (vDataInicioFuncao >= vDataReferencia 
        AND vDataInicioFuncao <= vDataFim
        AND vDataFimFuncao > vDataFim) THEN
    
      vRetorno := (vDataFim - vDataInicioFuncao) + 1;
    
    END IF;
    
    --Se a data de disponibilização está na primeira metade do mês referência 
    --e também a data de desligamento, então contam-se os dias trabalhados 
    --pelo terceirizado entre o desligamento e a disponibilização.
    
    IF (vDataInicioFuncao >= vDataReferencia 
        AND vDataInicioFuncao <= vDataFim
        AND vDataFimFuncao >= vDataReferencia
        AND vDataFimFuncao <= vDataFim) THEN
  
      vRetorno := (vDataFimFuncao - vDataInicioFuncao) + 1;
    
    END IF;
    
    --Se a data da disponibilização for inferior ao mês de cálculo e 
    --o terceirizado tiver desligamento antes da data fim, então contam-se
    --os dias trabalhados nesse período.
    
    IF (vDataInicioFuncao < vDataReferencia 
        AND vDataFimFuncao >= vDataReferencia
        AND vDataFimFuncao <= vDataFim) THEN
    
      vRetorno := (vDataFimFuncao - vDataReferencia) + 1;
    
    END IF;
 
  END IF;

  END IF;

  --Cálculo para a segunda metade do mês.

  IF (pOperacao IN (2,4)) THEN

  --Se o terceirizado não possui data de desligamento.

  IF (vDataFimFuncao IS NULL) THEN

    --Se a disponibilização é inferior a data referência.

    IF (vDataInicioFuncao < vDataReferencia) THEN
      
      vRetorno := (vDataFim - vDataInicio) + 1;
      
    END IF;

    --Caso a disponibilização esteja dentro do período.

    IF (vDataInicioFuncao >= vDataInicio AND vDataInicioFuncao <= vDataFim) THEN
  
      vRetorno := (vDataFim - vDataInicioFuncao) + 1;
    
    END IF;

  END IF;

  --Se o terceirizado possuir data de desligamento.

  IF (vDataFimFuncao IS NOT NULL) THEN
  
    --Se a data de disponibilização é inferior a data referência e a data de 
    --desligamento é superior ao último dia do mês referência então o
    --terceirizado trabalhou os dias entre o início e o fim.
  
    IF (vDataInicioFuncao < vDataReferencia AND vDataFimFuncao > vDataFim) THEN
      
      vRetorno := (vDataFim - vDataInicio) + 1;
      
    END IF;  
    
    --Se a data de disponibilização é maior que a data de inicio
    --e a data de desligamento superior a data de fim então
    --conta-se o período entre data fim e data de disponibilização.
  
    IF (vDataInicioFuncao >= vDataInicio 
        AND vDataInicioFuncao <= vDataFim
        AND vDataFimFuncao > vDataFim) THEN
    
      vRetorno := (vDataFim - vDataInicioFuncao) + 1;
    
    END IF;
    
    --Se a data de disponibilização é maior que a data de inicio
    --e a data de desligamento inferior a data de fim e superior
    --a data de inicio então conta-se este período.
    
    IF (vDataInicioFuncao >= vDataInicio 
        AND vDataInicioFuncao <= vDataFim
        AND vDataFimFuncao >= vDataInicio
        AND vDataFimFuncao <= vDataFim) THEN
  
      vRetorno := (vDataFimFuncao - vDataInicioFuncao) + 1;
    
    END IF;
    
    --Se a data da disponibilização for inferior ao mês de cálculo e 
    --o terceirizado tiver desligamento no mês referência, então contam-se
    --os dias trabalhados.
    
    IF (vDataInicioFuncao < vDataInicio 
        AND vDataFimFuncao >= vDataInicio
        AND vDataFimFuncao <= vDataFim) THEN
    
      vRetorno := (vDataFimFuncao - vDataInicio) + 1;
    
    END IF;
 
  END IF;

  END IF;  

  RETURN vRetorno;

END;