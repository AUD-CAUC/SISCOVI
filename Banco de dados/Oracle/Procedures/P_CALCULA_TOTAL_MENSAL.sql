create or replace procedure "P_CALCULA_TOTAL_MENSAL" (pCodContrato NUMBER, pMes NUMBER, pAno NUMBER) 
AS

  --Procedure que calcula o total mensal a reter em um determinado mês para
  --um determinado contrato.
  
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
  
  --Variáveis de datas.

  vDataReferencia DATE;
  vDataInicio DATE;
  vDataFim DATE;
  vDataInicioContrato DATE;
  vDataFimContrato DATE;

  --Variável de checagem da existência do contrato.

  vCheck NUMBER := 0;

  --Variáveis de exceção.

  vRemuneracaoException EXCEPTION;
  vPeriodoException EXCEPTION;
  vContratoException EXCEPTION;

BEGIN
  
  --Checagem da validade do contrato passado (existe).

  SELECT COUNT(cod)
    INTO vCheck
    FROM tb_contrato 
    WHERE cod = pCodContrato;

  IF (vCheck = 0) THEN

    RAISE vContratoException;

  END IF;

  --Definição da data referência (início do mês de cálculo) e do fim do mês.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');
  
  --Se a data passada for anterior ao contrato ou posterior ao seu termino aborta-se.
    
  SELECT MIN(ec.data_inicio_vigencia)
    INTO vDataInicioContrato
    FROM tb_evento_contratual ec 
    WHERE ec.cod_contrato = pCodContrato;
    
  SELECT MAX(ec.data_fim_vigencia)
    INTO vDataFimContrato
    FROM tb_evento_contratual ec 
    WHERE ec.cod_contrato = pCodContrato;

  IF (vDataReferencia < (LAST_DAY((ADD_MONTHS(TRUNC(vDataInicioContrato), -1)) + 1))) THEN

    RAISE vPeriodoException;

    RETURN;

  END IF;

  IF (vDataFimContrato IS NOT NULL AND (TRUNC(vDataReferencia) > TRUNC((LAST_DAY((ADD_MONTHS(vDataFimContrato, -1)) + 1))))) THEN

    RAISE vPeriodoException;

    RETURN;

  END IF; 

  --Verificação da existência de cálculo para aquele mês e consequente deleção.
  
  SELECT COUNT(tmr.cod)
    INTO vExisteCalculo
	FROM tb_total_mensal_a_reter tmr
      JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato
	WHERE EXTRACT(month FROM tmr.data_referencia) = pMes
	  AND EXTRACT(year FROM tmr.data_referencia) = pAno
    AND tc.cod_contrato = pCodContrato;
	  
  IF (vExisteCalculo > 0) THEN

    --Deleta as retroatividades associadas aquele mês/ano.
  
    DELETE
      FROM tb_retroatividade_total_mensal
      WHERE cod_total_mensal_a_reter IN (SELECT tmr.cod 
                                           FROM tb_total_mensal_a_reter tmr
                                             JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato
                                           WHERE EXTRACT(month FROM tmr.data_referencia) = pMes 
                                             AND EXTRACT(year FROM tmr.data_referencia) = pAno
                                             AND tc.cod_contrato = pCodContrato);

    --Deleta os recolhimentos realizados naquele mês/ano.
  
    DELETE 
	    FROM tb_total_mensal_a_reter tmr
  	  WHERE EXTRACT(month FROM tmr.data_referencia) = pMes 
	      AND EXTRACT(year FROM tmr.data_referencia) = pAno
		    AND tmr.cod_terceirizado_contrato IN (SELECT tc.cod 
                                                FROM tb_terceirizado_contrato tc
                                                WHERE tc.cod_contrato = pCodContrato);                                                 
  
  END IF;
  
    --Caso não haja mudaça de percentual no mês designado carregam-se os valores.
  
  IF (F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = FALSE) THEN 
	  
    --Definição dos percentuais.
  
    vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 1, pMes, pAno, 1, 1);
    vPercentualTercoConstitucional := vPercentualFerias/3;
    vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 3, pMes, pAno, 1, 1);
    vPercentualIncidencia := (F_RETORNA_PERCENTUAL_CONTRATO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
    vPercentualIndenizacao := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 4, pMes, pAno, 1, 1);
    vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 6, pMes, pAno, 1, 1);
    vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(pCodContrato, 5, pMes, pAno, 1, 1);
    vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

  END IF;
	
  --Para cada função do contrato.
  
  FOR c1 IN (SELECT cod
               FROM tb_funcao_contrato
               WHERE cod_contrato = pCodContrato) LOOP 
  
    --Se não existe dupla convenção e duplo percentual.

    IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, pMes, pAno, 1) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = FALSE) THEN

      --Define a remuneração do função.

      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);
      
      IF (vRemuneracao IS NULL) THEN
      
        RAISE vRemuneracaoException;
        
      END IF;
          
      --Para cada funcionário que ocupa aquela função.
      
      FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                        ft.cod
                   FROM tb_funcao_terceirizado ft
                   WHERE ft.cod_funcao_contrato = c1.cod
                     AND ((ft.data_inicio <= vDataReferencia)
                          OR (EXTRACT(month FROM ft.data_inicio) = pMes)
                              AND EXTRACT(year FROM ft.data_inicio) = pAno)
                     AND ((ft.data_fim IS NULL)
                          OR (ft.data_fim >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM ft.data_fim) = pMes)
                              AND EXTRACT(year FROm ft.data_fim) = pAno)
                ) LOOP
                 
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
               
        --Se a retenção for para período integral.           

        vTotalFerias := vRemuneracao * (vPercentualFerias/100);
        vTotalTercoConstitucional := vRemuneracao * (vPercentualTercoConstitucional/100);
        vTotalDecimoTerceiro := vRemuneracao * (vPercentualDecimoTerceiro/100);
        vTotalIncidencia := vRemuneracao * (vPercentualIncidencia/100);
        vTotalIndenizacao := vRemuneracao * (vPercentualIndenizacao/100);

        --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, situação similar para a retenção proporcional por menos de 14 dias trabalhados.

        IF (F_EXISTE_MUDANCA_FUNCAO(c2.cod_terceirizado_contrato, pMes, pAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

          vTotalFerias := (vTotalFerias/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);
          vTotalTercoConstitucional := (vTotalTercoConstitucional/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);
          vTotalDecimoTerceiro := (vTotalDecimoTerceiro/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);
          vTotalIncidencia := (vTotalIncidencia/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);
          vTotalIndenizacao := (vTotalIndenizacao/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);

        END IF;
                          
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_terceirizado_contrato,
                                             cod_funcao_terceirizado,
                                             ferias,
                                             terco_constitucional,
                                             decimo_terceiro,
                                             incidencia_submodulo_4_1,
                                             multa_fgts,
                                             total,
                                             data_referencia,
                                             login_atualizacao,
                                             data_atualizacao)
		      VALUES(c2.cod_terceirizado_contrato,
                 c2.cod,
                 vTotalFerias,
                 vTotalTercoConstitucional,
                 vTotalDecimoTerceiro,
                 vTotalIncidencia,
                 vTotalIndenizacao,
                 vTotal,
                 vDataReferencia,
                 'SYSTEM',
                 SYSDATE);
        
	    END LOOP;
  
    END IF;

    --Se não existe dupla convenção e existe duplo percentual.

    IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, pMes, pAno, 1) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = TRUE) THEN
    
      --Define a remuneração do funcao
            
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);

      IF (vRemuneracao IS NULL) THEN
      
        RAISE vRemuneracaoException;
        
      END IF;
            
      --Para cada funcionário que ocupa aquele função.
      
      FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                        ft.cod
                   FROM tb_funcao_terceirizado ft
                   WHERE ft.cod_funcao_contrato = c1.cod
                     AND ((ft.data_inicio <= vDataReferencia)
                          OR (EXTRACT(month FROM ft.data_inicio) = pMes)
                              AND EXTRACT(year FROM ft.data_inicio) = pAno)
                     AND ((ft.data_fim IS NULL)
                          OR (ft.data_fim >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM ft.data_fim) = pMes)
                              AND EXTRACT(year FROm ft.data_fim) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
        
        vValorFerias := 0;
        vValorTercoConstitucional := 0;
        vValorDecimoTerceiro := 0;
        vValorIncidencia := 0;
        vValorIndenizacao := 0;
        
        vDataInicio := vDataReferencia;

        FOR c3 IN (SELECT data_inicio AS data 
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = pCodContrato
                       AND (EXTRACT(month FROM data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM data_inicio) = pAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = pCodContrato
                       AND (EXTRACT(month FROM data_fim) = pMes
                            AND 
                            EXTRACT(year FROM data_fim) = pAno)

                   UNION

                   SELECT data_inicio AS data 
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM data_inicio) = pAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_fim) = pMes
                            AND 
                            EXTRACT(year FROM data_fim) = pAno)

                   UNION

                   SELECT CASE WHEN pMes = 2 THEN 
                            LAST_DAY(TO_DATE('28/' || pMes || '/' || pAno, 'dd/mm/yyyy')) 
                          ELSE 
                            TO_DATE('30/' || pMes || '/' || pAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP

          --Definição das datas de início e fim do subperíodo.

          vDataFim := c3.data;
        
          --Definição dos percentuais do subperíodo.
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(pCodContrato, 1, vDataInicio, vDataFim, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(pCodContrato, 3, vDataInicio, vDataFim, 1);
          vPercentualIncidencia := (F_RET_PERCENTUAL_CONTRATO(pCodContrato, 7, vDataInicio, vDataFim, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
        
          vPercentualIndenizacao := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 4, vDataInicio, vDataFim, 1);
          vPercentualPenalidadeFGTS := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 6, vDataInicio, vDataFim, 1);
          vPercentualMultaFGTS := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 5, vDataInicio, vDataFim, 1);
        
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * ((vDataFim - vDataInicio) + 1);

          --No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo.

          IF (F_EXISTE_MUDANCA_FUNCAO(c2.cod_terceirizado_contrato, pMes, pAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vValorFerias := (vValorFerias/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIndenizacao := (vValorIndenizacao/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);

          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;
          vTotalIndenizacao := vTotalIndenizacao + vValorIndenizacao;

          vDataInicio := vDataFim + 1;
          
        END LOOP;
     
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_terceirizado_contrato,
                                             cod_funcao_terceirizado,
                                             ferias,
                                             terco_constitucional,
                                             decimo_terceiro,
                                             incidencia_submodulo_4_1,
                                             multa_fgts,
                                             total,
                                             data_referencia,
                                             login_atualizacao,
                                             data_atualizacao)
		      VALUES(c2.cod_terceirizado_contrato,
                 c2.cod,
                 vTotalFerias,
                 vTotalTercoConstitucional,
                 vTotalDecimoTerceiro,
                 vTotalIncidencia,
                 vTotalIndenizacao,
                 vTotal,
                 vDataReferencia,
                 'SYSTEM',
                 SYSDATE);
        
	    END LOOP;
  
    END IF;
    
    --Se existe dupla convenção e não existe duplo percentual.
    
    IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, pMes, pAno, 1) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = FALSE) THEN
    
      --Para cada funcionário que ocupa aquele funcao.
      
      FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                        ft.cod
                   FROM tb_funcao_terceirizado ft
                   WHERE ft.cod_funcao_contrato = c1.cod
                     AND ((ft.data_inicio <= vDataReferencia)
                          OR (EXTRACT(month FROM ft.data_inicio) = pMes)
                              AND EXTRACT(year FROM ft.data_inicio) = pAno)
                     AND ((ft.data_fim IS NULL)
                          OR (ft.data_fim >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM ft.data_fim) = pMes)
                              AND EXTRACT(year FROm ft.data_fim) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;

        vValorFerias := 0;
        vValorTercoConstitucional := 0;
        vValorDecimoTerceiro := 0;
        vValorIncidencia := 0;
        vValorIndenizacao := 0;
        
        vDataInicio := vDataReferencia;

        FOR c3 IN (SELECT rfc.data_inicio AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = pCodContrato
                       AND fc.cod = c1.cod
                       AND (EXTRACT(month FROM rfc.data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM rfc.data_inicio) = pAno)

                   UNION

                   SELECT rfc.data_fim AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = pCodContrato
                       AND fc.cod = c1.cod
                       AND (EXTRACT(month FROM rfc.data_fim) = pMes
                            AND 
                            EXTRACT(year FROM rfc.data_fim) = pAno)

                   UNION

                   SELECT CASE WHEN pMes = 2 THEN 
                          LAST_DAY(TO_DATE('28/' || pMes || '/' || pAno, 'dd/mm/yyyy')) 
                          ELSE 
                          TO_DATE('30/' || pMes || '/' || pAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP

          --Definição da data fim do subperíodo.

          vDataFim := c3.data;

          --Define a remuneração do funcao
       
          vRemuneracao := F_RET_REMUNERACAO_PERIODO(c1.cod, vDataInicio, vDataFim, 1);
          
          IF (vRemuneracao IS NULL) THEN
      
            RAISE vRemuneracaoException;
        
          END IF;
                   
          --Cálculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * ((vDataFim - vDataInicio) + 1);
        
          --No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo.

          IF (F_EXISTE_MUDANCA_FUNCAO(c2.cod_terceirizado_contrato, pMes, pAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vValorFerias := (vValorFerias/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIndenizacao := (vValorIndenizacao/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);

          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;
          vTotalIndenizacao := vTotalIndenizacao + vValorIndenizacao;

          vDataInicio := vDataFim + 1;

        END LOOP;
      
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_terceirizado_contrato,
                                             cod_funcao_terceirizado,
                                             ferias,
                                             terco_constitucional,
                                             decimo_terceiro,
                                             incidencia_submodulo_4_1,
                                             multa_fgts,
                                             total,
                                             data_referencia,
                                             login_atualizacao,
                                             data_atualizacao)
		      VALUES(c2.cod_terceirizado_contrato,
                 c2.cod,
                 vTotalFerias,
                 vTotalTercoConstitucional,
                 vTotalDecimoTerceiro,
                 vTotalIncidencia,
                 vTotalIndenizacao,
                 vTotal,
                 vDataReferencia,
                 'SYSTEM',
                 SYSDATE);
        
	  END LOOP;
  
    END IF;
    
    --Se existe mudança de percentual e mudança de convenção.
    
    IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, pMes, pAno, 1) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = TRUE) THEN  
               
      --Para cada funcionário que ocupa aquele funcao.
      
      FOR c2 IN (SELECT ft.cod_terceirizado_contrato,
                        ft.cod
                   FROM tb_funcao_terceirizado ft
                   WHERE ft.cod_funcao_contrato = c1.cod
                     AND ((ft.data_inicio <= vDataReferencia)
                          OR (EXTRACT(month FROM ft.data_inicio) = pMes)
                              AND EXTRACT(year FROM ft.data_inicio) = pAno)
                     AND ((ft.data_fim IS NULL)
                          OR (ft.data_fim >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM ft.data_fim) = pMes)
                              AND EXTRACT(year FROm ft.data_fim) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;

        vValorFerias := 0;
        vValorTercoConstitucional := 0;
        vValorDecimoTerceiro := 0;
        vValorIncidencia := 0;
        vValorIndenizacao := 0;
        
        vDataInicio := vDataReferencia;
        
        FOR c3 IN (SELECT data_inicio AS data 
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = pCodContrato
                       AND (EXTRACT(month FROM data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM data_inicio) = pAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_contrato
                     WHERE cod_contrato = pCodContrato
                       AND (EXTRACT(month FROM data_fim) = pMes
                            AND 
                            EXTRACT(year FROM data_fim) = pAno)

                   UNION

                   SELECT data_inicio AS data 
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM data_inicio) = pAno)
    
                   UNION

                   SELECT data_fim AS data
                     FROM tb_percentual_estatico
                     WHERE (EXTRACT(month FROM data_fim) = pMes
                            AND 
                            EXTRACT(year FROM data_fim) = pAno)

                   UNION
                   
                   SELECT rfc.data_inicio AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = pCodContrato
                       AND fc.cod = c1.cod
                       AND (EXTRACT(month FROM rfc.data_inicio) = pMes
                            AND 
                            EXTRACT(year FROM rfc.data_inicio) = pAno)

                   UNION

                   SELECT rfc.data_fim AS data 
                     FROM tb_remuneracao_fun_con rfc
                       JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato
                     WHERE fc.cod_contrato = pCodContrato
                       AND fc.cod = c1.cod
                       AND (EXTRACT(month FROM rfc.data_fim) = pMes
                            AND 
                            EXTRACT(year FROM rfc.data_fim) = pAno)

                   UNION

                   SELECT CASE WHEN pMes = 2 THEN 
                          LAST_DAY(TO_DATE('28/' || pMes || '/' || pAno, 'dd/mm/yyyy')) 
                          ELSE 
                          TO_DATE('30/' || pMes || '/' || pAno, 'dd/mm/yyyy') END AS data
                     FROM DUAL

                   ORDER BY data ASC) LOOP

          --Definição da data fim do subperíodo.

          vDataFim := c3.data;

          --Define a remuneração do funcao
       
          vRemuneracao := F_RET_REMUNERACAO_PERIODO(c1.cod, vDataInicio, vDataFim, 1);
          
          IF (vRemuneracao IS NULL) THEN
      
            RAISE vRemuneracaoException;
        
          END IF;

          --Definição dos percentuais do subperíodo.
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(pCodContrato, 1, vDataInicio, vDataFim, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(pCodContrato, 3, vDataInicio, vDataFim, 1);
          vPercentualIncidencia := (F_RET_PERCENTUAL_CONTRATO(pCodContrato, 7, vDataInicio, vDataFim, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
        
          vPercentualIndenizacao := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 4, vDataInicio, vDataFim, 1);
          vPercentualPenalidadeFGTS := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 6, vDataInicio, vDataFim, 1);
          vPercentualMultaFGTS := F_RET_PERCENTUAL_ESTATICO(pCodContrato, 5, vDataInicio, vDataFim, 1);
        
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * ((vDataFim - vDataInicio) + 1);
          vValorIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * ((vDataFim - vDataInicio) + 1);

          --No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo.

          IF (F_EXISTE_MUDANCA_FUNCAO(c2.cod_terceirizado_contrato, pMes, pAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vValorFerias := (vValorFerias/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIncidencia := (vValorIncidencia/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);
            vValorIndenizacao := (vValorIndenizacao/((vDataFim - vDataInicio) + 1)) * F_DIAS_TRABALHADOS_PERIOODO(c2.cod, vDataInicio, vDataFim);

          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidencia := vTotalIncidencia + vValorIncidencia;
          vTotalIndenizacao := vTotalIndenizacao + vValorIndenizacao;

          vDataInicio := vDataFim + 1;

        END LOOP;     
          
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
        INSERT INTO tb_total_mensal_a_reter (cod_terceirizado_contrato,
                                             cod_funcao_terceirizado,
                                             ferias,
                                             terco_constitucional,
                                             decimo_terceiro,
                                             incidencia_submodulo_4_1,
                                             multa_fgts,
                                             total,
                                             data_referencia,
                                             login_atualizacao,
                                             data_atualizacao)
		      VALUES(c2.cod_terceirizado_contrato,
                 c2.cod,
                 vTotalFerias,
                 vTotalTercoConstitucional,
                 vTotalDecimoTerceiro,
                 vTotalIncidencia,
                 vTotalIndenizacao,
                 vTotal,
                 vDataReferencia,
                 'SYSTEM',
                 SYSDATE);
        
	    END LOOP;
  
    END IF;

  END LOOP;
  
  --Verifica se existe a retroarividade e dispara o processo de cálculo
  --em caso afirmativo.
  
  IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 1) = TRUE OR F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 2) = TRUE) THEN

    P_CALCULA_RETROATIVIDADE(pCodContrato, vDataReferencia);
  
  END IF;

  EXCEPTION 
  
    WHEN vRemuneracaoException THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução do procedimento: Remuneração não encontrada.');

    WHEN vPeriodoException THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução do procedimento: Período fora da vigência contratual.');
  
    WHEN vContratoException THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução do procedimento: Contrato inexistente.');
    
    WHEN OTHERS THEN
  
      RAISE_APPLICATION_ERROR(-20004, 'Erro na execução do procedimento: Causa não detectada.');

END;
