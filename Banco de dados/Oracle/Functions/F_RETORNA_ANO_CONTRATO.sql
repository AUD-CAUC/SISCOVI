create or replace function "F_RETORNA_ANO_CONTRATO"(pCodContrato NUMBER) RETURN VARCHAR
IS

  --Função que retorna o ano do contrato baseado na data de início deste.

  vAnoContrato VARCHAR(4);

BEGIN

  SELECT TO_CHAR(MIN(ec.data_inicio_vigencia), 'yyyy')
    INTO vAnoContrato
    FROM tb_contrato c
      JOIN tb_evento_contratual ec ON ec.cod_contrato = c.cod
      JOIN tb_tipo_evento_contratual tec ON tec.cod = ec.cod_tipo_evento
    WHERE c.cod = pCodContrato
      AND (UPPER(tec.tipo) = 'CONTRATO' OR (ec.PRORROGACAO = 'S'));

  RETURN vAnoContrato;

  EXCEPTION WHEN OTHERS THEN 
  
    RETURN NULL;

END;
