create or replace function "F_RETORNA_NUMERO_DE_MESES" (pDataInicio DATE, pDataFim DATE) RETURN NUMBER
IS

--Função que retorna o número de mêses que deverão ser levados em consideração entre duas datas.

  vRetorno NUMBER := 0;

BEGIN

  --Se as datas estão no mesmo ano.

  IF (EXTRACT(year FROM pDataInicio) = EXTRACT(year FROM pDataFim)) THEN

    vRetorno := (EXTRACT(month FROM pDataFim) - EXTRACT(month FROM pDataInicio)) + 1;

  END IF;

  --Caso sejam anos diferentes.

  IF (EXTRACT(year FROM pDataInicio) != EXTRACT(year FROM pDataFim)) THEN

    --Loop para o período de anos determinado.

    FOR i IN EXTRACT(year FROM pDataInicio) .. EXTRACT(year FROM pDataFim) LOOP

      --O primeiro ano é calculado em relação ao fim dele (mês 12).

      IF (i = EXTRACT(year FROM pDataInicio)) THEN
 
        vRetorno := 12 - EXTRACT(month FROM pDataInicio) + 1;

      END IF;
      
      --Os anos que estiverem entre o primeiro e o último acrescem 12 ao resultado final.
      
      IF (i != EXTRACT(year FROM pDataFim) AND i != EXTRACT(year FROM pDataInicio)) THEN
 
        vRetorno := vRetorno + 12;

      END IF;

      --O último ano soma o número de meses da data fim.

      IF (i = EXTRACT(year FROM pDataFim)) THEN
 
        vRetorno := vRetorno + EXTRACT(month FROM pDataFim);

      END IF;

    END LOOP;

  END IF;

  RETURN vRetorno;

END;
