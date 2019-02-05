create or replace procedure "P_CALCULA_RETROATIVIDADE" (pCodContrato NUMBER, pDataCobranca DATE) 
AS

  --Procedure que calcula a retroatividade do total mensal a reter 
  --em um determinado período para um determinado contrato.
  
  --Para fazer DEBUG no Oracle: DBMS_OUTPUT.PUT_LINE(vDataReferencia);
  
 --Variáveis totalizadoras de valores.

  vTotalFerias FLOAT := 0;
  vTotalTercoConstitucional FLOAT := 0;
  vTotalDecimoTerceiro FLOAT := 0;
  vTotalIncidencia FLOAT := 0;
  vTotalIndenizacao FLOAT := 0;
  vTotal FLOAT := 0;

  --Variáveis de valores parciais.

  vValorFerias FLOAT := 0;
  vValorTercoConstitucional FLOAT := 0;
  vValorDecimoTerceiro FLOAT := 0;
  vValorIncidencia FLOAT := 0;
  vValorIndenizacao FLOAT := 0;

  --Variáveis para o montante retido.

  vTotalFeriasRetido FLOAT := 0;
  vTotalTerConstitucionalRetido FLOAT := 0;
  vTotalDecimoTerceiroRetido FLOAT := 0;
  vTotalIncidenciaRetido FLOAT := 0;
  vTotalIndenizacaoRetido FLOAT := 0;
  vTotalRetido FLOAT := 0;

  --Variáveis de percentuais.

  vPercentualFerias FLOAT := 0;
  vPercentualTercoConstitucional FLOAT := 0;
  vPercentualDecimoTerceiro FLOAT := 0;
  vPercentualIncidencia FLOAT := 0;
  vPercentualIndenizacao FLOAT := 0;
  vPercentualPenalidadeFGTS FLOAT := 0;
  vPercentualMultaFGTS FLOAT := 0;

  --Variável da remuneração da função do contrato.
  
  vRemuneracao FLOAT := 0;

  --Variável para a verificação de existência da cálculos realizados.
  
  vExisteCalculo NUMBER := 0;

  --Variáveis de referêcia.

  vMes NUMBER;
  vAno NUMBER;
  vRetroatividadeRemuneracao NUMBER;
  vDataReferencia DATE;
  vUltimoDiaConvencao DATE;
  vDataInicioConvencao DATE;
  vDataFimConvencao DATE;
  vDataInicioPercentual DATE := NULL;
  vDataFimPercentualEstatico DATE := NULL;
  vDataFimPercentual DATE;
  vDataFimMes DATE;
  vDataInicio DATE;
  vDataFim DATE;
  vDataCobranca DATE;
  vCodTotalMensalAReter NUMBER;

  --Variáveis de exceção.

  vRemuneracaoException EXCEPTION;
  vPeriodoException EXCEPTION;
  vContratoException EXCEPTION;
  
