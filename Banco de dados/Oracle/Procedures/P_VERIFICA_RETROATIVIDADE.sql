create or replace procedure "P_VERIFICA_RETROATIVIDADE" (pCodContrato NUMBER, pCodRemFuncaoContrato NUMBER, pCodPercentualContrato NUMBER, pDataInicio DATE, pDataAditamento DATE, pOperacao NUMBER)
AS

  --Procedimento que verifica se existe uma situação de retroatividade
  --e insere o registro correspondente na tabela tb_retroatividade
  --em caso afirmativo.

  --pOperação = 1 - Cadastro de convenção e percentual.
  --pOperação = 2 - Cálculo de total mensal a reter.

  vExisteCalculo NUMBER;
  vAcao NUMBER := 0;
  vDataInicioRetroatividade DATE;
  vDataFimRetroatividade DATE;
  vDataCobrancaRetroatividade DATE;
  vLoginAtualizacao VARCHAR2(150) := 'SYSTEM';
  vCodConvecaoColetiva NUMBER;

BEGIN

  BEGIN

    --Definição da existência de cálculo para o mês de aditamento.

    SELECT COUNT(tmr.cod)
      INTO vExisteCalculo
      FROM tb_total_mensal_a_reter tmr
        JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato
        JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod
        JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato
      WHERE fc.cod_contrato = pCodContrato
        AND EXTRACT(month FROM tmr.data_referencia) = EXTRACT(month FROM pDataAditamento)
        AND EXTRACT(year FROM tmr.data_referencia) = EXTRACT(year FROM pDataAditamento);
      
    EXCEPTION WHEN NO_DATA_FOUND THEN
    
      vExisteCalculo := 0;

  END;

  --Para cadastro de convenção ou percentual.

  IF (pOperacao = 1) THEN

    --Sendo o aditamento no mesmo mês da convenção.

    IF ((EXTRACT(month FROM pDataInicio) = EXTRACT(month FROM pDataAditamento))
        AND
       (EXTRACT(year FROM pDataInicio) = EXTRACT(year FROM pDataAditamento))) THEN

      --Se já existe cálculo então haverá retroatividade no mês seguinte.

      IF (vExisteCalculo > 0) THEN

        vDataInicioRetroatividade := pDataInicio;
        vDataFimRetroatividade := LAST_DAY(pDataInicio); 
        vDataCobrancaRetroatividade := LAST_DAY(pDataInicio) + 1; --Mês seguinte.
        vAcao := 1;

      END IF;

    END IF;

    --Sendo o aditamento no mês supeior ao da convenção.

    IF (((EXTRACT(month FROM pDataInicio) < EXTRACT(month FROM pDataAditamento))
       AND
       (EXTRACT(year FROM pDataInicio) = EXTRACT(year FROM pDataAditamento)))
       OR
       ((EXTRACT(year FROM pDataInicio) < EXTRACT(year FROM pDataAditamento)))) THEN

      --Como nesse caso sempre há retroatividade então ela poderá ser
      --cobrada no mês do aditamento ou no seguinte a depender da
      --existência de cálculo.

      IF (vExisteCalculo > 0) THEN

        vDataInicioRetroatividade := pDataInicio;
        vDataFimRetroatividade := LAST_DAY(pDataAditamento); 
        vDataCobrancaRetroatividade := LAST_DAY(pDataAditamento) + 1;
        vAcao := 1;

      ELSE

        vDataInicioRetroatividade := pDataInicio;
        vDataFimRetroatividade := LAST_DAY(ADD_MONTHS(pDataAditamento, -1));
        vDataCobrancaRetroatividade := LAST_DAY(ADD_MONTHS(pDataAditamento, -1)) + 1;
        vAcao := 1;

      END IF;

    END IF; 

  END IF;

  --Inserir na tabela tb_retroatividade_remuneracao.
  
  IF (pCodRemFuncaoContrato IS NOT NULL AND vAcao = 1) THEN

    INSERT INTO tb_retroatividade_remuneracao (cod_rem_funcao_contrato, 
                                               inicio, 
                                               fim, 
                                               data_cobranca, 
                                               login_atualizacao, 
                                               data_atualizacao)
      VALUES (pCodRemFuncaoContrato,
              vDataInicioRetroatividade,
              vDataFimRetroatividade,
              vDataCobrancaRetroatividade,
              vLoginAtualizacao,
              SYSDATE);
              
  END IF;
  
  --Inserir na tabela tb_retroatividade_percentual.
  
  IF (pCodRemFuncaoContrato IS NULL AND vAcao = 1) THEN

    INSERT INTO tb_retroatividade_percentual (cod_percentual_contrato, 
                                              inicio, 
                                              fim, 
                                              data_cobranca, 
                                              login_atualizacao, 
                                              data_atualizacao)
      VALUES (pCodPercentualContrato,
              vDataInicioRetroatividade,
              vDataFimRetroatividade,
              vDataCobrancaRetroatividade,
              vLoginAtualizacao,
              SYSDATE);
              
  END IF;
  
END;