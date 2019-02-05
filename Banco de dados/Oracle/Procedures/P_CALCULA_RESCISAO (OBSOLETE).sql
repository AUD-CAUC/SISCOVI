create or replace procedure "P_CALCULA_RESCISAO" (pCodCargoFuncionario NUMBER, pCodTipoRestituicao NUMBER, pDataDesligamento DATE, pCodTipoRescisao CHAR) 
AS

  vDataDisponibilizacao DATE;

  vCodContrato NUMBER;
  vCodFuncionario NUMBER;
  vCodCargoContrato NUMBER;
  vCodTbRestituicaoRescisao NUMBER;
  vAno NUMBER;
  vMes NUMBER;

  vPercentualDecimoTerceiro FLOAT := 0;
  vPercentualIncidencia FLOAT := 0;
  vPercentualFGTS FLOAT := 0;
  vPercentualMultaFGTS FLOAT := 0;
  vPercentualPenalidadeFGTS FLOAT := 0;
  vPercentualFerias FLOAT := 0;
  
  vRemuneracao FLOAT := 0;
  
  vTotalMultaFGTSRemuneracao FLOAT := 0;
  
  vTotalDecimoTerceiro FLOAT := 0;
  vTotalIncidenciaDecimoTerceiro FLOAT := 0;
  vTotalMultaFGTSDecimoTerceiro FLOAT := 0;
  
  vTotalFerias FLOAT := 0;
  vTotalTercoConstitucional FLOAT := 0;
  vTotalIncidenciaFerias FLOAT := 0;
  vTotalIncidenciaTerco FLOAT := 0;
  vTotalMultaFGTSFerias FLOAT := 0;
  vTotalMultaFGTSTerco FLOAT := 0;

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
 
  --Eliminar essas variávels somente depois de confirmação da inutilidade.
  --vTotalMultaFGTSFerias2 FLOAT := 0;
  --vTotalMultaFGTSTerco2 FLOAT := 0;
  --vTotalIncidenciaTerco2 FLOAT := 0;
  --vTotalIncidenciaFerias2 FLOAT := 0;
  --vTotalMultaFGTSDecimoTerceiro2 FLOAT := 0;
  --vTotalIncidDecimoTerceiro2 FLOAT := 0;

  vDataInicioConvencao DATE;
  vDataFimConvencao DATE;
  vDataInicioPercentual DATE;
  vDataFimPercentual DATE;
  vDataReferencia DATE;
  vDataFimMes DATE;

  vDiasDeFerias NUMBER := 0;
  vDiasAdquiridos NUMBER := 0;
  vDiasVendidos NUMBER := 0;
  vNumeroDeMeses NUMBER := 0;
  vNumeroDeAnos NUMBER := 0;

