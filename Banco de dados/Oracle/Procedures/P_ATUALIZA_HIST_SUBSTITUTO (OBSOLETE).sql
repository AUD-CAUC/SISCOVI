create or replace procedure "P_ATUALIZA_HIST_SUBSTITUTO" (pCodUsuario NUMBER, pCodContrato NUMBER, pTipoSubstituto VARCHAR2, pLoginAtuatizacao VARCHAR, pDataContrato DATE)
AS

  vDataInicio DATE;
  vDataFim DATE;
  vCodSubstitutoAtual NUMBER;
  vCodRegistro NUMBER;

BEGIN

  BEGIN

    IF (pTipoSubstituto = '1° SUBSTITUTO') THEN

      SELECT hsc.cod,
             hsc.cod_usuario
        INTO vCodRegistro,
             vCodSubstitutoAtual
        FROM tb_hist_substituto_contrato hsc
          JOIN tb_usuario u ON u.cod = hsc.cod_usuario
          JOIN tb_perfil p ON p.cod = u.cod_perfil
        WHERE cod_contrato = pCodContrato
          AND data_fim IS NULL
          AND UPPER(p.sigla) = '1° SUBSTITUTO';

    END IF;

    IF (pTipoSubstituto = '2° SUBSTITUTO') THEN

      SELECT hsc.cod,
             hsc.cod_usuario
        INTO vCodRegistro,
             vCodSubstitutoAtual
        FROM tb_hist_substituto_contrato hsc
          JOIN tb_usuario u ON u.cod = hsc.cod_usuario
          JOIN tb_perfil p ON p.cod = u.cod_perfil
        WHERE cod_contrato = pCodContrato
          AND data_fim IS NULL
          AND UPPER(p.sigla) = '2° SUBSTITUTO';

    END IF;

      EXCEPTION WHEN NO_DATA_FOUND THEN

        vDataInicio := pDataContrato;
        vCodSubstitutoAtual := NULL;

  END;
  
  --Caso não exista nehum gesto para o contrato.

  IF (vCodSubstitutoAtual IS NULL) THEN

    INSERT INTO TB_HIST_SUBSTITUTO_CONTRATO (cod_contrato,
                                             cod_usuario,
                                             data_inicio,
                                             login_atualizacao,
                                             data_atualizacao)
      VALUES (pCodContrato,
              pCodUsuario,
              pDataContrato,
              pLoginAtuatizacao,
              SYSDATE);

  END IF;
  
  --Caso se esteja mudando o gestor do contrato.

  IF (vCodSubstitutoAtual IS NOT NULL AND vCodSubstitutoAtual != pCodUsuario) THEN

    INSERT INTO TB_HIST_SUBSTITUTO_CONTRATO (cod_contrato,
                                             cod_usuario,
                                             data_inicio,
                                             login_atualizacao,
                                             data_atualizacao)
      VALUES (pCodContrato,
              pCodUsuario,
              SYSDATE,
              pLoginAtuatizacao,
              SYSDATE);

    UPDATE TB_HIST_SUBSTITUTO_CONTRATO 
      SET data_fim = SYSDATE - 1
      WHERE data_fim IS NULL
        AND cod_contrato = pCodContrato
        AND cod_usuario = vCodSubstitutoAtual;
        
  END IF;

END;
