create or replace procedure "P_CALCULA_RESCISAO" (pCodTerceirizadoContrato NUMBER, pTipoRestituicao VARCHAR2, pDataDesligamento DATE, pTipoRescisao VARCHAR2) 
AS

  --Para DEBUG no ORACLE: DBMS_OUTPUT.PUT_LINE(vTotalFerias);

  --Chaves primárias

  vCodContrato NUMBER;
  vCodTbRestituicaoRescisao NUMBER;
  vCodTipoRescisao NUMBER;
  vCodTipoRestituicao NUMBER;

  --Variáveis totalizadoras de valores.

  vTotalFerias FLOAT := 0;
  vTotalTercoConstitucional FLOAT := 0;
  vTotalDecimoTerceiro FLOAT := 0;
  vTotalIncidenciaFerias FLOAT := 0;
  vTotalIncidenciaTerco FLOAT := 0;
  vTotalIncidenciaDecimoTerceiro FLOAT := 0;
  vTotalMultaFGTSRemuneracao FLOAT := 0;
  vTotalMultaFGTSFerias FLOAT := 0;
  vTotalMultaFGTSTerco FLOAT := 0;
  vTotalMultaFGTSDecimoTerceiro FLOAT := 0;

  --Variáveis de valores parciais.

  vValorFerias FLOAT := 0;
  vValorTercoConstitucional FLOAT := 0;
  vValorDecimoTerceiro FLOAT := 0;
  vValorIncidenciaFerias FLOAT := 0;
  vValorIncidenciaTerco FLOAT := 0;
  vValorIncidenciaDecimoTerceiro FLOAT := 0;
  vValorMultaFGTSRemuneracao FLOAT := 0;
  vValorMultaFGTSFerias FLOAT := 0;
  vValorMultaFGTSTerco FLOAT := 0;
  vValorMultaFGTSDecimoTerceiro FLOAT := 0;

  --Variáveis de percentuais.

  vPercentualFerias FLOAT := 0;
  vPercentualTercoConstitucional FLOAT := 0;
  vPercentualDecimoTerceiro FLOAT := 0;
  vPercentualIncidencia FLOAT := 0;
  vPercentualFGTS FLOAT := 0;
  vPercentualMultaFGTS FLOAT := 0;
  vPercentualPenalidadeFGTS FLOAT := 0;
   
  --Variável da remuneração da função do contrato.
  
  vRemuneracao FLOAT := 0;

  --Variáveis de datas.

  vDataDisponibilizacao DATE;
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
  vTipoRestituicao EXCEPTION;
  vTipoRescisao EXCEPTION;

  --Variáveis de controle.
  
  vDiasSubperiodo NUMBER := 0;
  vNumeroDeMeses NUMBER := 0;

-----------------------------------------------------------------

  --Variáveis de controle do saldo residual

  vIncidDecTer FLOAT := 0;
  vFGTSDecimoTerceiro FLOAT := 0;
  vIncidFerias FLOAT := 0;
  vIncidTerco FLOAT := 0;
  vFGTSFerias FLOAT := 0;
  vFGTSTerco FLOAT := 0;
  vFGTSRemuneracao FLOAT := 0;

  --Varíaveis que contém o valor final dos itens da rescisão.
  vDecimoTerceiro FLOAT := 0;
  vIncidSubmod41DecTer FLOAT := 0;
  vFerias FLOAT := 0;
  vTerco FLOAT := 0;
  vIncidSubmod41Ferias FLOAT := 0;
  vIncidSubmod41Terco FLOAT := 0;

BEGIN
  
  --Todos os parâmetros estão preenchidos.

  IF (pCodTerceirizadoContrato IS NULL OR
      pTipoRestituicao IS NULL OR
      pDataDesligamento IS NULL OR
      pTipoRescisao IS NULL) THEN
  
    RAISE vParametroNulo;
  
  END IF;

  --Checagem da validade do terceirizado passado (existe).

  SELECT COUNT(cod)
    INTO vCheck
    FROM tb_terceirizado_contrato
    WHERE cod = pCodTerceirizadoContrato;

  IF (vCheck = 0) THEN

    RAISE vTerceirizadoException;

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

  --Atribuição do cod do tipo de rescisão.

  BEGIN

    SELECT cod
      INTO vCodTipoRescisao
      FROM tb_tipo_rescisao
      WHERE UPPER(tipo_rescisao) = UPPER(pTipoRescisao);

    EXCEPTION WHEN NO_DATA_FOUND THEN

      vCodTipoRestituicao := NULL;

  END;

  IF (vCodTipoRestituicao IS NULL) THEN

    RAISE vTipoRescisao;

  END IF;

  --Carregar a data de disponibilização e o cod do contrato.

  SELECT tc.cod_contrato,
         tc.data_disponibilizacao
    INTO vCodContrato,
         vDataDisponibilizacao
    FROM tb_terceirizado_contrato tc 
    WHERE tc.cod = pCodTerceirizadoContrato;