BEGIN

  --Carregar o cod do funcionário, data de disponibilização e cod do contrato.

  SELECT cf.cod_funcionario,
         cf.data_disponibilizacao,
         cc.cod_contrato,
         cc.cod
    INTO vCodFuncionario,
         vDataDisponibilizacao,
         vCodContrato,
         vCodCargoContrato
    FROM tb_cargo_funcionario cf
      JOIN tb_cargo_contrato cc ON cc.cod = cf.cod_cargo_contrato
    WHERE cf.cod = pCodCargoFuncionario;

  vNumeroDeMeses := F_RETORNA_NUMERO_DE_MESES(vDataDisponibilizacao, pDataDesligamento);

  --Número de anos a serem contabilizados.

  SELECT (EXTRACT(year FROM pDataDesligamento) - EXTRACT(year FROM vDataDisponibilizacao)) + 1
    INTO vNumeroDeAnos
    FROM DUAL;
  
  --Cálculo dos valores relacionados ao 13° e a multa do FGTS sobre a remuneração.

  BEGIN

    --Definir o valor das variáveis vMes e vAno de acordo com a data de início da contagem.

    vMes := EXTRACT(month FROM vDataDisponibilizacao);
    vAno := EXTRACT(year FROM vDataDisponibilizacao);

    --O cálculo é feito mês a mês para preservar os efeitos das alterações contratuais.

    FOR i IN 1 .. vNumeroDeMeses LOOP

      --Se não existem alterações nos percentuais ou na convenção.

      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo e os percentuais de décimo terceiro e incidência.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
      
        --Se existe direito de décimo terceiro para aquele mês.           

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
	  
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + ((vRemuneracao * (vPercentualDecimoTerceiro/100)));
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100));
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)));
          --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100));
          --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)));
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)));

        END IF;

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = FALSE) THEN

          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_DIAS_TRABALHADOS_MES(pCodCargoFuncionario, vMes, vAno));              
        
        END IF;

      END IF;

      --Se existe alteração de convenção.
 
      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo para a primeira metade do mês e os percentuais do mês.
             
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 2, 2);
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
      
        --Se existe direito de décimo terceiro para aquele mês.          

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
	  
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));       
          
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));

          vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));

        END IF;

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = FALSE) THEN

          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));              
        
          vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);

          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));

        END IF;               
  
      END IF;

      --Se existe apenas alteração de percentual no mês.

      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a remuneração do cargo no mês e os percentuais do mês da primeira metade do mês.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 2, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 2, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 2, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 2, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 2, 2);
      
        --Se existe direito de férias para aquele mês.         

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN

	        vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
          vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
        
          vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));

        END IF;

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = FALSE) THEN

          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));              
        
          vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
          vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);

          vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));

        END IF;                 
  
      END IF;
    
      --Se existe alteração na convenção e nos percentuais.
    
      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a primeira remuneração do cargo no mês.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 2, 2);
      
        --Definição do percentual antigo.
      
        vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 2, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 2, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 2, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 2, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 2, 2);
      
        --Definição das datas para os períodos da convenção e percentuais.
      
        SELECT data_fim
          INTO vDataFimConvencao
          FROM tb_remuneracao_cargo_contrato
          WHERE cod_cargo_contrato = vCodCargoContrato
            AND data_aditamento IS NOT NULL
            AND (EXTRACT(month FROM data_fim) = vMes
                 AND EXTRACT(year FROM data_fim) = vAno);
               
        SELECT data_fim
          INTO vDataFimPercentual
          FROM tb_percentual_contrato
          WHERE cod_contrato = vCodContrato
            AND data_aditamento IS NOT NULL
            AND (EXTRACT(month FROM data_fim) = vMes
                 AND EXTRACT(year FROM data_fim) = vAno);
               
        vDataInicioConvencao := vDataFimConvencao + 1;
        vDataInicioPercentual := vDataFimPercentual + 1;
      
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
      
        --Convenção acaba antes do percentual.
      
        IF (vDataFimConvencao < vDataFimPercentual) THEN
       
          --Se existe direito de décimo terceiro para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
   
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração nova com percentual atigo.
          
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));

            --Definição do percentual novo.

            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
          
            ----Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
         
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));

          END IF;

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = FALSE) THEN

            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração nova com percentual atigo.
          
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));

            --Definição do percentual novo.

            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
          
            ----Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
         
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
          
          END IF;   

        END IF;
      
        --Convenção acaba depois do percentual.
      
        IF (vDataFimConvencao > vDataFimPercentual) THEN
      
          --Se existe direito de férias para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
        
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.
  
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

             --Definição do percentual novo.

            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração antiga com percentual novo.
          
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
          
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
        
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));

          END IF;

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = FALSE) THEN

            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.
  
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

             --Definição do percentual novo.

            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração antiga com percentual novo.
          
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
        
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));

          END IF;
        
        END IF;
      
        --Convenção acaba depois do percentual.
      
        IF (vDataFimConvencao = vDataFimPercentual) THEN
      
          --Se existe direito de férias para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
        
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));


            --Definição dos novos percentuais e da nova convenção .

            vPercentualDecimoTerceiro := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 3, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração nova com percentual novo.
          
            vTotalDecimoTerceiro := vTotalDecimoTerceiro + (((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalIncidenciaDecimoTerceiro := vTotalIncidenciaDecimoTerceiro + ((((vRemuneracao * (vPercentualDecimoTerceiro/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
      
            vTotalMultaFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalIncidDecimoTerceiro2 := vTotalIncidDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalMultaFGTSDecimoTerceiro2 := vTotalMultaFGTSDecimoTerceiro2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualDecimoTerceiro/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalMultaFGTSRemuneracao := vTotalMultaFGTSRemuneracao + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));


          END IF;
        
        END IF;    

      END IF;

      --Atualização dos valores finais totais devidos.

      IF (vMes = 12 OR (vMes = EXTRACT(month FROM pDataDesligamento) AND vAno = EXTRACT(year FROM pDataDesligamento))) THEN

        --Para o valor do décimo terceiro.

        IF (vTotalDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 3, 3) <= 0) THEN

          vTotalDecimoTerceiro := 0;

        ELSE

          vDecimoTerceiro :=  vDecimoTerceiro + (vTotalDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 3, 3));
          vTotalDecimoTerceiro := 0;

        END IF;

        --Para o valor da incidência do décimo terceiro.

        IF (vTotalIncidenciaDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 3, 103) <= 0) THEN

          vTotalIncidenciaDecimoTerceiro := 0;

        ELSE

          vIncidSubmod41DecTer :=  vIncidSubmod41DecTer + (vTotalIncidenciaDecimoTerceiro - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 3, 103));
          vTotalIncidenciaDecimoTerceiro := 0;

        END IF;
    
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
  
    IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

      vTotalIncidenciaDecimoTerceiro := 0;

    END IF;
    
  END;

  --Cálculos relacionados às férias. 

  BEGIN

    --Definir o valor das variáveis vMes e vAno de acordo com a data de início do período aquisitivo.

    vMes := EXTRACT(month FROM vDataDisponibilizacao);
    vAno := EXTRACT(year FROM vDataDisponibilizacao);

    --O cálculo é feito mês a mês para preservar os efeitos das alterações contratuais.

    FOR i IN 1 .. vNumeroDeMeses LOOP

      --Se não existem alterações nos percentuais ou na convenção.

      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo e os percentuais de férias e incidência.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
      
        --Se existe direito de férias para aquele mês.           

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
	  
          vTotalFerias := vTotalFerias + ((vRemuneracao * (vPercentualFerias/100)));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (vRemuneracao * ((vPercentualFerias/100)/3));
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100));
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100));
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)));
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)));
          --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100));
          --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)));
          --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100));
          --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)));

        END IF;               
  
      END IF;

      --Se existe alteração de convenção.

      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = FALSE) THEN
    
        --Define a remuneração do cargo para a primeira metade do mês e os percentuais do mês.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 2, 2);
        vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
      
        --Se existe direito de férias para aquele mês.         

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
	  
          vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
          --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));

          vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        
          vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));
          --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 2));


        END IF;               
  
      END IF;

      --Se existe apenas alteração de percentual no mês.

      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = FALSE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a remuneração do cargo no mês e os percentuais do mês da primeira metade do mês.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
        vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 2, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 2, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 2, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 2, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 2, 2);
      
        --Se existe direito de férias para aquele mês.         

        IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN

	        vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
          --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

          vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
          vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
          vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
          vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
          vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
        
          vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
          --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));

        END IF;               
  
      END IF;
    
      --Se existe alteração na convenção e nos percentuais.
    
      IF (F_EXISTE_DUPLA_CONVENCAO(vCodCargoContrato, vMes, vAno, 2) = TRUE AND F_EXISTE_MUDANCA_PERCENTUAL(vCodContrato, vMes, vAno, 2) = TRUE) THEN
    
        --Define a primeira remuneração do cargo no mês.
            
        vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 2, 2);
      
        --Definição do percentual antigo.
      
        vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 2, 2);
        vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 2, 2);
        vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 2, 2);
        vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 2, 2);
        vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 2, 2);
      
        --Definição das datas para os períodos da convenção e percentuais.
      
        SELECT data_fim
          INTO vDataFimConvencao
          FROM tb_remuneracao_cargo_contrato
          WHERE cod_cargo_contrato = vCodCargoContrato
            AND data_aditamento IS NOT NULL
            AND (EXTRACT(month FROM data_fim) = vMes
                 AND EXTRACT(year FROM data_fim) = vAno);
               
        SELECT data_fim
          INTO vDataFimPercentual
          FROM tb_percentual_contrato
          WHERE cod_contrato = vCodContrato
            AND data_aditamento IS NOT NULL
            AND (EXTRACT(month FROM data_fim) = vMes
                 AND EXTRACT(year FROM data_fim) = vAno);
               
        vDataInicioConvencao := vDataFimConvencao + 1;
        vDataInicioPercentual := vDataFimPercentual + 1;
      
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
      
        --Convenção acaba antes do percentual.
       
        IF (vDataFimConvencao < vDataFimPercentual) THEN
      
          --Se existe direito de férias para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
   
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração nova com percentual atigo.
            
            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30)  * (vDataFimPercentual - vDataInicioConvencao + 1));

            --Definição do percentual novo.

            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
            vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
          
            --Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
        
            vTotalFerias := vTotalFerias + (((vRemuneracao * (vPercentualFerias/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + (((((vRemuneracao * (vPercentualFerias/100)) + (vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
    
            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioPercentual + 1));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioPercentual + 1));

          END IF;
        
        END IF;
      
        --Convenção acaba depois do percentual.
      
        IF (vDataFimConvencao > vDataFimPercentual) THEN
      
          --Se existe direito de férias para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
        
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

            --Definição do percentual novo.

            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
            vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
            
            --Retenção proporcional da segunda porção do mês para a remuneração antiga com percentual novo.
          
            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimConvencao - vDataInicioPercentual + 1));

            --Definição da nova remuneração.
          
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
          
            --Retenção proporcional da terça parte do mês para a remuneração nova com percentual novo.
        
            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * (vDataFimMes - vDataInicioConvencao + 1));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * (vDataFimMes - vDataInicioConvencao + 1));

          END IF;
  
        END IF;
      
        --Convenção acaba depois do percentual.
      
        IF (vDataFimConvencao = vDataFimPercentual) THEN
      
          --Se existe direito de férias para aquele mês.         

          IF (F_FUNC_RETENCAO_INTEGRAL(pCodCargoFuncionario, vMes, vAno) = TRUE) THEN
        
            --Retenção proporcional da primeira porção do mês para a remuneração antiga com percentual antigo.

            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 3));

            --Definição dos novos percentuais e da nova convenção .

            vPercentualFerias := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 1, vMes, vAno, 1, 2);
            vPercentualIncidencia := F_RETORNA_PERCENTUAL_PERIODO(vCodContrato, 7, vMes, vAno, 1, 2);
            vRemuneracao := F_RETORNA_REMUNERACAO_PERIODO(vCodCargoContrato, vMes, vAno, 1, 2);
            vPercentualFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 4, vMes, vAno, 1, 2);
            vPercentualMultaFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 5, vMes, vAno, 1, 2);
            vPercentualPenalidadeFGTS := F_RETORNA_PERCENTUAL_PERIODO (vCodContrato, 6, vMes, vAno, 1, 2);
          
            --Retenção proporcional da segunda porção do mês para a remuneração nova com percentual novo.
          
            vTotalFerias := vTotalFerias + ((((vRemuneracao * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalTercoConstitucional := vTotalTercoConstitucional + (((vRemuneracao * ((vPercentualFerias/100)/3))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalIncidenciaFerias := vTotalIncidenciaFerias + ((((vRemuneracao * (vPercentualFerias/100)) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalIncidenciaTerco := vTotalIncidenciaTerco + (((((vRemuneracao * ((vPercentualFerias/100)/3))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalMultaFGTSFerias := vTotalMultaFGTSFerias + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            vTotalMultaFGTSTerco := vTotalMultaFGTSTerco + (((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalIncidenciaFerias2 := vTotalIncidenciaFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalMultaFGTSFerias2 := vTotalMultaFGTSFerias2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * (vPercentualFerias/100)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalIncidenciaTerco2 := vTotalIncidenciaTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * (vPercentualIncidencia/100))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));
            --vTotalMultaFGTSTerco2 := vTotalMultaFGTSTerco2 + ((((((vRemuneracao * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100) * ((vPercentualFerias/100)/3)))) / (((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)) + ((vPercentualIncidencia/100) + 1))) * ((vPercentualFGTS/100) * (vPercentualMultaFGTS/100) * (vPercentualPenalidadeFGTS/100)))/30) * F_RET_NUMERO_DIAS_MES_PARCIAL(vCodCargoContrato, vMes, vAno, 4));

          END IF;
        
        END IF;    

      END IF;
           
      --Atualização do mês e ano conforme a sequência do loop.
    
      IF (vMes != 12) THEN
    
        vMes := vMes + 1;
    
      ELSE

        vMes := 1;
        vAno := vAno + 1;    
    
      END IF;

    END LOOP;

    --Contabilização do valor final (valor calculado menos restituições).

    vAno := EXTRACT(year FROM vDataDisponibilizacao);

    FOR i IN 1 .. vNumeroDeAnos LOOP

      vTotalFerias := (vTotalFerias - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 2, 1));
      vTotalTercoConstitucional :=  (vTotalTercoConstitucional - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 2, 2));
      vTotalIncidenciaFerias :=  (vTotalIncidenciaFerias - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 2, 101));
      vTotalIncidenciaTerco :=  (vTotalIncidenciaTerco - F_SALDO_CONTA_VINCULADA (pCodCargoFuncionario, vAno, 2, 102));

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

    --Chave primária do registro a ser inserido na tabela tb_restituicao_rescisao.

    vCodTbRestituicaoRescisao := tb_restituicao_rescisao_cod.nextval;

    --Readequação das variáveis para a manutenção.
  
    IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

      vIncidDecTer := vIncidSubmod41DecTer;
      vFGTSDecimoTerceiro := vTotalMultaFGTSDecimoTerceiro;
      vIncidFerias := vIncidSubmod41Ferias;
      vIncidTerco :=vIncidSubmod41Terco;
      vFGTSFerias := vTotalMultaFGTSFerias;
      vFGTSTerco := vTotalMultaFGTSTerco;
      vFGTSRemuneracao := vTotalMultaFGTSRemuneracao;
      
      vIncidSubmod41DecTer := 0;
      vTotalMultaFGTSDecimoTerceiro := 0;
      vIncidSubmod41Ferias := 0;
      vIncidSubmod41Terco := 0;
      vTotalMultaFGTSFerias := 0;
      vTotalMultaFGTSTerco := 0;
      vTotalMultaFGTSRemuneracao := 0;

    END IF;

    /*
    DBMS_OUTPUT.PUT_LINE('Décimo terceiro total ("PURO"): R$ ' || ROUND(vTotalDecimoTerceiro,2));
    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre o 13°: R$ ' || ROUND(vTotalIncidenciaDecimoTerceiro,2));
    DBMS_OUTPUT.PUT_LINE('Multa do FGTS incidente sobre o 13°: R$ ' || ROUND(vTotalMultaFGTSDecimoTerceiro,2));
    
    DBMS_OUTPUT.PUT_LINE('Total de férias ("PURO"): R$ ' || ROUND(vTotalFerias,2));
    DBMS_OUTPUT.PUT_LINE('Total de terço constitucional: R$ ' || ROUND(vTotalTercoConstitucional,2));
    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre Férias: R$ ' || ROUND(vTotalIncidenciaFerias,2));
    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre o Terço: R$ ' || ROUND(vTotalIncidenciaTerco,2));
    DBMS_OUTPUT.PUT_LINE('Multa FGTS sobre férias: R$ ' || ROUND(vTotalMultaFGTSFerias,2));
    DBMS_OUTPUT.PUT_LINE('Multa FGTS sobre o terço constitucional: R$ ' || ROUND(vTotalMultaFGTSTerco,2));
    
    
    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre o 13° após resgate: R$ ' || ROUND(vTotalIncidDecimoTerceiro2,2));
    DBMS_OUTPUT.PUT_LINE('Multa do FGTS incidente sobre o 13° após resgate: R$ ' || ROUND(vTotalMultaFGTSDecimoTerceiro2,2));

    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre Férias após devolução: R$ ' || ROUND(vTotalIncidenciaFerias2,2));
    DBMS_OUTPUT.PUT_LINE('Incidência do submódulo 4.1 sobre o Terço após devolução: R$ ' || ROUND(vTotalIncidenciaTerco2,2));
    DBMS_OUTPUT.PUT_LINE('Multa FGTS sobre férias após devolução: R$ ' || ROUND(vTotalMultaFGTSFerias2,2));
    DBMS_OUTPUT.PUT_LINE('Multa FGTS sobre o terço constitucional após devolução: R$ ' || ROUND(vTotalMultaFGTSTerco2,2));
    DBMS_OUTPUT.PUT_LINE('Multa FGTS sobre a remuneração: R$ ' || ROUND(vTotalMultaFGTSRemuneracao,2));
    */
    
  END;
  
  --DBMS_OUTPUT.PUT_LINE(vDecimoTerceiro);
  
  INSERT INTO tb_restituicao_rescisao (cod,
                                       cod_cargo_funcionario,
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
              pCodCargoFuncionario,
              pCodTipoRestituicao,
              pCodTipoRescisao,
              pDataDesligamento,
              vDecimoTerceiro,
              vIncidSubmod41DecTer,
              vTotalMultaFGTSDecimoTerceiro,
              vFerias,
              vTerco,
              vIncidSubmod41Ferias,
              vIncidSubmod41Terco,
              vTotalMultaFGTSFerias,
              vTotalMultaFGTSTerco,
              vTotalMultaFGTSRemuneracao,
              SYSDATE,
              'SYSTEM',
              SYSDATE);

  
  IF (UPPER(F_RETORNA_TIPO_RESTITUICAO(pCodTipoRestituicao)) = 'MOVIMENTAÇÃO') THEN

    INSERT INTO tb_saldo_residual_rescisao (cod_restituicao_rescisao,
                                            incid_submod_4_1_dec_terceiro,
                                            incid_multa_fgts_dec_terceiro,
                                            incid_submod_4_1_ferias,
                                            incid_submod_4_1_terco,
                                            incid_multa_fgts_ferias,
                                            incid_multa_fgts_terco,
                                            multa_fgts_salario,
                                            restituido,
                                            login_atualizacao,
                                            data_atualizacao)
      VALUES (vCodTbRestituicaoRescisao,
              vIncidDecTer,
              vFGTSDecimoTerceiro,
              vIncidFerias,
              vIncidTerco,
              vFGTSFerias,
              vFGTSTerco,
              vFGTSRemuneracao, 
              'N',
              'SYSTEM',
              SYSDATE);

  END IF;

END;
