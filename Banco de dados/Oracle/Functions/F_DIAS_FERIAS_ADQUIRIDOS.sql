create or replace function "F_DIAS_FERIAS_ADQUIRIDOS" (pDataInicio DATE, pDataFim DATE) RETURN NUMBER
IS

  --Função que retorna o número de dias que um terceirizado
  --possui em um determinado período aquisitivo.

  vDiasAUsufruir NUMBER := 0;
  vContagemDeDias NUMBER := 0;
  vDataInicio DATE := NULL;
  vDataFim DATE := NULL;

  vPeriodo EXCEPTION;

BEGIN

  --Calcula o número de dias baseado no período aquisitivo.

  LOOP

    --Inicializa a data de início no primeiro laço.

    IF (vDataInicio IS NULL) THEN

      vDataInicio := pDataInicio;

    END IF;

    --Define o fim do mês como dia 30 exceto para fevereiro.

    IF (EXTRACT(MONTH FROM vDataInicio) != 2) THEN

      vDataFim := TO_DATE('30/' || EXTRACT(MONTH FROM vDataInicio) || '/' || EXTRACT(YEAR FROM vDataInicio), 'dd/mm/yyyy');

    ELSE

      vDataFim := LAST_DAY(vDataInicio);

    END IF;

    --Ajusta a data fim para o final do período aquisitivo no mês correspondente.

    IF ((EXTRACT(MONTH FROM vDataFim) = EXTRACT(MONTH FROM pDataFim)) AND (EXTRACT(YEAR FROM vDataFim) = EXTRACT(YEAR FROM pDataFim)) AND (EXTRACT(DAY FROM pDataFim) != 31)) THEN

      vDataFim := pDataFim;

    END IF;

    vContagemDeDias := vContagemDeDias + ((vDataFim - vDataInicio) + 1);

    --Para o mês de fevereiro se equaliza o número de dias contados.

    IF (EXTRACT(MONTH FROM vDataFim) = 2) THEN
  
      --Se o mês for de 28 dias então soma-se 2 a contagem.

      IF (EXTRACT(DAY FROM vDataFim) = 28) THEN

        vContagemDeDias := vContagemDeDias + 2;

      ELSE

        --Se o mês não for de 28 dias ele é de 29.
        
        IF (EXTRACT(DAY FROM vDataFim) = 29) THEN
     
          vContagemDeDias := vContagemDeDias + 1;

        END IF;

      END IF;

    END IF;

    vDataInicio := LAST_DAY(vDataInicio) + 1;    

    EXIT WHEN ((EXTRACT(MONTH FROM vDataFim) = EXTRACT(MONTH FROM pDataFim)) AND (EXTRACT(YEAR FROM vDataFim) = EXTRACT(YEAR FROM pDataFim)));
  
  END LOOP;

  IF (vContagemDeDias > 360) THEN

    RAISE vPeriodo;

  END IF;
  
  --A cada 12 dias de trabalho o funcionário adquire 1 dias de férias,
  --considerando um período de 360 dias, óbviamente.

  vDiasAUsufruir := vContagemDeDias *  (30/360);

  RETURN vDiasAUsufruir;
  
  EXCEPTION WHEN vPeriodo THEN
  
    RAISE_APPLICATION_ERROR(-20001, 'O período aquisitivo informado gera uma contagem superior a 360 dias.');

END;
