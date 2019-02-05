create or replace procedure "P_ATUALIZA_HISTORICO_GESTAO" (pCodUsuario NUMBER, pCodContrato NUMBER, pSiglaPerfil VARCHAR2, pLoginAtuatizacao VARCHAR, pDataContrato DATE)
AS

  --Procedimento que atualiza o histórico de gestão do contrato.

  vDataInicio DATE;
  vDataFim DATE;
  vCodGestorAtual NUMBER;
  vCodRegistro NUMBER;
  vCodPerfilGestao NUMBER;
  
  perfil_gestao_inexistente EXCEPTION;

BEGIN

  --Recupera o cod do perfil de gestão.

  BEGIN
  
    SELECT cod
      INTO vCodPerfilGestao
      FROM tb_perfil_gestao pg
      WHERE UPPER(pg.sigla) = UPPER(pSiglaPerfil);
      
    EXCEPTION WHEN NO_DATA_FOUND THEN
    
      vCodPerfilGestao := NULL;
  
  END;
  
  --Aborta caso não exista o perfil de gestão passado.
  
  IF (vCodPerfilGestao IS NULL) THEN
  
    RAISE perfil_gestao_inexistente;
    
    RETURN;
    
  END IF;

  --Seleciona o último usuário que ocupou a função dada como argumento em pSiglaPerfil baseando-se na data fim nula.

  BEGIN

    SELECT hgc.cod,
           hgc.cod_usuario
      INTO vCodRegistro,
           vCodGestorAtual
      FROM tb_historico_gestao_contrato hgc
      WHERE hgc.cod_contrato = pCodContrato
        AND hgc.cod_perfil_gestao = vCodPerfilGestao
        AND data_fim IS NULL;

    EXCEPTION WHEN NO_DATA_FOUND THEN

      vDataInicio := pDataContrato;
      vCodGestorAtual := NULL;

  END;
  
  --Caso não exista nehum gestor para o contrato.

  IF (vCodGestorAtual IS NULL) THEN

    INSERT INTO tb_historico_gestao_contrato (cod_contrato,
                                              cod_usuario,
                                              cod_perfil_gestao,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
      VALUES (pCodContrato,
              pCodUsuario,
              vCodPerfilGestao,
              pDataContrato,
              pLoginAtuatizacao,
              SYSDATE);

  END IF;
  
  --Caso se esteja mudando o gestor do contrato.

  IF (vCodGestorAtual IS NOT NULL AND vCodGestorAtual != pCodUsuario) THEN

    INSERT INTO TB_HISTORICO_GESTAO_CONTRATO (cod_contrato,
                                              cod_usuario,
                                              cod_perfil_gestao,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
      VALUES (pCodContrato,
              pCodUsuario,
              vCodPerfilGestao,
              SYSDATE,
              pLoginAtuatizacao,
              SYSDATE);

    UPDATE TB_HISTORICO_GESTAO_CONTRATO 
      SET data_fim = SYSDATE - 1
      WHERE cod = vCodRegistro;
        
  END IF;

END;