/*
  --Número de anos a serem contabilizados.

  SELECT (EXTRACT(year FROM pDataDesligamento) - EXTRACT(year FROM vDataDisponibilizacao)) + 1
    INTO vNumeroDeAnos
    FROM DUAL;
*/
  --Carrega o número de meses que compreende o período de férias.
  
  vNumeroDeMeses := F_RETORNA_NUMERO_DE_MESES(vDataDisponibilizacao, pDataDesligamento);
  
  --Definir o valor das variáveis vMes e vAno de acordo com a data de disponibilização.

  vMes := EXTRACT(month FROM vDataDisponibilizacao);
  vAno := EXTRACT(year FROM vDataDisponibilizacao);

  --O cálculo é feito mês a mês para preservar os efeitos das alterações contratuais.

  FOR i IN 1 .. vNumeroDeMeses LOOP

    --Definição da data referência.

    vDataReferencia := TO_DATE('01/' || vMes || '/' || vAno, 'dd/mm/yyyy');

    --Reset das variáveis que contém valores parciais.

    vValorFerias := 0;
    vValorTercoConstitucional := 0;
    vValorDecimoTerceiro := 0;
    vValorIncidenciaFerias := 0;
    vValorIncidenciaTerco := 0;
    vValorIncidenciaDecimoTerceiro := 0;
    vValorMultaFGTSRemuneracao := 0;
    vValorMultaFGTSFerias := 0;
    vValorMultaFGTSTerco := 0;
    vValorMultaFGTSDecimoTerceiro := 0;

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
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 6, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 5, vMes, vAno, 1, 2);
            
        IF (vRemuneracao IS NULL) THEN
       
          RAISE vRemuneracaoException;
        
        END IF;

        --Cálculo do valor integral correspondente ao mês.      

        vValorFerias := (vRemuneracao * (vPercentualFerias/100));
        vValorTercoConstitucional := (vRemuneracao * (vPercentualTercoConstitucional/100));
        vValorDecimoTerceiro := (vRemuneracao * (vPercentualDecimoTerceiro/100));
        vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
        vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));
        vValorIncidenciaDecimoTerceiro := (vValorDecimoTerceiro * (vPercentualIncidencia/100));
        vValorMultaFGTSFerias := (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)));
        vValorMultaFGTSTerco := (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualTercoConstitucional/100)));
        vValorMultaFGTSDecimoTerceiro := (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)));
        vValorMultaFGTSRemuneracao := (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)));

        --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
        --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

        IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

          vValorFerias := (vValorFerias/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorTercoConstitucional := (vValorTercoConstitucional/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorDecimoTerceiro := (vValorDecimoTerceiro/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidenciaFerias := (vValorIncidenciaFerias/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidenciaTerco := (vValorIncidenciaTerco/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorIncidenciaDecimoTerceiro := (vValorIncidenciaDecimoTerceiro/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorMultaFGTSFerias := (vValorMultaFGTSFerias/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorMultaFGTSTerco := (vValorMultaFGTSTerco/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorMultaFGTSDecimoTerceiro := (vValorMultaFGTSDecimoTerceiro/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          vValorMultaFGTSRemuneracao := (vValorMultaFGTSRemuneracao/30) * F_DIAS_TRABALHADOS_MES(f.cod, vMes, vAno);
          
        END IF;

        vTotalFerias := vTotalFerias + vValorFerias;
        vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
        vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
        vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
        vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;
        vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
        vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
        vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
        vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
        vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;
           
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
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 1, vDataInicio, vDataFim, 2);     
          vPercentualTercoConstitucional := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 2, vDataInicio, vDataFim, 2);
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 3, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);
          vPercentualFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 4, vDataInicio, vDataFim, 2);
          vPercentualPenalidadeFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 6, vDataInicio, vDataFim, 2);
          vPercentualMultaFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 5, vDataInicio, vDataFim, 2);

          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));
          vValorIncidenciaDecimoTerceiro := (vValorDecimoTerceiro * (vPercentualIncidencia/100));
          vValorMultaFGTSFerias := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSTerco := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualTercoConstitucional/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSDecimoTerceiro := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSRemuneracao := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * vDiasSubperiodo;

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaDecimoTerceiro := (vValorIncidenciaDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSFerias := (vValorMultaFGTSFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSTerco := (vValorMultaFGTSTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSDecimoTerceiro := (vValorMultaFGTSDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSRemuneracao := (vValorMultaFGTSRemuneracao/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;   

          vDataInicio := vDataFim + 1;

        END LOOP;
  
      END IF;

      --Se existe alteração de remuneração apenas.
  
      IF (F_EXISTE_DUPLA_CONVENCAO(f.cod_funcao_contrato, vMes, vAno, 2) = TRUE AND F_MUNDANCA_PERCENTUAL_CONTRATO(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Definição dos percentuais, que não se alteram no período.
  
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(f.cod_funcao_contrato, vMes, vAno, 1, 2);
        vPercentualFerias := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 1, vMes, vAno, 1, 2);
        vPercentualTercoConstitucional := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 2, vMes, vAno, 1, 2);
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_CONTRATO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 6, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_ESTATICO(vCodContrato, 5, vMes, vAno, 1, 2);
    
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
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));
          vValorIncidenciaDecimoTerceiro := (vValorDecimoTerceiro * (vPercentualIncidencia/100));
          vValorMultaFGTSFerias := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSTerco := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualTercoConstitucional/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSDecimoTerceiro := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSRemuneracao := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * vDiasSubperiodo;

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaDecimoTerceiro := (vValorIncidenciaDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSFerias := (vValorMultaFGTSFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSTerco := (vValorMultaFGTSTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSDecimoTerceiro := (vValorMultaFGTSDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSRemuneracao := (vValorMultaFGTSRemuneracao/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
          
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;

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
  
          vPercentualFerias := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 1, vDataInicio, vDataFim, 2);     
          vPercentualTercoConstitucional := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 2, vDataInicio, vDataFim, 2);
          vPercentualDecimoTerceiro := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 3, vDataInicio, vDataFim, 2);
          vPercentualIncidencia := F_RET_PERCENTUAL_CONTRATO(vCodContrato, 7, vDataInicio, vDataFim, 2);
          vPercentualFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 4, vDataInicio, vDataFim, 2);
          vPercentualPenalidadeFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 6, vDataInicio, vDataFim, 2);
          vPercentualMultaFGTS := F_RET_PERCENTUAL_ESTATICO(vCodContrato, 5, vDataInicio, vDataFim, 2);

          --Calculo da porção correspondente ao subperíodo.
 
          vValorFerias := ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
          vValorTercoConstitucional := ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
          vValorDecimoTerceiro := ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
          vValorIncidenciaFerias := (vValorFerias * (vPercentualIncidencia/100));
          vValorIncidenciaTerco := (vValorTercoConstitucional * (vPercentualIncidencia/100));
          vValorIncidenciaDecimoTerceiro := (vValorDecimoTerceiro * (vPercentualIncidencia/100));
          vValorMultaFGTSFerias := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSTerco := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualTercoConstitucional/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSDecimoTerceiro := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * vDiasSubperiodo;
          vValorMultaFGTSRemuneracao := ((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * vDiasSubperiodo;

          --No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, 
          --situação similar para a retenção proporcional por menos de 14 dias trabalhados.

          IF (F_EXISTE_MUDANCA_FUNCAO(pCodTerceirizadoContrato, vMes, vAno) = TRUE OR F_FUNC_RETENCAO_INTEGRAL(f.cod, vMes, vAno) = FALSE) THEN

            vValorFerias := (vValorFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorTercoConstitucional := (vValorTercoConstitucional/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorDecimoTerceiro := (vValorDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaFerias := (vValorIncidenciaFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaTerco := (vValorIncidenciaTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorIncidenciaDecimoTerceiro := (vValorIncidenciaDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSFerias := (vValorMultaFGTSFerias/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSTerco := (vValorMultaFGTSTerco/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSDecimoTerceiro := (vValorMultaFGTSDecimoTerceiro/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
            vValorMultaFGTSRemuneracao := (vValorMultaFGTSRemuneracao/vDiasSubperiodo) * F_DIAS_TRABALHADOS_PERIOODO(f.cod, vDataInicio, vDataFim);
          
          END IF;

          vTotalFerias := vTotalFerias + vValorFerias;
          vTotalTercoConstitucional := vTotalTercoConstitucional + vValorTercoConstitucional;
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + vValorDecimoTerceiro;
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + vValorIncidenciaFerias;
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + vValorIncidenciaTerco;
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;    

          vDataInicio := vDataFim + 1;

        END LOOP;  
        
      END IF;

    END LOOP;

    --Atualização dos valores finais totais devidos.

    IF (vMes = 12 OR (vMes = EXTRACT(month FROM pDataDesligamento) AND vAno = EXTRACT(year FROM pDataDesligamento))) THEN
/*      
      --Para o valor do décimo terceiro.

      IF (vTotalDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 3) <= 0) THEN

        vTotalDecimoTerceiro := 0;

      ELSE

        vDecimoTerceiro :=  vDecimoTerceiro + (vTotalDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 3));
        vTotalDecimoTerceiro := 0;

      END IF;

      --Para o valor da incidência do décimo terceiro.

      IF (vTotalIncidenciaDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 103) <= 0) THEN

        vTotalIncidenciaDecimoTerceiro := 0;

      ELSE

        vIncidSubmod41DecTer :=  vIncidSubmod41DecTer + (vTotalIncidenciaDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 103));
        vTotalIncidenciaDecimoTerceiro := 0;

      END IF;
*/
      --Contabilização do valor final (valor calculado menos restituições).

      vTotalFerias := (vTotalFerias - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 1));
      vTotalTercoConstitucional :=  (vTotalTercoConstitucional - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 2));
      vTotalDecimoTerceiro := vTotalDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 3);
      vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 3, 103);
      vTotalIncidenciaFerias :=  (vTotalIncidenciaFerias - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 101));
      vTotalIncidenciaTerco :=  (vTotalIncidenciaTerco - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 102));
    
    END IF;
    
    --Atualização do mês e ano conforme a sequência do loop.
    
    IF (vMes != 12) THEN
    
      vMes := vMes + 1;
    
    ELSE
    
      vMes := 1;
      vAno := vAno + 1;    
   
    END IF;
   
  END LOOP;

  --A incidência não é restituída para o empregado, portanto na movimentação
  --ela não deve ser computada. 
  
  IF (UPPER(pTipoRestituicao) = 'MOVIMENTAÇÃO') THEN

    vTotalIncidenciaDecimoTerceiro := 0;

  END IF;

------------------------------------------------------------------------------------------
/*
  --Contabilização do valor final (valor calculado menos restituições).

  vAno := EXTRACT(year FROM vDataDisponibilizacao);

  FOR i IN 1 .. vNumeroDeAnos LOOP

    vTotalFerias := (vTotalFerias - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 1));
    vTotalTercoConstitucional :=  (vTotalTercoConstitucional - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 2));
    vTotalIncidenciaFerias :=  (vTotalIncidenciaFerias - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 101));
    vTotalIncidenciaTerco :=  (vTotalIncidenciaTerco - F_SALDO_CONTA_VINCULADA (pCodTerceirizadoContrato, vAno, 2, 102));

    vAno := vAno + 1;

  END LOOP;

  IF (vTotalFerias >= 0) THEN

    vFerias := vTotalFerias;

  END IF;

  IF (vTotalTercoConstitucional >= 0) THEN

    vTerco := vTotalTercoConstitucional;

  END IF;

  IF (vTotalIncidenciaFerias >= 0) THEN

    vIncidSubmod41Ferias := vTotalIncidenciaFerias;

  END IF;

  IF (vTotalIncidenciaTerco >= 0) THEN

    vIncidSubmod41Terco := vTotalIncidenciaTerco;

  END IF;
*/

  --Chave primária do registro a ser inserido na tabela tb_restituicao_rescisao.

  vCodTbRestituicaoRescisao := tb_restituicao_rescisao_cod.nextval;

  --Readequação das variáveis para a manutenção.
  
  IF (UPPER(pTipoRestituicao) = 'MOVIMENTAÇÃO') THEN

    vIncidDecTer := vTotalIncidenciaDecimoTerceiro;
    vIncidFerias := vTotalIncidenciaFerias;
    vIncidTerco := vTotalIncidenciaTerco;
    vFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro;
    vFGTSFerias := vTotalMultaFGTSFerias;
    vFGTSTerco := vTotalMultaFGTSTerco;
    vFGTSRemuneracao := vTotalMultaFGTSRemuneracao;
      
    vTotalIncidenciaDecimoTerceiro := 0;
    vTotalIncidenciaFerias := 0;
    vTotalIncidenciaTerco := 0;
    vTotalMultaFGTSDecimoTerceiro := 0;
    vTotalMultaFGTSFerias := 0;
    vTotalMultaFGTSTerco := 0;
    vTotalMultaFGTSRemuneracao := 0;

  END IF;
  
/*
  DBMS_OUTPUT.PUT_LINE(vTotalDecimoTerceiro);
  DBMS_OUTPUT.PUT_LINE(vTotalIncidenciaDecimoTerceiro);
  DBMS_OUTPUT.PUT_LINE(vTotalMultaFGTSDecimoTerceiro);
  DBMS_OUTPUT.PUT_LINE(vTotalFerias);
  DBMS_OUTPUT.PUT_LINE(vTotalTercoConstitucional);
  DBMS_OUTPUT.PUT_LINE(vTotalIncidenciaFerias);
  DBMS_OUTPUT.PUT_LINE(vTotalMultaFGTSFerias);,
  DBMS_OUTPUT.PUT_LINE(vTotalMultaFGTSTerco);
  DBMS_OUTPUT.PUT_LINE(vTotalMultaFGTSRemuneracao);
*/
  
  INSERT INTO tb_restituicao_rescisao (cod,
                                       cod_terceirizado_contrato,
                                       cod_tipo_restituicao,
                                       cod_tipo_rescisao,
                                       data_desligamento,
                                       valor_decimo_terceiro,
                                       incid_submod_4_1_dec_terceiro,
                                       incid_multa_fgts_dec_terceiro,
                                       valor_ferias,
                                       valor_terco,
                                       incid_submod_4_1_ferias,
                                       incid_submod_4_1_terco,
                                       incid_multa_fgts_ferias,
                                       incid_multa_fgts_terco,
                                       multa_fgts_salario,
                                       data_referencia,
                                       login_atualizacao,
                                       data_atualizacao)
      VALUES (vCodTbRestituicaoRescisao,
              pCodTerceirizadoContrato,
              vCodTipoRestituicao,
              vCodTipoRescisao,
              pDataDesligamento,
              vTotalDecimoTerceiro,
              vTotalIncidenciaDecimoTerceiro,
              vTotalMultaFGTSDecimoTerceiro,
              vTotalFerias,
              vTotalTercoConstitucional,
              vTotalIncidenciaFerias,
              vTotalIncidenciaTerco,
              vTotalMultaFGTSFerias,
              vTotalMultaFGTSTerco,
              vTotalMultaFGTSRemuneracao,
              SYSDATE,
              'SYSTEM',
              SYSDATE);

  
  IF (UPPER(pTipoRestituicao) = 'MOVIMENTAÇÃO') THEN

    INSERT INTO tb_saldo_residual_rescisao (cod_restituicao_rescisao,
                                            valor_decimo_terceiro,
                                            incid_submod_4_1_dec_terceiro,
                                            incid_multa_fgts_dec_terceiro,
                                            valor_ferias,
                                            valor_terco,
                                            incid_submod_4_1_ferias,
                                            incid_submod_4_1_terco,
                                            incid_multa_fgts_ferias,
                                            incid_multa_fgts_terco,
                                            multa_fgts_salario,
                                            restituido,
                                            login_atualizacao,
                                            data_atualizacao)
      VALUES (vCodTbRestituicaoRescisao,
              0,
              vIncidDecTer,
              vFGTSDecimoTerceiro,
              0,
              0,
              vIncidFerias,
              vIncidTerco,
              vFGTSFerias,
              vFGTSTerco,
              vFGTSRemuneracao, 
              'N',
              'SYSTEM',
              SYSDATE);

  END IF;

  EXCEPTION 
  
    WHEN vRemuneracaoException THEN

      RAISE_APPLICATION_ERROR(-20001, 'Erro na execução do procedimento: Remuneração não encontrada.');

    WHEN vPeriodoException THEN

      RAISE_APPLICATION_ERROR(-20002, 'Erro na execução do procedimento: Período fora da vigência contratual.');
  
    WHEN vTerceirizadoException THEN

      RAISE_APPLICATION_ERROR(-20003, 'Erro na execução do procedimento: Contrato inexistente.');

    WHEN vTipoRescisao THEN

      RAISE_APPLICATION_ERROR(-20006, 'O tipo de rescisão passado não foi encontrado.');

    WHEN vTipoRestituicao THEN

      RAISE_APPLICATION_ERROR(-20007, 'O tipo de restituição passado não foi encontrado.');
    
    WHEN OTHERS THEN
  
      RAISE_APPLICATION_ERROR(-20004, 'Erro na execução do procedimento: Causa não detectada.');

END;
