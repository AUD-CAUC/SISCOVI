create or replace function "F_RETORNA_DIAS_FUNCAO" (pCodFuncaoTerceirizado NUMBER, pMes NUMBER, pAno NUMBER) RETURN NUMBER
IS 

  --Função que retorna o número de dias que um terceirizado exerceu em determinada função em um mês.

  vDataInicioFuncao DATE := NULL;
  vDataFimFuncao DATE := NULL;
  vDataReferencia DATE := NULL;
  vRetorno NUMBER := NULL;

BEGIN

  --Define como data referência o primeiro dia do mês de acordo com os argumentos.

  vDataReferencia := TO_DATE('01/' || pMes || '/' || pAno, 'dd/mm/yyyy');

  --Seleciona a data de inicio e fim do terceirizado na função.
  
  SELECT data_inicio,
         data_fim
    INTO vDataInicioFuncao,
         vDataFimFuncao
    FROM tb_funcao_terceirizado
    WHERE cod = pCodFuncaoTerceirizado;

  --Se a data de inicio na função foir inferior a data referência, a variável recebe a data referência.

  IF (vDataInicioFuncao < vDataReferencia) THEN

    vDataInicioFuncao := vDataReferencia;

  END IF;

  --Se a data fim na função for nula, a variável recebe o último dia do mês.

  IF (vDataFimFuncao IS NULL OR vDataFimFuncao > LAST_DAY(vDataReferencia)) THEN

    vDataFimFuncao := LAST_DAY(vDataReferencia);

  END IF;

  --Retorno recebe o número de dias trabalhados entre o inicio e o fim na função naquele mês.

  vRetorno := (vDataFimFuncao - vDataInicioFuncao) + 1;

  --Em havendo valores negativos o retorno é zerado.

  IF (vRetorno < 0) THEN

    vRetorno := 0;

  END IF;

  RETURN vRetorno;

END;



   

