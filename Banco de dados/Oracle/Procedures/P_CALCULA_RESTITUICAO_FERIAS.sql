create or replace procedure "P_CALCULA_RESTITUICAO_FERIAS" (pCodTerceirizadoContrato NUMBER,
                                                            pTipoRestituicao VARCHAR2,
                                                            pDiasVendidos NUMBER,
                                                            pInicioFerias DATE,
                                                            pFimFerias DATE, 
                                                            pInicioPeriodoAquisitivo DATE, 
                                                            pFimPeriodoAquisitivo DATE,
                                                            pValorMovimentado FLOAT,
                                                            pProporcional CHAR) AS

  --Procedure que calcula e faz o registro de restituição de férias no banco de dados.
  --Consideram-se períodos de 12 meses.

  --Para DEBUG no ORACLE: DBMS_OUTPUT.PUT_LINE(vTotalFerias);

  --Chaves primárias

  vCodContrato NUMBER;
  vCodTbRestituicaoFerias NUMBER;
  vCodTipoRestituicao NUMBER;

  --Variáveis totalizadoras de valores.

  vTotalFerias FLOAT := 0;
  vTotalTercoConstitucional FLOAT := 0;
  vTotalIncidenciaFerias FLOAT := 0;
  vTotalIncidenciaTerco FLOAT := 0;

  --Variáveis de valores parciais.

  vValorFerias FLOAT := 0;
  vValorTercoConstitucional FLOAT := 0;
  vValorIncidenciaFerias FLOAT := 0;
  vValorIncidenciaTerco FLOAT := 0;

  --Variáveis de percentuais.

  vPercentualFerias FLOAT := 0;
  vPercentualTercoConstitucional FLOAT := 0;
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

  --Variáveis de controle.
  
  vDiasDeFerias NUMBER := 0;
  vDiasAdquiridos NUMBER := 0;
  vDiasVendidos NUMBER := 0;
  vNumeroDeMeses NUMBER := 0;
  vControleMeses NUMBER := 0;
  vDiasSubperiodo NUMBER := 0;

  --Variáveis auxiliares.

  vIncidenciaFerias FLOAT := 0;
  vIncidenciaTerco FLOAT := 0;
  vTerco FLOAT := 0;
  vFerias FLOAT := 0;

  --Variáveis de exceção.

  vRemuneracaoException EXCEPTION;
  vPeriodoException EXCEPTION;
  vTerceirizadoException EXCEPTION;
  vParametroNulo EXCEPTION;
  vTipoRestituicao EXCEPTION;

