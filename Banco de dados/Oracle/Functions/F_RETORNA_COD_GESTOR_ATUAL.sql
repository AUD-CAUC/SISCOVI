create or replace function "F_RETORNA_COD_GESTOR_ATUAL" (pCodContrato NUMBER) RETURN NUMBER
IS

  --Função que retorna o cod do gestor atual de um contrato.

  vCodUsuario NUMBER;

BEGIN

  SELECT hgc.cod_usuario
    INTO vCodUsuario
    FROM tb_historico_gestao_contrato hgc
      JOIN tb_perfil_gestao pg ON pg.cod = hgc.cod_perfil_gestao
    WHERE hgc.cod_contrato = pCodContrato
      AND UPPER(pg.sigla) = UPPER('GESTOR')
      AND (hgc.data_fim IS NULL
           OR
           hgc.data_fim = (SELECT MAX(data_fim)
                             FROM tb_historico_gestao_contrato
                             WHERE cod_contrato = pCodContrato
                               AND NOT EXISTS (SELECT cod
                                                 FROM tb_historico_gestao_contrato
                                                 WHERE cod_contrato = pCodContrato
                                                   AND data_fim IS NULL)));


  RETURN vCodUsuario;

  EXCEPTION WHEN NO_DATA_FOUND THEN
 
    RETURN NULL;

END;
