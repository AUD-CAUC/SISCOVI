create or replace procedure "P_VERIFICA_RETRO_ESTATICA" (pCodPercentualEstatico NUMBER, pDataInicio DATE, pDataAditamento DATE, pLoginAtualizacao VARCHAR2)
AS

  --Procedimento que verifica se existe uma situação de retroatividade
  --e insere o registro correspondente na tabela tb_retroatividade
  --em caso afirmativo.

  --pOperação = 1 - Cadastro de convenção e percentual.
  --pOperação = 2 - Cálculo de total mensal a reter.

  vDataInicioRetroatividade DATE;
  vDataFimRetroatividade DATE;
  vDataCobrancaRetroatividade DATE;

  CURSOR contrato_calculado IS
    SELECT DISTINCT(fc.cod_contrato) AS cod_contrato
      FROM tb_total_mensal_a_reter tmr
        JOIN tb_funcao_terceirizado ft ON ft.cod = tmr.cod_funcao_terceirizado
        JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato
        JOIN tb_contrato c ON c.cod = fc.cod_contrato
      WHERE c.se_ativo = 'S'
        AND EXTRACT(month FROM tmr.data_referencia) = EXTRACT(month FROM pDataAditamento)
        AND EXTRACT(year FROM tmr.data_referencia) = EXTRACT(year FROM pDataAditamento)
        AND pDataAditamento >= (SELECT MIN(ec.data_inicio_vigencia)
                                  FROM tb_evento_contratual ec 
                                  WHERE ec.cod_contrato = c.cod)
        AND pDataAditamento <=  (SELECT MAX(ec.data_inicio_vigencia)
                                   FROM tb_evento_contratual ec 
                                   WHERE ec.cod_contrato = c.cod);

  CURSOR contrato_nao_calculado IS
    SELECT DISTINCT(c.cod) AS cod_contrato
      FROM tb_contrato c
        WHERE c.se_ativo = 'S'
        AND c.cod NOT IN (SELECT DISTINCT(fc.cod_contrato) AS cod_contrato
                            FROM tb_total_mensal_a_reter tmr
                              JOIN tb_funcao_terceirizado ft ON ft.cod = tmr.cod_funcao_terceirizado
                              JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato
                              JOIN tb_contrato c1 ON c1.cod = fc.cod_contrato
                            WHERE c1.se_ativo = 'S'
                              AND EXTRACT(month FROM tmr.data_referencia) = EXTRACT(month FROM pDataAditamento)
                              AND EXTRACT(year FROM tmr.data_referencia) = EXTRACT(year FROM pDataAditamento)
                              AND pDataAditamento >= (SELECT MIN(ec.data_inicio_vigencia)
                                                        FROM tb_evento_contratual ec 
                                                        WHERE ec.cod_contrato = c1.cod)
                              AND pDataAditamento <=  (SELECT MAX(ec1.data_inicio_vigencia)
                                                         FROM tb_evento_contratual ec1
                                                         WHERE ec1.cod_contrato = c1.cod))
        AND pDataAditamento >= (SELECT MIN(ec2.data_inicio_vigencia)
                                  FROM tb_evento_contratual ec2 
                                  WHERE ec2.cod_contrato = c.cod)
        AND pDataAditamento <=  (SELECT MAX(ec3.data_inicio_vigencia)
                                   FROM tb_evento_contratual ec3
                                   WHERE ec3.cod_contrato = c.cod);
       
BEGIN

    --Sendo o aditamento no mesmo mês da convenção.

    IF ((EXTRACT(month FROM pDataInicio) = EXTRACT(month FROM pDataAditamento))
        AND
       (EXTRACT(year FROM pDataInicio) = EXTRACT(year FROM pDataAditamento))) THEN

      --Se já existe cálculo então haverá retroatividade no mês seguinte.

      vDataInicioRetroatividade := pDataInicio;
      vDataFimRetroatividade := LAST_DAY(pDataInicio); 
      vDataCobrancaRetroatividade := LAST_DAY(pDataInicio) + 1; --Mês seguinte.

      FOR c1 IN contrato_calculado LOOP

        INSERT INTO tb_retro_percentual_estatico (cod_contrato,
                                                  cod_percentual_estatico, 
                                                  inicio, 
                                                  fim, 
                                                  data_cobranca, 
                                                  login_atualizacao, 
                                                  data_atualizacao)
          VALUES (c1.cod_contrato,
                  pCodPercentualEstatico,
                  vDataInicioRetroatividade,
                  vDataFimRetroatividade,
                  vDataCobrancaRetroatividade,
                  pLoginAtualizacao,
                  SYSDATE);

      END LOOP;

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

      vDataInicioRetroatividade := pDataInicio;
      vDataFimRetroatividade := LAST_DAY(pDataAditamento); 
      vDataCobrancaRetroatividade := LAST_DAY(pDataAditamento) + 1;

      FOR c1 IN contrato_calculado LOOP

        INSERT INTO tb_retro_percentual_estatico (cod_contrato,
                                                  cod_percentual_estatico, 
                                                  inicio, 
                                                  fim, 
                                                  data_cobranca, 
                                                  login_atualizacao, 
                                                  data_atualizacao)
          VALUES (c1.cod_contrato,
                  pCodPercentualEstatico,
                  vDataInicioRetroatividade,
                  vDataFimRetroatividade,
                  vDataCobrancaRetroatividade,
                  pLoginAtualizacao,
                  SYSDATE);

      END LOOP;

      vDataInicioRetroatividade := pDataInicio;
      vDataFimRetroatividade := LAST_DAY(ADD_MONTHS(pDataAditamento, -1));
      vDataCobrancaRetroatividade := LAST_DAY(ADD_MONTHS(pDataAditamento, -1)) + 1;

      FOR c1 IN contrato_nao_calculado LOOP

        INSERT INTO tb_retro_percentual_estatico (cod_contrato,
                                                  cod_percentual_estatico, 
                                                  inicio, 
                                                  fim, 
                                                  data_cobranca, 
                                                  login_atualizacao, 
                                                  data_atualizacao)
          VALUES (c1.cod_contrato,
                  pCodPercentualEstatico,
                  vDataInicioRetroatividade,
                  vDataFimRetroatividade,
                  vDataCobrancaRetroatividade,
                  pLoginAtualizacao,
                  SYSDATE);

      END LOOP;

    END IF; 

    EXCEPTION 
      
      WHEN OTHERS THEN
  
        RAISE_APPLICATION_ERROR(-20004, 'Erro na execução do procedimento P_VERIFICA_RETRO_ESTATICA: Causa não detectada.');
  
END;