BEGIN

  --Para cada cargo do contrato.
  
  FOR c1 IN (SELECT cod
             FROM tb_funcao_contrato
             WHERE cod_contrato = pCodContrato
               AND ((cod IN (SELECT rfc.cod_funcao_contrato
                               FROM tb_retroatividade_remuneracao rr
                                 JOIN tb_remuneracao_fun_con rfc ON rfc.cod = rr.cod_rem_funcao_contrato
                               WHERE EXTRACT(month FROM rr.data_cobranca) = EXTRACT(month FROM pDataCobranca)
                                 AND EXTRACT(year FROM rr.data_cobranca) = EXTRACT(year FROM pDataCobranca)))
                     OR EXISTS (SELECT rp.cod 
                                  FROM tb_retroatividade_percentual rp
                                    JOIN tb_percentual_contrato pc ON pc.cod = rp.cod_percentual_contrato
                                  WHERE EXTRACT(month FROM rp.data_cobranca) = EXTRACT(month FROM pDataCobranca)
                                    AND EXTRACT(year FROM rp.data_cobranca) = EXTRACT(year FROM pDataCobranca)
                                    AND pc.cod_contrato = pCodContrato)
                     OR EXISTS (SELECT rpe.cod 
                                  FROM tb_retro_percentual_estatico rpe
                                  WHERE EXTRACT(month FROM rpe.data_cobranca) = EXTRACT(month FROM pDataCobranca)
                                    AND EXTRACT(year FROM rpe.data_cobranca) = EXTRACT(year FROM pDataCobranca)))) LOOP

    --Configuração da data inicio e fim do perído retroativo.

    vDataInicio := pDataInicio;
    vDataFim := pDataFim;
     
    --Análise de como deve ser consederada a convenção coletiva
    --do cargo. Desconsidera-se o aditamento se não estiver na
    --data de cobrança do mesmo.
    
    vRetroatividadeRemuneracao := 2;

    BEGIN
    
      SELECT rr.data_cobranca
        INTO vDataCobranca
        FROM tb_retroatividade_remuneracao rr
          JOIN tb_remuneracao_fun_con rfc ON rfc.cod = rr.cod_rem_funcao_contrato
        WHERE rfc.cod_funcao_contrato = c1.cod
          AND EXTRACT(month FROM rr.data_cobranca) = EXTRACT(month FROM pDataCobranca)
          AND EXTRACT(year FROM rr.data_cobranca) = EXTRACT(year FROM pDataCobranca);

      EXCEPTION WHEN NO_DATA_FOUND THEN

        vDataCobranca := NULL;

    END;

    IF ((vDataCobranca IS NULL) 
        OR 
        (EXTRACT(month FROM vDataCobranca) != EXTRACT(month FROM pDataCobranca)
        AND 
        EXTRACT(year FROM vDataCobranca) != EXTRACT(year FROM pDataCobranca))) THEN

      vRetroatividadeRemuneracao := 1;

    END IF;

    --LOOP que compreende o período de retroatividade para um determinado cargo. 

    FOR i IN 1 .. F_RETORNA_NUMERO_DE_MESES(pDataInicio, pDataFim) LOOP

      --Definição da data referência (início do mês de cálculo) e do fim do mês.

      vMes := EXTRACT(month FROM vDataInicio);
      vAno := EXTRACT(year FROM vDataInicio);

      vDataReferencia := TO_DATE('01/' || vMes || '/' || vAno, 'dd/mm/yyyy');
  
      vDataFimMes := LAST_DAY(vDataReferencia);
  
      IF (EXTRACT(day FROM vDataFimMes) = 31) THEN
  
        vDataFimMes := vDataFimMes - 1;
  
      END IF;
  
      IF (EXTRACT(day FROM vDataFimMes) = 28) THEN
  
        vDataFimMes := vDataFimMes + 2;
  
      END IF;
  
      IF (EXTRACT(day FROM vDataFimMes) = 29) THEN
  
        vDataFimMes := vDataFimMes + 1;
  
      END IF;

      --Caso não haja mudaça de percentual no mês designado carregam-se os valores.
  
      IF (F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, vMes, vAno, 2) = FALSE) THEN 
	  
        --Definição dos percentuais.
  
        vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 1, 2);
  	    vPercentualTercoConstitucional := vPercentualFerias/3;
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 1, 2);
  	    vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 1, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
  	    vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
  	    vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
  	    vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
  	    vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

      END IF;

      --Se não existe dupla convenção e duplo percentual.

      IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 1, vRetroatividadeRemuneracao);

        IF (vRemuneracao IS NULL) THEN
      
          RAISE vRemuneracaoException;
        
        END IF;
      
        --Para cada funcionário que ocupa aquele cargo.
      
        FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                          ft.cod
                     FROM tb_funcao_terceirizado ft
                     WHERE ft.cod_funcao_contrato = c1.cod
                       AND ((ft.data_inicio <= vDataReferencia)
                             OR 
                             (EXTRACT(month FROM ft.data_inicio) = vMes)
                              AND 
                              EXTRACT(year FROM ft.data_inicio) = vAno)
                       AND ((ft.data_fim IS NULL)
                            OR 
                            (ft.data_fim >= LAST_DAY(vDataReferencia))
                            OR 
                            (EXTRACT(month FROM ft.data_fim) = vMes)
                             AND 
                             EXTRACT(year FROm ft.data_fim) = vAno)) LOOP
                            
          --Redefine todas as variáveis.
    
          vTotal := 0.00;
          vTotalFerias := 0.00;
          vTotalTercoConstitucional := 0.00;
          vTotalDecimoTerceiro := 0.00;
          vTotalIncidencia := 0.00;
          vTotalIndenizacao := 0.00;

          vTotalRetido := 0.00;
          vTotalFeriasRetido := 0.00;
          vTotalTerConstitucionalRetido := 0.00;
          vTotalDecimoTerceiroRetido := 0.00;
          vTotalIncidenciaRetido := 0.00;
          vTotalIndenizacaoRetido := 0.00;

          vCodTotalMensalAReter := -1;
          vExisteCalculo := NULL;

          --Seleção do registro do total_mensal_a_reter a ser vinculado a retroatividade. 

          SELECT cod
            INTO vCodTotalMensalAReter
            FROM tb_total_mensal_a_reter
            WHERE cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato
              AND EXTRACT(month FROM data_referencia) = EXTRACT(month FROM pDataCobranca)
              AND EXTRACT(year FROM data_referencia) = EXTRACT(year FROM pDataCobranca);

          --Verifica a existência de cálculo para definir inserção ou atualização de valores.

          SELECT COUNT(cod)
            INTO vExisteCalculo
            FROM tb_retroatividade_total_mensal
            WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

          --Preenchimento dos valores retidos.

          SELECT ferias,
                 terco_constitucional,
                 decimo_terceiro,
                 incidencia_submodulo_4_1,
                 multa_fgts,
                 total
            INTO vTotalFeriasRetido,
                 vTotalTerConstitucionalRetido,
                 vTotalDecimoTerceiroRetido,
                 vTotalIncidenciaRetido,
                 vTotalIndenizacaoRetido,
                 vTotalRetido
            FROM tb_total_mensal_a_reter
            WHERE EXTRACT(month FROM data_referencia) = vMes
              AND EXTRACT(year FROM data_referencia) = vAno
              AND cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato;

          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
	  
            vTotalFerias := vRemuneracao * (vPercentualFerias/100);
            vTotalTercoConstitucional := vRemuneracao * (vPercentualTercoConstitucional/100);
            vTotalDecimoTerceiro := vRemuneracao * (vPercentualDecimoTerceiro/100);
            vTotalIncidencia := vRemuneracao * (vPercentualIncidencia/100);
            vTotalIndenizacao := vRemuneracao * (vPercentualIndenizacao/100);
      
          END IF;
        
          --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN

            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
	  
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES(c2.cod, vMes, vAno);
      
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);

          --Cálculo da diferença.

          vTotalFerias := (vTotalFerias) - (vTotalFeriasRetido);
          vTotalTercoConstitucional := (vTotalTercoConstitucional) - (vTotalTerConstitucionalRetido);
          vTotalDecimoTerceiro := (vTotalDecimoTerceiro) - (vTotalDecimoTerceiroRetido);
          vTotalIncidencia := (vTotalIncidencia) - (vTotalIncidenciaRetido);
          vTotalIndenizacao := (vTotalIndenizacao) - (vTotalIndenizacaoRetido);
          vTotal := (vTotal) - (vTotalRetido);

          --Insere ou atualiza os valores retroativos na tabela tb_retroatividade_total_mensal.

          IF (vExisteCalculo = 0) THEN

            INSERT INTO tb_retroatividade_total_mensal (cod_total_mensal_a_reter,
                                                        ferias,
                                                        terco_constitucional,
                                                        decimo_terceiro,
                                                        incidencia_submodulo_4_1,
                                                        multa_fgts,
                                                        total,
                                                        login_atualizacao,
                                                        data_atualizacao)
              VALUES (vCodTotalMensalAReter,
                      vTotalFerias,
                      vTotalTercoConstitucional,
                      vTotalDecimoTerceiro,
                      vTotalIncidencia,
                      vTotalIndenizacao,
                      vTotal,
                      'RETROACTIVE SYSTEM',
                      SYSDATE);

            ELSE

              IF (vExisteCalculo = 1) THEN

                UPDATE tb_retroatividade_total_mensal
                  SET ferias = ferias + vTotalFerias,
                      terco_constitucional = terco_constitucional + vTotalTercoConstitucional,
                      decimo_terceiro = decimo_terceiro + vTotalDecimoTerceiro,
                      incidencia_submodulo_4_1 = incidencia_submodulo_4_1 + vTotalIncidencia,
                      multa_fgts = multa_fgts + vTotalIndenizacao,
                      total = total + vTotal,
                      login_atualizacao = 'RETROACTIVE SYSTEM',
                      data_atualizacao = SYSDATE
                  WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

              END IF;

            END IF;
       
	      END LOOP;
  
      END IF;
    
      --Se não existe dupla convenção e existe duplo percentual.

      IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a remuneração do cargo
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 1, vRetroatividadeRemuneracao);

        IF (vRemuneracao IS NULL) THEN
      
          RAISE vRemuneracaoException;
        
        END IF;
            
        --Para cada funcionário que ocupa aquele cargo.
      
        FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                          ft.cod
                     FROM tb_funcao_terceirizado ft
                     WHERE ft.cod_funcao_contrato = c1.cod
                       AND ((ft.data_inicio <= vDataReferencia)
                             OR 
                             (EXTRACT(month FROM ft.data_inicio) = vMes)
                              AND 
                              EXTRACT(year FROM ft.data_inicio) = vAno)
                       AND ((ft.data_fim IS NULL)
                            OR 
                            (ft.data_fim >= LAST_DAY(vDataReferencia))
                            OR 
                            (EXTRACT(month FROM ft.data_fim) = vMes)
                             AND 
                             EXTRACT(year FROm ft.data_fim) = vAno)) LOOP
                   
          --Redefine todas as variáveis.
    
          vTotal := 0.00;
          vTotalFerias := 0.00;
          vTotalTercoConstitucional := 0.00;
          vTotalDecimoTerceiro := 0.00;
          vTotalIncidencia := 0.00;
          vTotalIndenizacao := 0.00;

          vTotalRetido := 0.00;
          vTotalFeriasRetido := 0.00;
          vTotalTerConstitucionalRetido := 0.00;
          vTotalDecimoTerceiroRetido := 0.00;
          vTotalIncidenciaRetido := 0.00;
          vTotalIndenizacaoRetido := 0.00;

          vCodTotalMensalAReter := -1;
          vExisteCalculo := NULL;

          --Seleção do registro do total_mensal_a_reter a ser vinculado a retroatividade. 

          SELECT cod
            INTO vCodTotalMensalAReter
            FROM tb_total_mensal_a_reter
            WHERE cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato
              AND EXTRACT(month FROM data_referencia) = EXTRACT(month FROM pDataCobranca)
              AND EXTRACT(year FROM data_referencia) = EXTRACT(year FROM pDataCobranca);

          --Verifica a existência de cálculo para definir inserção ou atualização de valores.

          SELECT COUNT(cod)
            INTO vExisteCalculo
            FROM tb_retroatividade_total_mensal
            WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

          --Preenchimento dos valores retidos.

          SELECT ferias,
                 terco_constitucional,
                 decimo_terceiro,
                 incidencia_submodulo_4_1,
                 multa_fgts,
                 total
            INTO vTotalFeriasRetido,
                 vTotalTerConstitucionalRetido,
                 vTotalDecimoTerceiroRetido,
                 vTotalIncidenciaRetido,
                 vTotalIndenizacaoRetido,
                 vTotalRetido
            FROM tb_total_mensal_a_reter
            WHERE EXTRACT(month FROM data_referencia) = vMes
              AND EXTRACT(year FROM data_referencia) = vAno
              AND cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato;
        
          --Definição dos percentuais da primeira metade do mês.
  
          vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 2, 2);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 2, 2);
          vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 2, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
  	      vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 2, 2);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 2, 2);
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
                   
          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
        
            --Recolhimento referente a primeira metade do mês.
	    
            vTotalFerias := (((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3));
            vTotalTercoConstitucional := (((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3));
            vTotalDecimoTerceiro := (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3));
            vTotalIncidencia := (((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3));
            vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3));
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 1, 2);     
  	        vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 1, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
          
            --Recolhimento referente a primeira metade do mês.
	  
            vTotalFerias := vTotalFerias + (((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
            vTotalIncidencia := vTotalIncidencia + (((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
            vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
      
          END IF;
        
          --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN

            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
	  
            vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 3));
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
            
            vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
       
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
          --Cálculo da diferença.

          vTotalFerias := (vTotalFerias - vTotalFeriasRetido);
          vTotalTercoConstitucional := (vTotalTercoConstitucional - vTotalTerConstitucionalRetido);
          vTotalDecimoTerceiro := (vTotalDecimoTerceiro - vTotalDecimoTerceiroRetido);
          vTotalIncidencia := (vTotalIncidencia - vTotalIncidenciaRetido);
          vTotalIndenizacao := (vTotalIndenizacao - vTotalIndenizacaoRetido);
          vTotal := (vTotal - vTotalRetido);

          --Insere ou atualiza os valores retroativos na tabela tb_retroatividade_total_mensal.

          IF (vExisteCalculo = 0) THEN

            INSERT INTO tb_retroatividade_total_mensal (cod_total_mensal_a_reter,
                                                        ferias,
                                                        terco_constitucional,
                                                        decimo_terceiro,
                                                        incidencia_submodulo_4_1,
                                                        multa_fgts,
                                                        total,
                                                        login_atualizacao,
                                                        data_atualizacao)
              VALUES (vCodTotalMensalAReter,
                      vTotalFerias,
                      vTotalTercoConstitucional,
                      vTotalDecimoTerceiro,
                      vTotalIncidencia,
                      vTotalIndenizacao,
                      vTotal,
                      'RETROACTIVE SYSTEM',
                      SYSDATE);

            ELSE

              IF (vExisteCalculo = 1) THEN

                UPDATE tb_retroatividade_total_mensal
                  SET ferias = ferias + vTotalFerias,
                      terco_constitucional = terco_constitucional + vTotalTercoConstitucional,
                      decimo_terceiro = decimo_terceiro + vTotalDecimoTerceiro,
                      incidencia_submodulo_4_1 = incidencia_submodulo_4_1 + vTotalIncidencia,
                      multa_fgts = multa_fgts + vTotalIndenizacao,
                      total = total + vTotal,
                      login_atualizacao = 'RETROACTIVE SYSTEM',
                      data_atualizacao = SYSDATE
                  WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

              END IF;

            END IF;
        
	      END LOOP;
  
      END IF;
    
      --Se existe dupla convenção e não existe duplo percentual.

      IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 2, vRetroatividadeRemuneracao);
        vRemuneracao2 := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 1, vRetroatividadeRemuneracao);

        IF (vRemuneracao IS NULL OR vRemuneracao2 IS NULL) THEN
      
          RAISE vRemuneracaoException;
        
        END IF;
      
        --Para cada funcionário que ocupa aquele cargo.
      
        FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                          ft.cod
                     FROM tb_funcao_terceirizado ft
                     WHERE ft.cod_funcao_contrato = c1.cod
                       AND ((ft.data_inicio <= vDataReferencia)
                             OR 
                             (EXTRACT(month FROM ft.data_inicio) = vMes)
                              AND 
                              EXTRACT(year FROM ft.data_inicio) = vAno)
                       AND ((ft.data_fim IS NULL)
                            OR 
                            (ft.data_fim >= LAST_DAY(vDataReferencia))
                            OR 
                            (EXTRACT(month FROM ft.data_fim) = vMes)
                             AND 
                             EXTRACT(year FROm ft.data_fim) = vAno)) LOOP
                   
          --Redefine todas as variáveis.
    
          vTotal := 0.00;
          vTotalFerias := 0.00;
          vTotalTercoConstitucional := 0.00;
          vTotalDecimoTerceiro := 0.00;
          vTotalIncidencia := 0.00;
          vTotalIndenizacao := 0.00;

          vTotalRetido := 0.00;
          vTotalFeriasRetido := 0.00;
          vTotalTerConstitucionalRetido := 0.00;
          vTotalDecimoTerceiroRetido := 0.00;
          vTotalIncidenciaRetido := 0.00;
          vTotalIndenizacaoRetido := 0.00;

          vCodTotalMensalAReter := -1;
          vExisteCalculo := NULL;

          --Seleção do registro do total_mensal_a_reter a ser vinculado a retroatividade. 

          SELECT cod
            INTO vCodTotalMensalAReter
            FROM tb_total_mensal_a_reter
            WHERE cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato
              AND EXTRACT(month FROM data_referencia) = EXTRACT(month FROM pDataCobranca)
              AND EXTRACT(year FROM data_referencia) = EXTRACT(year FROM pDataCobranca);

          --Verifica a existência de cálculo para definir inserção ou atualização de valores.

          SELECT COUNT(cod)
            INTO vExisteCalculo
            FROM tb_retroatividade_total_mensal
            WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

          --Preenchimento dos valores retidos.

          SELECT ferias,
                 terco_constitucional,
                 decimo_terceiro,
                 incidencia_submodulo_4_1,
                 multa_fgts,
                 total
            INTO vTotalFeriasRetido,
                 vTotalTerConstitucionalRetido,
                 vTotalDecimoTerceiroRetido,
                 vTotalIncidenciaRetido,
                 vTotalIndenizacaoRetido,
                 vTotalRetido
            FROM tb_total_mensal_a_reter
            WHERE EXTRACT(month FROM data_referencia) = vMes
              AND EXTRACT(year FROM data_referencia) = vAno
              AND cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato;
                   
          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
	  
            --Retenção proporcional da primeira convenção.
          
            vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
            vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
            vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
            vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
          
            --Retenção proporcional da segunda convenção.
          
            vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 2));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 2));
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 2));
            vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 2));
            vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 2));
      
          END IF;
        
          --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN
          
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
            --Retenção proporcional da primeira convenção.
	  
            vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) *  F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 1));
          
            --Retenção proporcional da segunda convenção.
	  
            vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) *  F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 2));
      
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
          --Cálculo da diferença.

          vTotalFerias := (vTotalFerias - vTotalFeriasRetido);
          vTotalTercoConstitucional := (vTotalTercoConstitucional - vTotalTerConstitucionalRetido);
          vTotalDecimoTerceiro := (vTotalDecimoTerceiro - vTotalDecimoTerceiroRetido);
          vTotalIncidencia := (vTotalIncidencia - vTotalIncidenciaRetido);
          vTotalIndenizacao := (vTotalIndenizacao - vTotalIndenizacaoRetido);
          vTotal := (vTotal - vTotalRetido);

          --Insere ou atualiza os valores retroativos na tabela tb_retroatividade_total_mensal.

          IF (vExisteCalculo = 0) THEN

            INSERT INTO tb_retroatividade_total_mensal (cod_total_mensal_a_reter,
                                                        ferias,
                                                        terco_constitucional,
                                                        decimo_terceiro,
                                                        incidencia_submodulo_4_1,
                                                        multa_fgts,
                                                        total,
                                                        login_atualizacao,
                                                        data_atualizacao)
              VALUES (vCodTotalMensalAReter,
                      vTotalFerias,
                      vTotalTercoConstitucional,
                      vTotalDecimoTerceiro,
                      vTotalIncidencia,
                      vTotalIndenizacao,
                      vTotal,
                      'RETROACTIVE SYSTEM',
                      SYSDATE);

            ELSE

              IF (vExisteCalculo = 1) THEN

                UPDATE tb_retroatividade_total_mensal
                  SET ferias = ferias + vTotalFerias,
                      terco_constitucional = terco_constitucional + vTotalTercoConstitucional,
                      decimo_terceiro = decimo_terceiro + vTotalDecimoTerceiro,
                      incidencia_submodulo_4_1 = incidencia_submodulo_4_1 + vTotalIncidencia,
                      multa_fgts = multa_fgts + vTotalIndenizacao,
                      total = total + vTotal,
                      login_atualizacao = 'RETROACTIVE SYSTEM',
                      data_atualizacao = SYSDATE
                  WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

              END IF;

            END IF;
        
	      END LOOP;
  
      END IF;
    
      --Se existe mudança de percentual e mudança de convenção.
    
      IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a remuneração do cargo
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 2, vRetroatividadeRemuneracao);
        vRemuneracao2 := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, vMes, vAno, 1, vRetroatividadeRemuneracao);

        IF (vRemuneracao IS NULL OR vRemuneracao2 IS NULL) THEN
      
          RAISE vRemuneracaoException;
        
        END IF;
      
        --Definição das datas para os períodos da convenção e percentuais.
      
        SELECT data_fim
        INTO vDataFimConvencao
        FROM tb_remuneracao_fun_con
        WHERE cod_funcao_contrato = c1.cod
          AND data_aditamento IS NOT NULL
          AND (EXTRACT(month FROM data_fim) = vMes
               AND EXTRACT(year FROM data_fim) = vAno);
               
        --Observação: datas dos percentuais são todas iguais para um bloco.

        --Para o percentual do contrato.

        IF (F_MUNDANCA_PERCENTUAL_CONTRATO(pCodContrato, vMes, vAno, 1) = TRUE) THEN

          SELECT DISTINCT(data_fim)
            INTO vDataFimPercentual
            FROM tb_percentual_contrato
            WHERE cod_contrato = pCodContrato
              AND data_aditamento IS NOT NULL
              AND (EXTRACT(month FROM data_fim) = vMes
                   AND EXTRACT(year FROM data_fim) = vAno);

        END IF;

        --Para o percentual estático.

        IF (F_MUNDANCA_PERCENTUAL_ESTATICO(pCodContrato, vMes, vAno, 1) = TRUE) THEN

          SELECT DISTINCT(data_fim)
            INTO vDataFimPercentualEstatico
            FROM tb_percentual_estatico
            WHERE data_aditamento IS NOT NULL
              AND (EXTRACT(month FROM data_fim) = vMes
                   AND EXTRACT(year FROM data_fim) = vAno);

        END IF;

        --Decisão da data fim do percentual.

        IF (vDataFimPercentual IS NOT NULL AND vDataFimPercentualEstatico IS NOT NULL) THEN

          SELECT GREATEST(vDataFimPercentual, vDataFimPercentualEstatico)
            INTO vDataFimPercentual
            FROM DUAL;
 
        END IF;
               
        vDataInicioConvencao := vDataFimConvencao + 1;
        vDataInicioPercentual := vDataFimPercentual + 1;
         
        --Para cada funcionário que ocupa aquele cargo.
      
        FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                          ft.cod
                     FROM tb_funcao_terceirizado ft
                     WHERE ft.cod_funcao_contrato = c1.cod
                       AND ((ft.data_inicio <= vDataReferencia)
                             OR 
                             (EXTRACT(month FROM ft.data_inicio) = vMes)
                              AND 
                              EXTRACT(year FROM ft.data_inicio) = vAno)
                       AND ((ft.data_fim IS NULL)
                            OR 
                            (ft.data_fim >= LAST_DAY(vDataReferencia))
                            OR 
                            (EXTRACT(month FROM ft.data_fim) = vMes)
                             AND 
                             EXTRACT(year FROm ft.data_fim) = vAno)) LOOP
                   
          --Redefine todas as variáveis.
    
          vTotal := 0.00;
          vTotalFerias := 0.00;
          vTotalTercoConstitucional := 0.00;
          vTotalDecimoTerceiro := 0.00;
          vTotalIncidencia := 0.00;
          vTotalIndenizacao := 0.00;

          vTotalRetido := 0.00;
          vTotalFeriasRetido := 0.00;
          vTotalTerConstitucionalRetido := 0.00;
          vTotalDecimoTerceiroRetido := 0.00;
          vTotalIncidenciaRetido := 0.00;
          vTotalIndenizacaoRetido := 0.00;

          vCodTotalMensalAReter := -1;
          vExisteCalculo := NULL;

          --Seleção do registro do total_mensal_a_reter a ser vinculado a retroatividade. 

          SELECT cod
            INTO vCodTotalMensalAReter
            FROM tb_total_mensal_a_reter
            WHERE cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato
              AND EXTRACT(month FROM data_referencia) = EXTRACT(month FROM pDataCobranca)
              AND EXTRACT(year FROM data_referencia) = EXTRACT(year FROM pDataCobranca);

          --Verifica a existência de cálculo para definir inserção ou atualização de valores.

          SELECT COUNT(cod)
            INTO vExisteCalculo
            FROM tb_retroatividade_total_mensal
            WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

          --Preenchimento dos valores retidos.

          SELECT ferias,
                 terco_constitucional,
                 decimo_terceiro,
                 incidencia_submodulo_4_1,
                 multa_fgts,
                 total
            INTO vTotalFeriasRetido,
                 vTotalTerConstitucionalRetido,
                 vTotalDecimoTerceiroRetido,
                 vTotalIncidenciaRetido,
                 vTotalIndenizacaoRetido,
                 vTotalRetido
            FROM tb_total_mensal_a_reter
            WHERE EXTRACT(month FROM data_referencia) = vMes
              AND EXTRACT(year FROM data_referencia) = vAno
              AND cod_funcao_terceirizado = c2.cod
              AND cod_terceirizado_contrato = c2.cod_terceirizado_contrato;
        
          --Definição do método de cálculo.
        
          IF (vDataFimConvencao < vDataFimPercentual) THEN
        
            --Definição dos percentuais da primeira metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 2, 2);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 2, 2);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 2, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 2, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 2, 2);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

            --Se a retenção for para período integral.           

            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
	  
              --Retenção proporcional da primeira porção do mês.
          
              vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
              vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
              vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
              vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 1);
          
              --Retenção proporcional da segunda porção do mês.
          
              vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
              vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
              vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
              vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
              vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 2, 2);     
              vPercentualTercoConstitucional := vPercentualFerias/3;
              vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 1, 2);
              vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 1, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
            
              --Retenção proporcional da terceira porção do mês.
          
              vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
              vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
              vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
              vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));            
      
            END IF;
        
            --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN

              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);

              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
              --Retenção proporcional da primeira porção do mês.
          
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 1);
          
              --Retenção proporcional da segunda porção do mês.
          
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 2) - F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 4)));
            
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;

              --Retenção proporcional da terceira porção do mês.
          
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 4)); 
      
            END IF;
      
            vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
          END IF;
        
          IF (vDataFimConvencao > vDataFimPercentual) THEN
        
            --Definição dos percentuais da primeira metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 2, 2);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 2, 2);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 2, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 2, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 2, 2);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

            --Se a retenção for para período integral.           

            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
	  
              --Retenção proporcional da primeira porção do mês.
          
              vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
          
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 1, 2);     
              vPercentualTercoConstitucional := vPercentualFerias/3;
              vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 1, 2);
              vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 1, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
          
              --Retenção proporcional da segunda porção do mês.
          
              vTotalFerias := vTotalFerias + (((vRemuneracao * (vPercentualFerias/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
              vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
              vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
              vTotalIncidencia := vTotalIncidencia + (((vRemuneracao * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            
              --Retenção proporcional da terceira porção do mês.
          
              vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
              vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
              vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
              vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));            
      
            END IF;
        
            --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN

              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);

              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
              --Retenção proporcional da primeira porção do mês.
          
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 3);
          
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;

              --Retenção proporcional da segunda porção do mês.
          
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * (F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 4) - F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 2)));
            
              --Retenção proporcional da terceira porção do mês.
          
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 2));     
      
            END IF;
      
            vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
          END IF;
        
          --Caso as datas da convenção e do percentual sejam iguais.
        
          IF (vDataFimConvencao = vDataFimPercentual) THEN
        
            --Definição dos percentuais da primeira metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 2, 2);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 2, 2);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 2, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 2, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 2, 2);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

            --Se a retenção for para período integral.           

            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = TRUE) THEN
	  
              --Retenção proporcional da primeira porção do mês.
          
              vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 3);
          
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, vMes, vAno, 1, 2);     
              vPercentualTercoConstitucional := vPercentualFerias/3;
              vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, vMes, vAno, 1, 2);
              vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, vMes, vAno, 1, 2) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
          
              --Retenção proporcional da segunda porção do mês.
          
              vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
              vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
              vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
              vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, vMes, vAno, 4));            
      
            END IF;
        
            --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
            IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, vMes, vAno) = FALSE) THEN

              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 2, 2);

              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
              --Retenção proporcional da primeira porção do mês.
          
              vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 3);
          
              --Definição dos percentuais da segunda metade do mês.
  
              vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, vMes, vAno, 1, 2);
              vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, vMes, vAno, 1, 2);
              vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, vMes, vAno, 1, 2);
              vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;

              --Retenção proporcional da segunda porção do mês.
          
  	          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, vMes, vAno, 4));      
      
            END IF;
      
            vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
          END IF;
      
          --Cálculo da diferença.

          vTotalFerias := (vTotalFerias - vTotalFeriasRetido);
          vTotalTercoConstitucional := (vTotalTercoConstitucional - vTotalTerConstitucionalRetido);
          vTotalDecimoTerceiro := (vTotalDecimoTerceiro - vTotalDecimoTerceiroRetido);
          vTotalIncidencia := (vTotalIncidencia - vTotalIncidenciaRetido);
          vTotalIndenizacao := (vTotalIndenizacao - vTotalIndenizacaoRetido);
          vTotal := (vTotal - vTotalRetido);

          --Insere ou atualiza os valores retroativos na tabela tb_retroatividade_total_mensal.

          IF (vExisteCalculo = 0) THEN

            INSERT INTO tb_retroatividade_total_mensal (cod_total_mensal_a_reter,
                                                        ferias,
                                                        terco_constitucional,
                                                        decimo_terceiro,
                                                        incidencia_submodulo_4_1,
                                                        multa_fgts,
                                                        total,
                                                        login_atualizacao,
                                                        data_atualizacao)
              VALUES (vCodTotalMensalAReter,
                      vTotalFerias,
                      vTotalTercoConstitucional,
                      vTotalDecimoTerceiro,
                      vTotalIncidencia,
                      vTotalIndenizacao,
                      vTotal,
                      'RETROACTIVE SYSTEM',
                      SYSDATE);

            ELSE

              IF (vExisteCalculo = 1) THEN

                UPDATE tb_retroatividade_total_mensal
                  SET ferias = ferias + vTotalFerias,
                      terco_constitucional = terco_constitucional + vTotalTercoConstitucional,
                      decimo_terceiro = decimo_terceiro + vTotalDecimoTerceiro,
                      incidencia_submodulo_4_1 = incidencia_submodulo_4_1 + vTotalIncidencia,
                      multa_fgts = multa_fgts + vTotalIndenizacao,
                      total = total + vTotal,
                      login_atualizacao = 'RETROACTIVE SYSTEM',
                      data_atualizacao = SYSDATE
                  WHERE cod_total_mensal_a_reter = vCodTotalMensalAReter;

              END IF;

            END IF;
        
	      END LOOP;
  
      END IF;

      --Incrementa a data a cada repetição para passar por todos os meses da retroatividade.

      IF (EXTRACT(month FROM vDataFim) != vMes) THEN
      
        vDataInicio := ADD_MONTHS(vDataInicio, 1);

      END IF;

    END LOOP;
  
  END LOOP;

END;
