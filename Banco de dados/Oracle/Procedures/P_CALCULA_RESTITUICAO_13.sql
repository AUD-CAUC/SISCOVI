create or replace procedure "P_CALCULA_RESTITUICAO_13" (pCodTerceirizadoContrato NUMBER,
                                                        pCodTipoRestituicao NUMBER,
                                                        pNumeroParcela NUMBER,
                                                        pInicioContagem DATE,
                                                        pFimContagem DATE,
                                                        pValor13 FLOAT) AS

--Procedure que calcula faz o registro de restituição de férias no banco de dados.

  --Para DEBUG no ORACLE: DBMS_OUTPUT.PUT_LINE(vTotalFerias);

  --Chaves primárias

  vCodContrato NUMBER;
  vCodTbRestituicao13 NUMBER;

  --Variáveis totalizadoras de valores.

  vTotalDecimoTerceiro FLOAT := 0;
  vTotalIncidencia FLOAT := 0;

  --Variáveis de valores parciais.

  vValorDecimoTerceiro FLOAT := 0;
  vValorIncidencia FLOAT := 0;

  --Variáveis de percentuais.

  vPercentualDecimoTerceiro FLOAT := 0;
  vPercentualIncidencia FLOAT := 0;
 
  --Variável da remuneração da função do contrato.
  
  vRemuneracao FLOAT := 0;

  --Variáveis de datas.

  vDataReferencia DATE;
  vDataInicio DATE;
  vDataFim DATE;
  vAno NUMBER;
  vMes NUMBER;

  --Variável de checagem da existência do terceirizado.

  vCheck NUMBER := 0;

  --Variáveis de exceção.

  vRemuneracaoException EXCEPTION;
  vPeriodoException EXCEPTION;
  vTerceirizadoException EXCEPTION;
  vParametroNulo EXCEPTION;

  --Variáveis de controle.
  
  vNumeroDeMeses NUMBER := 0;
  vDiasSubperiodo NUMBER := 0;

  --Variáveis auxiliares.

  vValor FLOAT := 0;
  vIncidencia FLOAT := 0;