BEGIN

  --Todos os parâmetros estão preenchidos.

  IF (pCodTerceirizadoContrato IS NULL OR
      pTipoRestituicao IS NULL OR
      pDiasVendidos IS NULL OR
      pInicioFerias IS NULL OR
      pFimFerias IS NULL OR
      pInicioPeriodoAquisitivo IS NULL OR
      pFimPeriodoAquisitivo IS NULL) THEN
  
    RAISE vParametroNulo;
  
  END IF;

  --Atribuição do cod do tipo de restituição.

  BEGIN

    SELECT cod
      INTO vCodTipoRestituicao
      FROM tb_tipo_restituicao
      WHERE UPPER(nome) = UPPER(pTipoRestituicao);

    EXCEPTION WHEN NO_DATA_FOUND THEN

      vCodTipoRestituicao := NULL;

  END;

  IF (vCodTipoRestituicao IS NULL) THEN

    RAISE vTipoRestituicao;

  END IF;

  --Checagem da validade do terceirizado passado (existe).

  SELECT COUNT(cod)
    INTO vCheck
    FROM tb_terceirizado_contrato
    WHERE cod = pCodTerceirizadoContrato;

  IF (vCheck = 0) THEN

    RAISE vTerceirizadoException;

  END IF;

  --Carrega o número de meses que compreende o período de férias.
  
  vNumeroDeMeses := F_RETORNA_NUMERO_DE_MESES(pInicioPeriodoAquisitivo, pFimPeriodoAquisitivo);
  
  --Carregar o cod do terceirizado e do contrato.

  SELECT tc.cod_contrato
    INTO vCodContrato
    FROM tb_terceirizado_contrato tc 
    WHERE tc.cod = pCodTerceirizadoContrato;

  --Definir o valor das variáveis vMes e vAno de acordo com a data de início do período aquisitivo.

  vMes := EXTRACT(month FROM pInicioPeriodoAquisitivo);
  vAno := EXTRACT(year FROM pInicioPeriodoAquisitivo);

  --O cálculo é feito mês a mês para preservar os efeitos das alterações contratuais.

  FOR i IN 1 .. vNumeroDeMeses LOOP

    --Definição da data referência.

    vDataReferencia := TO_DATE('01/' || vMes || '/' || vAno, 'dd/mm/yyyy');

    --Reset das variáveis que contém valores parciais.

    vValorFerias := 0;
    vValorTercoConstitucional := 0;
    vValorIncidenciaFerias := 0;
    vValorIncidenciaTerco := 0;

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
        vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 1, vMes, vAno, 1, 2);
        vPercentualTercoConstitucional := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 2, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 7, vMes, vAno, 1, 2);
     
        IF (vRemuneracao IS NULL) THEN
        
          RAISE vRemuneracaoException;
        
        END IF;

        --Cálculo do valor integral correspondente ao mês.      

        vValorFerias := (vRemuneracao * (vPercentualFerias/100));
        vValorTercoConstitucional := (vRemuneracao * (vPercentualTercoConstitucional/100));
        vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
        vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));

        --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
        --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

        IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

          vValorFerias := (vValorFerias/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorTercoConstitucional := (vValorTercoConstitucional/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidenciaFerias := (vValorIncidenciaFerias/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidenciaTerco := (vValorIncidenciaTerco/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          
        END IF;

        vTotalFerias := vTotalFerias + vValorFerias;
        vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
        vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
        vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;            
  
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

                   MINUS

                   SELECT CASE WHEN EXTRACT(DAY FROM LAST_DAY(TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy'))) = 31 THEN
                            TO_DATE('31/' || vMes || '/' || vAno, 'dd/mm/yyyy')
                          ELSE
                            NULL END AS data
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
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 1, vDataInicio, vDataFim, 2);     
          vPercentualTercoConstitucional := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 2, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);
        
          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;    

          vDataInicio := vDataFim + 1;

        END LOOP;
  
      END IF;

      --Se existe alteração de remuneração apenas.
  
      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = TRUE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Definição dos percentuais, que não se alteram no período.
  
        vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 1, vMes, vAno, 1, 2);
        vPercentualTercoConstitucional := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 2, vMes, vAno, 1, 2);
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

                   MINUS

                   SELECT CASE WHEN EXTRACT(DAY FROM LAST_DAY(TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy'))) = 31 THEN
                            TO_DATE('31/' || vMes || '/' || vAno, 'dd/mm/yyyy')
                          ELSE
                            NULL END AS data
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
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;    

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

                   MINUS

                   SELECT CASE WHEN EXTRACT(DAY FROM LAST_DAY(TO_DATE('30/' || vMes || '/' || vAno, 'dd/mm/yyyy'))) = 31 THEN
                            TO_DATE('31/' || vMes || '/' || vAno, 'dd/mm/yyyy')
                          ELSE
                            NULL END AS data
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
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 1, vDataInicio, vDataFim, 2);     
          vPercentualTercoConstitucional := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 2, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);

          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;    

          vDataInicio := vDataFim + 1;

        END LOOP;  
        
      END IF;

      vControleMeses := vControleMeses + 1;

    END LOOP;
    
    --Atualização do mês e ano conforme a sequência do loop.
    
    IF (vMes != 12) THEN
    
      vMes := vMes + 1;
    
    ELSE
    
      vMes := 1;
      vAno := vAno + 1;    
    
    END IF;
    
    IF (vControleMeses = 12) THEN
    
      EXIT; --Não pode calcular um 13° mês.
    
    END IF;

  END LOOP;
  
  --Atribuição de valor à vDiasVendidos.

  IF (pDiasVendidos IS NULL) THEN
  
    vDiasVendidos := 0;
    
  ELSE
  
    vDiasVendidos := pDiasVendidos;
    
  END IF;
  
  vCodTbRestituicaoFerias := tb_restituicao_ferias_cod_seq.nextval;
  
  --Total de dias de férias para cálculo proporcional da restituição.
  
  vDiasDeFerias := ((pFimFerias - pInicioFerias) + 1) + vDiasVendidos;
  
  --Dias de férias adquiridos durante o período aquisitivo.
  
  vDiasAdquiridos := F_DIAS_FERIAS_ADQUIRIDOS (vCodContrato, pCodTerceirizadoContrato, pInicioPeriodoAquisitivo, pFimPeriodoAquisitivo);
  
  --Definição do montante proporcional a ser restituiído.
  
  vTotalFerias := (vTotalFerias/vDiasAdquiridos) * vDiasDeFerias;
  vTotalIncidenciaFerias := (vTotalIncidenciaFerias/vDiasAdquiridos) * vDiasDeFerias;
  vTotalIncidenciaTerco := (vTotalIncidenciaTerco/vDiasAdquiridos) * vDiasDeFerias; 

  --Cancelamento do terço de férias para parcela diferente da única ou primeira.

  IF (F_PARCELAS_CONCEDIDAS_FERIAS(vCodContrato, pCodTerceirizadoContrato, pInicioPeriodoAquisitivo, pFimPeriodoAquisitivo) > 0) THEN

    vTotalTercoConstitucional := 0;

  END IF;
  
  --A incidência não é restituída para o empregado, portanto na movimentação
  --ela não deve ser computada, mas deve ser colocada como saldo residual.
  
  IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(vCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    vIncidenciaFerias :=  vTotalIncidenciaFerias;
    vIncidenciaTerco := vTotalIncidenciaTerco;
    
    vTerco := vTotalTercoConstitucional; 
    vFerias := vTotalFerias; 
    
    vTotalTercoConstitucional := pValorMovimentado/4;
    vTotalFerias := pValorMovimentado - vTotalTercoConstitucional;
    
    vTerco := vTerco - vTotalTercoConstitucional;
    vFerias := vFerias - vTotalFerias;

    vTotalIncidenciaFerias := 0;
    vTotalIncidenciaTerco := 0;

  END IF;
  
  --Gravação no banco.
  
  INSERT INTO tb_restituicao_ferias (cod,
                                     cod_terceirizado_contrato,
                                     cod_tipo_restituicao,
                                     data_inicio_periodo_aquisitivo,
                                     data_fim_periodo_aquisitivo,
                                     data_inicio_usufruto,
                                     data_fim_usufruto,
                                     valor_ferias,
                                     valor_terco_constitucional,
                                     incid_submod_4_1_ferias,
                                     incid_submod_4_1_terco,
                                     se_proporcional,
                                     dias_vendidos,
                                     data_referencia,
                                     login_atualizacao,
                                     data_atualizacao)
    VALUES (vCodTbRestituicaoFerias,
            pCodTerceirizadoContrato,
            vCodTipoRestituicao,
            pInicioPeriodoAquisitivo,
            pFimPeriodoAquisitivo,
            pInicioFerias,
            pFimFerias,
            vTotalFerias,
            vTotalTercoConstitucional,
            vTotalIncidenciaFerias,
            vTotalIncidenciaTerco,
            pProporcional,
            pDiasVendidos,
            SYSDATE,
           'SYSTEM',
            SYSDATE);

  IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(vCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    INSERT INTO tb_saldo_residual_ferias (cod_restituicao_ferias,
                                          valor_ferias,
                                          valor_terco,
                                          incid_submod_4_1_ferias,
                                          incid_submod_4_1_terco,
                                          restituido,
                                          login_atualizacao,
                                          data_atualizacao)
      VALUES (vCodTbRestituicaoFerias,
              vFerias,
              vTerco,
              vIncidenciaFerias,
              vIncidenciaTerco,
              'N',
              'SYSTEM',
              SYSDATE);

  END IF;

  EXCEPTION 
    
    WHEN vParametroNulo THEN

      RAISE_APPLICATION_ERROR(-20005, 'Falha no procedimento P_CALCULA_RESTITUICAO_FERIAS: parâmetro nulo.');

    WHEN vRemuneracaoException THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução do procedimento: Remuneração não encontrada.');

    WHEN vPeriodoException THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução do procedimento: Período fora da vigência contratual.');
  
    WHEN vTerceirizadoException THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução do procedimento: Terceirizado não encontrado no contrato.');

    WHEN vTipoRestituicao THEN

      RAISE_APPLICATION_ERROR(-20007, 'O tipo de restituição passado não foi encontrado.');

    WHEN NO_DATA_FOUND THEN

      RAISE_APPLICATION_ERROR(-20006, 'Dados não encontrados.');
    
    WHEN OTHERS THEN
  
      RAISE_APPLICATION_ERROR(-20004, 'Erro na execução do procedimento: Causa não detectada.');

END;