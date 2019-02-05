create or replace procedure "P_CALCULA_TOTAL_MENSAL" (pCodContrato NUMBER, pMes NUMBER, pAno NUMBER) 
AS

  --Procedure que calcula o total mensal a reter em um determinado mês para
  --um determinado contrato.
  
  --Para fazer DEBUG no Oracle: DBMS_OUTPUT.PUT_LINE(vDataReferencia);

  vTotalFerias FLOAT := 0;
  vTotalTercoConstitucional FLOAT := 0;
  vTotalDecimoTerceiro FLOAT := 0;
  vTotalIncidencia FLOAT := 0;
  vTotalIndenizacao FLOAT := 0;
  vPercentualFerias FLOAT := 0;
  vPercentualTercoConstitucional FLOAT := 0;
  vPercentualDecimoTerceiro FLOAT := 0;
  vPercentualIncidencia FLOAT := 0;
  vPercentualIndenizacao FLOAT := 0;
  vPercentualPenalidadeFGTS FLOAT := 0;
  vPercentualMultaFGTS FLOAT := 0;
  vRemuneracao FLOAT := 0;
  vRemuneracao2 FLOAT := 0;
  vTotal FLOAT := 0;
  vExisteCalculo NUMBER := 0;
  vDataReferencia DATE;
  vDataInicioConvencao DATE;
  vDataFimConvencao DATE;
  vDataInicioPercentual DATE;
  vDataFimPercentual DATE;
  vDataFimMes DATE;
  vDataRetroatividadeConvencao DATE;
  vFimRetroatividadeConvencao DATE;
  vDataRetroatividadePercentual DATE;
  vFimRetroatividadePercentual DATE;
  vDataRetroatividadePercentual2 DATE := NULL;
  vFimRetroatividadePercentual2 DATE := NULL;
  vDataInicio DATE;
  vDataFim DATE;
  vDataCobranca DATE;
  vDataInicioContrato DATE;
  vDataFimContrato DATE;

  remuneracao_inexistente EXCEPTION;

  --Cursor que reune a lista dos cargos pertencentes ao contrato em questão.
  
  CURSOR cargo IS
    SELECT cod
      FROM tb_cargo_contrato
      WHERE cod_contrato = pCodContrato;