BEGIN

  --Todos os parâmetros estão preenchidos.

  IF (pCodTerceirizadoContrato IS NULL OR
      pCodTipoRestituicao IS NULL OR
      pNumeroParcela IS NULL OR
      pInicioContagem IS NULL OR
      pFimContagem IS NULL OR
      pValor13 IS NULL) THEN
  
    RAISE vParametroNulo;
  
  END IF;

  --Checagem da validade do terceirizado passado (existe no contrato).

  SELECT COUNT(cod)
    INTO vCheck
    FROM tb_terceirizado_contrato
    WHERE cod = pCodTerceirizadoContrato;

  IF (vCheck = 0) THEN

    RAISE vTerceirizadoException;

  END IF;

  --Carrega o número de meses que compreende o período de férias.
  
  vNumeroDeMeses := F_RETORNA_NUMERO_DE_MESES(pInicioContagem, pFimContagem);
  
  --Carregar o cod do terceirizado e do contrato.

  SELECT tc.cod_contrato
    INTO vCodContrato
    FROM tb_terceirizado_contrato tc 
    WHERE tc.cod = pCodTerceirizadoContrato;

  --Definir o valor das variáveis vMes e vAno de acordo com a data de início do período aquisitivo.

  vMes := EXTRACT(month FROM pInicioContagem);
  vAno := EXTRACT(year FROM pInicioContagem);

  --O cálculo é feito mês a mês para preservar os efeitos das alterações contratuais.

  FOR i IN 1 .. vNumeroDeMeses LOOP

    --Definição da data referência.

    vDataReferencia := TO_DATE('01/' || vMes || '/' || vAno, 'dd/mm/yyyy');

    --Reset das variáveis que contém valores parciais.

    vValorDecimoTerceiro := 0;
    vValorIncidencia := 0;

    --Este loop reúne as funções que um determinado terceirizado exerceu no mês de cálculo.

    FOR f IN (SELECT ft.cod_funcao_contrato,
                     ft.cod
                FROM tb_funcao_terceirizado ft
                WHERE ft.cod_terceirizado_contrato = pCodTerceirizadoContrato
                  AND (((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= vDataReferencia)
                       AND 
                       (ft.data_fim >= vDataReferencia))
                        OR
                       ((TO_DATE('01/' || EXTRACT(month FROM ft.data_inicio) || '/' || EXTRACT(year FROM ft.data_inicio), 'dd/mm/yyyy') <= vDataReferencia)
                        AND
                       (ft.data_fim IS NULL)))) LOOP

      --Se não existem alterações nos percentuais ou na remuneração.

      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = FALSE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo e os percentuais de férias, terço e incidência.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(f.cod_funcao_contrato, vMes, vAno, 1, 2);
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 7, vMes, vAno, 1, 2);
     
        IF (vRemuneracao IS NULL) THEN
       
          RAISE vRemuneracaoException;
        
        END IF;

        --Cálculo do valor integral correspondente ao mês.      

        vValorDecimoTerceiro := (vRemuneracao * (vPercentualDecimoTerceiro/100));
        vValorIncidencia := (vValorDecimoTerceiro * (vPercentualIncidencia/100));

        --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
        --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

        IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

          vValorDecimoTerceiro := (vValorDecimoTerceiro/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidencia := (vValorIncidencia/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          
        END IF;

        vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
        vTotalIncidencia := vTotalIncidencia + vValorIncidencia;   
  
      END IF;

      --Se existe apenas alteração de percentual no mês.

      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = FALSE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = TRUE) THEN

        --Define a remuneração do cargo, que não se altera no período.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(f.cod_funcao_contrato, vMes, vAno, 1, 2);
     
        IF (vRemuneracao IS NULL) THEN
       
          RAISE vRemuneracaoException;
        
        END IF;
    
        --Definição da data de início como sendo a data referência (primeiro dia do mês).

        vDataInicio := vDataReferencia;

        --Loop contendo das datas das alterações de percentuais que comporão os subperíodos.

        FOR c3 IN (SELECT data_inicio AS data 
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = vCodContrato
                       AND (EXTRACT(month FROM data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM data_inicio) = vAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = vCodContrato
                       AND (EXTRACT(month FROM data_fim) = vMes
                            AND 
                            EXTRACT(year FROM data_fim) = vAno)

                   UNION

                   SELECT data_inicio AS data 
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM data_inicio) = vAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_fim) = vMes
                            AND 
                            EXTRACT(year FROM data_fim) = vAno)

                   UNION

                   SELECT CASE WHEN vMes = 2 THEN 
                            LAST_DAY(TO_DATE('28/' || vMes || '/' || vAno, 'dd/mm/yyyy')) 
                          ELSE 
                            TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP
          
          --Definição da data fim do subperíodo.

          vDataFim := c3.data;

          --Definição dos dias no subperíodo.

          vDiasSubperiodo := ((vDataFim - vDataInicio) + 1);

          IF (vMes = 2) THEN

            IF (EXTRACT(DAY FROM vDataFim) = EXTRACT(DAY FROM LAST_DAY(vDataFim))) THEN

              IF (EXTRACT(DAY FROM vDataFim) = 28) THEN

                vDiasSubperiodo := vDiasSubperiodo + 2;

              ELSE

                vDiasSubperiodo := vDiasSubperiodo + 1;

              END IF;

            END IF;

          END IF;

          --Definição dos percentuais do subperíodo.
  
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 3, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);
                  
          --Calculo da porção correspondente ao subperíodo.
 
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidencia := (vValorDecimoTerceiro * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;     

          vDataInicio := vDataFim + 1;

        END LOOP;
  
      END IF;

      --Se existe alteração de remuneração apenas.
  
      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = TRUE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Definição dos percentuais, que não se alteram no período.
  
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 7, vMes, vAno, 1, 2);
    
        --Definição da data de início como sendo a data referência (primeiro dia do mês).

        vDataInicio := vDataReferencia;

        --Loop contendo das datas das alterações de percentuais que comporão os subperíodos.

        FOR c3 IN (SELECT rfc.data_inicio AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = vCodContrato
                       AND fc.cod = f.cod_funcao_contrato
                       AND (EXTRACT(month FROM rfc.data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM rfc.data_inicio) = vAno)

                   UNION

                   SELECT rfc.data_fim AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = vCodContrato
                       AND fc.cod = f.cod_funcao_contrato
                       AND (EXTRACT(month FROM rfc.data_fim) = vMes
                            AND 
                            EXTRACT(year FROM rfc.data_fim) = vAno)

                   UNION

                   SELECT CASE WHEN vMes = 2 THEN 
                          LAST_DAY(TO_DATE('28/' || vMes || '/' || vAno, 'dd/mm/yyyy')) 
                          ELSE 
                          TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP
          
          --Definição da data fim do subperíodo.

          vDataFim := c3.data;

          --Definição dos dias no subperíodo.

          vDiasSubperiodo := ((vDataFim - vDataInicio) + 1);

          IF (vMes = 2) THEN

            IF (EXTRACT(DAY FROM vDataFim) = EXTRACT(DAY FROM LAST_DAY(vDataFim))) THEN

              IF (EXTRACT(DAY FROM vDataFim) = 28) THEN

                vDiasSubperiodo := vDiasSubperiodo + 2;

              ELSE

                vDiasSubperiodo := vDiasSubperiodo + 1;

              END IF;

            END IF;

          END IF;

          --Define a remuneração do cargo, que não se altera no período.
            
          vRemuneracao := F_RET_REMUNERACAO_PERIODO(f.cod_funcao_contrato, vDataInicio, vDataFim, 2);
     
          IF (vRemuneracao IS NULL) THEN
       
            RAISE vRemuneracaoException;
        
          END IF;

          --Calculo da porção correspondente ao subperíodo.
 
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidencia := (vValorDecimoTerceiro * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;   

          vDataInicio := vDataFim + 1;

        END LOOP;              
  
      END IF;
    
      --Se existe alteração na remuneração e nos percentuais.
    
      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = TRUE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Definição da data de início como sendo a data referência (primeiro dia do mês).

        vDataInicio := vDataReferencia;

        --Loop contendo das datas das alterações de percentuais que comporão os subperíodos.

        FOR c3 IN (SELECT data_inicio AS data 
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = vCodContrato
                       AND (EXTRACT(month FROM data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM data_inicio) = vAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = vCodContrato
                       AND (EXTRACT(month FROM data_fim) = vMes
                            AND 
                            EXTRACT(year FROM data_fim) = vAno)

                   UNION

                   SELECT data_inicio AS data 
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM data_inicio) = vAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_fim) = vMes
                            AND 
                            EXTRACT(year FROM data_fim) = vAno)

                   UNION
                   
                   SELECT rfc.data_inicio AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = vCodContrato
                       AND fc.cod = f.cod_funcao_contrato
                       AND (EXTRACT(month FROM rfc.data_inicio) = vMes
                            AND 
                            EXTRACT(year FROM rfc.data_inicio) = vAno)

                   UNION

                   SELECT rfc.data_fim AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = vCodContrato
                       AND fc.cod = f.cod_funcao_contrato
                       AND (EXTRACT(month FROM rfc.data_fim) = vMes
                            AND 
                            EXTRACT(year FROM rfc.data_fim) = vAno)

                   UNION

                   SELECT CASE WHEN vMes = 2 THEN 
                          LAST_DAY(TO_DATE('28/' || vMes || '/' || vAno, 'dd/mm/yyyy')) 
                          ELSE 
                          TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP
          
          --Definição da data fim do subperíodo.

          vDataFim := c3.data;

          --Definição dos dias no subperíodo.

          vDiasSubperiodo := ((vDataFim - vDataInicio) + 1);

          IF (vMes = 2) THEN

            IF (EXTRACT(DAY FROM vDataFim) = EXTRACT(DAY FROM LAST_DAY(vDataFim))) THEN

              IF (EXTRACT(DAY FROM vDataFim) = 28) THEN

                vDiasSubperiodo := vDiasSubperiodo + 2;

              ELSE

                vDiasSubperiodo := vDiasSubperiodo + 1;

              END IF;

            END IF;

          END IF;

          --Define a remuneração da função no subperíodo.
            
          vRemuneracao := F_RET_REMUNERACAO_PERIODO(f.cod_funcao_contrato, vDataInicio, vDataFim, 2);
     
          IF (vRemuneracao IS NULL) THEN
       
            RAISE vRemuneracaoException;
        
          END IF;

          --Definição dos percentuais do subperíodo.
  
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 3, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);

          --Calculo da porção correspondente ao subperíodo.
 
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidencia := (vValorDecimoTerceiro * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;   

          vDataInicio := vDataFim + 1;

        END LOOP;  
        
      END IF;

    END LOOP;
    
    --Atualização do mês e ano conforme a sequência do loop.
    
    IF (vMes != 12) THEN
    
      vMes := vMes + 1;
    
    ELSE
    
      vMes := 1;
      vAno := vAno + 1;    
    
    END IF;

  END LOOP;

  vCodTbRestituicao13 := tb_rest_dec_ter_seq.nextval;

  --No caso de primeira parcela é liberado a metade daquilo que foi retido (sem descontos).
  
  IF (pNumeroParcela = 1 OR pNumeroParcela = 2) THEN

    vTotalDecimoTerceiro := vTotalDecimoTerceiro/2;
    vTotalIncidencia := vTotalIncidencia/2;

  END IF;

  --No caso de segunda parcela a movimentação gera resíduos referentes ao
  --valor do décimo terceiro que é afetado pelos descontos (IRPF, INSS e etc.)

  IF ((pNumeroParcela = 2 OR pNumeroParcela = 0) AND UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    vValor := vTotalDecimoTerceiro - pValor13;

    vTotalDecimoTerceiro := pValor13;    

  END IF;

  --A incidência não é restituída para o empregado, portanto na movimentação
  --ela não deve ser computada. 
  
  IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    vIncidencia := vTotalIncidencia;

    vTotalIncidencia := 0;

  END IF;
  
  --Gravação no banco.
  
  INSERT INTO tb_restituicao_decimo_terceiro (cod,
                                              cod_terceirizado_contrato,
                                              cod_tipo_restituicao,
                                              parcela,
                                              data_inicio_contagem,
                                              valor,
                                              incidencia_submodulo_4_1,
                                              data_referencia,
                                              login_atualizacao,
                                              data_atualizacao)
    VALUES (vCodTbRestituicao13,
            pCodTerceirizadoContrato,
            pCodTipoRestituicao,
            pNumeroParcela,
            pInicioContagem,
            vTotalDecimoTerceiro,
            vTotalIncidencia,
            SYSDATE,
           'SYSTEM',
            SYSDATE);
/*
  --Da primeira parcela sobra metade do valor de décimo terceiro (não faz mais sentido).

  IF (pNumeroParcela = 1 AND UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    vValor := vTotalDecimoTerceiro;    

  END IF;          
*/
  --A incidência não é restituída para o empregado, portanto na movimentação
  --ela não deve ser computada. 
  
  IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    INSERT INTO tb_saldo_residual_dec_ter (cod_restituicao_dec_terceiro,
                                           valor,
                                           incidencia_submodulo_4_1,
                                           restituido,
                                           login_atualizacao,
                                           data_atualizacao)
      VALUES (vCodTbRestituicao13,
              vValor,
              vIncidencia,
              'N',
              'SYSTEM',
              SYSDATE);

    vTotalIncidencia := 0;

  END IF;

  EXCEPTION 
      
    WHEN vParametroNulo THEN

      RAISE_APPLICATION_ERROR(-20005, 'Falha no procedimento P_CALCULA_RESTITUICAO_13: parâmetro nulo.');

    WHEN vRemuneracaoException THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução do procedimento: Remuneração não encontrada.');

    WHEN vPeriodoException THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução do procedimento: Período fora da vigência contratual.');
  
    WHEN vTerceirizadoException THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução do procedimento: Terceirizado não encontrado no contrato.');
    
    WHEN OTHERS THEN
  
      RAISE_APPLICATION_ERROR(-20004, 'Erro na execução do procedimento: Causa não detectada.');

END;