BEGIN

  --Definição da data referência (início do mês de cálculo) e do fim do mês.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');
  
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

  --Se a data passada for anterior ao contrato ou posterior ao seu termino aborta-se.
    
  SELECT MIN(v.data_inicio_vigencia)
    INTO vDataInicioContrato
    FROM tb_eventos_contratuais v 
    WHERE v.cod_contrato = pCodContrato;
    
  SELECT MAX(v.data_fim_vigencia)
    INTO vDataFimContrato
    FROM tb_eventos_contratuais v 
    WHERE v.cod_contrato = pCodContrato;

  IF (vDataReferencia < (LAST_DAY((ADD_MONTHS(TRUNC(vDataInicioContrato), -1)) + 1))) THEN

    RETURN;

  END IF;

  IF (vDataFimContrato IS NOT NULL AND (TRUNC(vDataReferencia) > TRUNC((LAST_DAY((ADD_MONTHS(vDataFimContrato, -1)) + 1))))) THEN

    RETURN;

  END IF; 

  --Verificação da existência de cálculo para aquele mês e consequente deleção.
  
  SELECT COUNT(tmr.cod)
    INTO vExisteCalculo
	FROM tb_total_mensal_a_reter tmr
      JOIN tb_cargo_funcionario cf ON cf.cod = tmr.cod_cargo_funcionario
      JOIN tb_cargo_contrato cc ON cc.cod = cf.cod_cargo_contrato
	WHERE EXTRACT(month FROM tmr.data_referencia) = pMes
	  AND EXTRACT(year FROM tmr.data_referencia) = pAno
    AND cc.cod_contrato = pCodContrato;
	  
  IF (vExisteCalculo > 0) THEN
  
    DELETE
      FROM tb_retroatividade_total_mensal
      WHERE cod_total_mensal_a_reter IN (SELECT tmr.cod 
                                           FROM tb_total_mensal_a_reter tmr
                                             JOIN tb_cargo_funcionario cf ON cf.cod = tmr.cod_cargo_funcionario
                                             JOIN tb_cargo_contrato cc ON cc.cod = cf.cod_cargo_contrato
                                           WHERE EXTRACT(month FROM data_referencia) = pMes 
                                             AND EXTRACT(year FROM data_referencia) = pAno
                                             AND cc.cod_contrato = pCodContrato);
  
    DELETE 
	    FROM tb_total_mensal_a_reter tmr
  	  WHERE EXTRACT(month FROM tmr.data_referencia) = pMes 
	      AND EXTRACT(year FROM tmr.data_referencia) = pAno
		    AND tmr.cod_cargo_funcionario IN (SELECT cf.cod 
                                        FROM tb_cargo_funcionario cf
                                          JOIN tb_cargo_contrato cc ON cc.cod = cf.cod_cargo_contrato
                                        WHERE cc.cod_contrato = pCodContrato);                                                 
  
  END IF;
  
  --Caso não haja mudaça de percentual no mês designado carregam-se os valores.
  
  IF (F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = FALSE) THEN 
	  
    --Definição dos percentuais.
  
    vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 1, 1);
    vPercentualTercoConstitucional := vPercentualFerias/3;
    vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 1, 1);
    vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
    vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
    vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
    vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
    vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

  END IF;
	
  --Para cada cargo do contrato.
  
  FOR c1 IN cargo LOOP 
  
    --Se não existe dupla convenção e duplo percentual.

    IF (F_EXISTE_DUPLA_CONVENCAO(c1.cod, pMes, pAno, 1) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(pCodContrato, pMes, pAno, 1) = FALSE) THEN
    
      --Define a remuneração do cargo

      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);
      
      IF (vRemuneracao IS NULL) THEN
      
        RAISE remuneracao_inexistente;
        
      END IF;
      
      --Para cada funcionário que ocupa aquele cargo.
      
      FOR c2 IN (SELECT cod_funcionario, 
                        cod
                   FROM tb_cargo_funcionario
                   WHERE cod_cargo_contrato = c1.cod
                     AND ((data_disponibilizacao <= vDataReferencia)
                          OR (EXTRACT(month FROM data_disponibilizacao) = pMes)
                              AND EXTRACT(year FROM data_disponibilizacao) = pAno)
                     AND ((data_desligamento IS NULL)
                          OR (data_desligamento >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM data_desligamento) = pMes)
                              AND EXTRACT(year FROm data_desligamento) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
                   
        --Se a retenção for para período integral.           

        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
	  
          vTotalFerias := vRemuneracao * (vPercentualFerias/100);
          vTotalTercoConstitucional := vRemuneracao * (vPercentualTercoConstitucional/100);
          vTotalDecimoTerceiro := vRemuneracao * (vPercentualDecimoTerceiro/100);
          vTotalIncidencia := vRemuneracao * (vPercentualIncidencia/100);
          vTotalIndenizacao := vRemuneracao * (vPercentualIndenizacao/100);
      
        END IF;
        
        --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN
        
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);

          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
	  
          vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES(c2.cod, pMes, pAno);
      
        END IF;
      
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_cargo_funcionario,
                                             ferias,
                                             terco_constitucional,
                                             decimo_terceiro,
                                             incidencia_submodulo_4_1,
                                             multa_fgts,
                                             total,
                                             data_referencia,
                                             login_atualizacao,
                                             data_atualizacao)
		      VALUES(c2.cod,
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
    
      --Define a remuneração do cargo
            
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);
            
      --Para cada funcionário que ocupa aquele cargo.
      
      FOR c2 IN (SELECT cod_funcionario, 
                        cod
                   FROM tb_cargo_funcionario
		               WHERE cod_cargo_contrato = c1.cod
                     AND ((data_disponibilizacao <= vDataReferencia)
                          OR (EXTRACT(month FROM data_disponibilizacao) = pMes)
                              AND EXTRACT(year FROm data_disponibilizacao) = pAno)
                     AND ((data_desligamento IS NULL)
                          OR (data_desligamento >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM data_desligamento) = pMes)
                              AND EXTRACT(year FROm data_desligamento) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
        
        --Definição dos percentuais da primeira metade do mês.
  
        vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 2, 1);     
        vPercentualTercoConstitucional := vPercentualFerias/3;
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 2, 1);
        vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 2, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
        vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 2, 1);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 2, 1);
        vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
                   
        --Se a retenção for para período integral.           

        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
        
          --Recolhimento referente a primeira metade do mês.
	  
          vTotalFerias := (((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3));
          vTotalTercoConstitucional := (((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3));
          vTotalDecimoTerceiro := (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3));
          vTotalIncidencia := (((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3));
          vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3));
          
          --Definição dos percentuais da segunda metade do mês.
  
          vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 1, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 1, 1);
          vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
          
          --Recolhimento referente a primeira metade do mês.
	  
          vTotalFerias := vTotalFerias + (((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
          vTotalIncidencia := vTotalIncidencia + (((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
      
        END IF;
        
        --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);

          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
	  
	        vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 3));
          
           --Definição dos percentuais da segunda metade do mês.
  
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
          
          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 4));
      
        END IF;
      
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_cargo_funcionario,
	    							                         ferias,
										                         terco_constitucional,
										                         decimo_terceiro,
										                         incidencia_submodulo_4_1,
										                         multa_fgts,
										                         total,
										                         data_referencia,
										                         login_atualizacao,
										                         data_atualizacao)
		    VALUES(c2.cod,
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
    
      --Define a remuneração do cargo
            
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 2, 1);
      vRemuneracao2 := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);
      
      --Para cada funcionário que ocupa aquele cargo.
      
      FOR c2 IN (SELECT cod_funcionario, 
                        cod
		           FROM tb_cargo_funcionario
		           WHERE cod_cargo_contrato = c1.cod
                     AND ((data_disponibilizacao <= vDataReferencia)
                          OR (EXTRACT(month FROM data_disponibilizacao) = pMes)
                              AND EXTRACT(year FROm data_disponibilizacao) = pAno)
                     AND ((data_desligamento IS NULL)
                          OR (data_desligamento >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM data_desligamento) = pMes)
                              AND EXTRACT(year FROm data_desligamento) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
                   
        --Se a retenção for para período integral.           

        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
	  
          --Retenção proporcional da primeira convenção.
          
          vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          
          --Retenção proporcional da segunda convenção.
          
          vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 2));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 2));
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 2));
          vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 2));
          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 2));
      
        END IF;
        
        --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
        IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);

          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
          --Retenção proporcional da primeira convenção.
	  
          vTotalIndenizacao := (((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 1));
          
          --Retenção proporcional da segunda convenção.
	  
          vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) *  F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 2));
      
        END IF;
      
        vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);	
      
        INSERT INTO tb_total_mensal_a_reter (cod_cargo_funcionario,
	    							                         ferias,
										                         terco_constitucional,
										                         decimo_terceiro,
										                         incidencia_submodulo_4_1,
										                         multa_fgts,
										                         total,
										                         data_referencia,
										                         login_atualizacao,
										                         data_atualizacao)
		    VALUES(c2.cod,
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
    
      --Define a remuneração do cargo
            
      vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 2, 1);
      vRemuneracao2 := F_RETORNA_REMUNERACAO_PERIODO(c1.cod, pMes, pAno, 1, 1);
      
      --Definição das datas para os períodos da convenção e percentuais.
      
      SELECT data_fim
        INTO vDataFimConvencao
        FROM tb_remuneracao_cargo_contrato
        WHERE cod_cargo_contrato = c1.cod
          AND data_aditamento IS NOT NULL
          AND (EXTRACT(month FROM data_fim) = pMes
               AND EXTRACT(year FROM data_fim) = pAno);
               
      SELECT data_fim
        INTO vDataFimPercentual
        FROM tb_percentual_contrato
        WHERE cod_contrato = pCodContrato
          AND data_aditamento IS NOT NULL
          AND (EXTRACT(month FROM data_fim) = pMes
               AND EXTRACT(year FROM data_fim) = pAno);
               
      vDataInicioConvencao := vDataFimConvencao + 1;
      vDataInicioPercentual := vDataFimPercentual + 1;
         
      --Para cada funcionário que ocupa aquele cargo.
      
      FOR c2 IN (SELECT cod_funcionario, 
                        cod
		           FROM tb_cargo_funcionario
		           WHERE cod_cargo_contrato = c1.cod
                     AND ((data_disponibilizacao <= vDataReferencia)
                          OR (EXTRACT(month FROM data_disponibilizacao) = pMes)
                              AND EXTRACT(year FROm data_disponibilizacao) = pAno)
                     AND ((data_desligamento IS NULL)
                          OR (data_desligamento >= LAST_DAY(vDataReferencia))
                          OR (EXTRACT(month FROM data_desligamento) = pMes)
                              AND EXTRACT(year FROm data_desligamento) = pAno)
                ) LOOP
                   
        --Redefine todas as variáveis.
    
        vTotal := 0.00;
        vTotalFerias := 0.00;
        vTotalTercoConstitucional := 0.00;
        vTotalDecimoTerceiro := 0.00;
        vTotalIncidencia := 0.00;
        vTotalIndenizacao := 0.00;
        
        --Definição do método de cálculo.
        
        IF (vDataFimConvencao < vDataFimPercentual) THEN
        
          --Definição dos percentuais da primeira metade do mês.
  
          vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 2, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 2, 1);
          vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 2, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 2, 1);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 2, 1); 
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
	  
            --Retenção proporcional da primeira porção do mês.
          
            vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
            vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
            vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
            vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 1);
          
            --Retenção proporcional da segunda porção do mês.
          
            vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 2, 1);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 1, 1);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
            
            --Retenção proporcional da terceira porção do mês.
          
            vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));            
      
          END IF;
        
          --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
            --Retenção proporcional da primeira porção do mês.
          
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 1);
          
            --Retenção proporcional da segunda porção do mês.
          
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * (F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 2) - F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 4)));
            
            --Definição dos percentuais da segunda metade do mês.
   
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;

            --Retenção proporcional da terceira porção do mês.
          
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 4)); 
      
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
        END IF;
        
        IF (vDataFimConvencao > vDataFimPercentual) THEN
        
          --Definição dos percentuais da primeira metade do mês.
  
          vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 2, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 2, 1);
          vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 2, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 2, 1);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 2, 1);
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
	  
            --Retenção proporcional da primeira porção do mês.
          
            vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 1, 1);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 1, 1);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
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
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
            --Retenção proporcional da primeira porção do mês.
          
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 3);
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
          
            --Retenção proporcional da segunda porção do mês.
          
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao * (vPercentualIndenizacao/100))/30) * (F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 4) - F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 2)));
            
            --Retenção proporcional da terceira porção do mês.
          
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 2));     
      
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
        END IF;
        
        --Caso as datas da convenção e do percentual sejam iguais.
        
        IF (vDataFimConvencao = vDataFimPercentual) THEN
        
          --Definição dos percentuais da primeira metade do mês.
  
          vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 2, 1);     
          vPercentualTercoConstitucional := vPercentualFerias/3;
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 2, 1);
          vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 2, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
          vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 2, 1);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 2, 1);
          vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

          --Se a retenção for para período integral.           

          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = TRUE) THEN
	  
            --Retenção proporcional da primeira porção do mês.
          
            vTotalFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalIncidencia := ((vRemuneracao * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
            vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 3);
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 1, pMes, pAno, 1, 1);     
            vPercentualTercoConstitucional := vPercentualFerias/3;
            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 3, pMes, pAno, 1, 1);
            vPercentualIncidencia := (F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 7, pMes, pAno, 1, 1) * (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) * (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;
          
            --Retenção proporcional da segunda porção do mês.
          
            vTotalFerias := vTotalFerias + (((vRemuneracao2 * (vPercentualFerias/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao2 * (vPercentualTercoConstitucional/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao2 * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
            vTotalIncidencia := vTotalIncidencia + (((vRemuneracao2 * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(c1.cod, pMes, pAno, 4));            
      
          END IF;
        
          --Caso o funcionário não tenha trabalhado 15 dias ou mais no período.
      
          IF (F_FUNC_RETENCAO_INTEGRAL(c2.cod, pMes, pAno) = FALSE) THEN

            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 2, 1);

            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
        
            --Retenção proporcional da primeira porção do mês.
          
	          vTotalIndenizacao := ((vRemuneracao * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 3);
          
            --Definição dos percentuais da segunda metade do mês.
  
            vPercentualIndenizacao := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao := (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100))) * 100;
            
            --Retenção proporcional da segunda porção do mês.
          
  	        vTotalIndenizacao := vTotalIndenizacao + (((vRemuneracao2 * (vPercentualIndenizacao/100))/30) * F_DIAS_TRABALHADOS_MES_PARCIAL(c1.cod, c2.cod, pMes, pAno, 4));      
      
          END IF;
      
          vTotal := (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
        
        END IF;

        INSERT INTO tb_total_mensal_a_reter (cod_cargo_funcionario,
	    							                         ferias,
										                         terco_constitucional,
										                         decimo_terceiro,
										                         incidencia_submodulo_4_1,
										                         multa_fgts,
										                         total,
										                         data_referencia,
										                         login_atualizacao,
										                         data_atualizacao)
		    VALUES(c2.cod,
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

    IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 1) = TRUE) THEN
  
      SELECT MIN(inicio),
             MAX(fim)
        INTO vDataRetroatividadeConvencao,
             vFimRetroatividadeConvencao
        FROM tb_retroatividade_remuneracao rr
          JOIN tb_remuneracao_cargo_contrato rcco ON rcco.cod = rr.cod_remuneracao_cargo_contrato
          JOIN tb_cargo_contrato cc ON cc.cod = rcco.cod_cargo_contrato
        WHERE cc.cod_contrato = pCodContrato
          AND EXTRACT(month FROM data_cobranca) = pMes
          AND EXTRACT(year FROM data_cobranca) = pAno; 

    END IF;

    IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 2) = TRUE) THEN

      BEGIN

        SELECT MIN(rp.inicio),
               MAX(rp.fim)
          INTO vDataRetroatividadePercentual,
               vFimRetroatividadePercentual        
          FROM tb_retroatividade_percentual rp
            JOIN tb_percentual_contrato pc ON pc.cod = rp.cod_percentual_contrato
          WHERE pc.cod_contrato = pCodContrato
            AND EXTRACT(month FROM data_cobranca) = pMes
            AND EXTRACT(year FROM data_cobranca) = pAno;

        EXCEPTION WHEN NO_DATA_FOUND THEN

          vDataRetroatividadePercentual := NULL;
          vFimRetroatividadePercentual := NULL;

      END;

      BEGIN

        SELECT MIN(rpe.inicio),
               MAX(rpe.fim)
          INTO vDataRetroatividadePercentual2,
               vFimRetroatividadePercentual2      
          FROM tb_retro_percentual_estatico rpe
          WHERE rpe.cod_contrato = pCodContrato
            AND EXTRACT(month FROM rpe.data_cobranca) = pMes
            AND EXTRACT(year FROM rpe.data_cobranca) = pAno;

        EXCEPTION WHEN NO_DATA_FOUND THEN

          vDataRetroatividadePercentual2 := NULL;
          vFimRetroatividadePercentual2 := NULL;

      END;

      IF (vDataRetroatividadePercentual2 IS NOT NULL AND vFimRetroatividadePercentual2 IS NOT NULL) THEN

        IF (vDataRetroatividadePercentual IS NOT NULL AND vFimRetroatividadePercentual IS NOT NULL) THEN

          vDataRetroatividadePercentual := LEAST (vDataRetroatividadePercentual, vDataRetroatividadePercentual2);
          vFimRetroatividadePercentual := LEAST (vFimRetroatividadePercentual, vFimRetroatividadePercentual2);

        END IF;

        ELSE
 
          vDataRetroatividadePercentual := vDataRetroatividadePercentual2;
          vFimRetroatividadePercentual := vFimRetroatividadePercentual2;

      END IF;

    END IF;

    BEGIN

      IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 1) = TRUE AND F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 2) = TRUE) THEN

        vDataInicio := LEAST(vDataRetroatividadeConvencao, vDataRetroatividadePercentual);
        vDataFim := GREATEST(vFimRetroatividadeConvencao, vFimRetroatividadePercentual);

      ELSE

        IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 1) = TRUE) THEN
  
          vDataInicio := vDataRetroatividadeConvencao;
          vDataFim := vFimRetroatividadeConvencao;

        ELSE

          IF (F_COBRANCA_RETROATIVIDADE(pCodContrato, pMes, pANo, 2) = TRUE) THEN

            vDataInicio := vDataRetroatividadePercentual;
            vDataFim := vFimRetroatividadePercentual;

          END IF;
          
        END IF;
        
      END IF;

      vDataCobranca := vDataReferencia;

      P_CALCULA_RETROATIVIDADE(pCodContrato, vDataInicio, vDataFim, vDataCobranca);

    END;
  
  END IF;

  --EXCEPTION WHEN OTHERS THEN
  
    --DBMS_OUTPUT.PUT_LINE('Erro crítico no cálculo do total mensal a reter (Very enlightening).');

END;
