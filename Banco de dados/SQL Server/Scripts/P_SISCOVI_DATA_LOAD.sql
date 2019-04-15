USE [SISCOVI]
GO

/****** Object:  StoredProcedure [dbo].[P_SISCOVI_DATA_LOAD]    Script Date: 03/07/2018 15:57:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[P_SISCOVI_DATA_LOAD]
AS

BEGIN

  DECLARE @vDataDisponibilizacao DATE = '05/08/2016';
  DECLARE @vDataAtualizacao DATE = GETDATE();
  DECLARE @vLoginAtualizacao VARCHAR(15) = 'SYSTEM';
  DECLARE @vCodPerfilGestor INT;
  DECLARE @vCodPerfilAdmin INT;
  DECLARE @vCodGestor1 INT;
  DECLARE @vCodGestor2 INT;
  DECLARE @vCodContrato1 INT;
  DECLARE @vCodContrato2 INT;
  DECLARE @vCod13 INT;
  DECLARE @vCodSubMod INT;
  DECLARE @vCodTercoConstitucional INT;
  DECLARE @vCodFerias INT;
  DECLARE @vCodFGTS INT;
  DECLARE @vCodPenalidadeFGTS INT;
  DECLARE @vCodMultaFGTS INT;
  DECLARE @vCodCargoContrato1 INT;
  DECLARE @vCodCargoContrato2 INT;
  DECLARE @vCodFuncionario INT;
  DECLARE @vCount INT = 0;
  DECLARE @vCodCargo1 INT;
  DECLARE @vCodCargo2 INT;
  DECLARE @vCodPerfilUsuario INT;
  DECLARE @vCodPerfilGestao INT;
  DECLARE @vCodEventoContrato INT;
  DECLARE @vCodTerceirizado INT;
  DECLARE @vCodTerceirizadoContrato INT;

  DECLARE cur_funcionario CURSOR FOR 
    SELECT cod AS "cod_funcionario"
      FROM tb_terceirizado;

  --Insert em tb_perfil_usuario.
  
  INSERT INTO tb_perfil_usuario (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Administrador do sistema', 'ADMINISTRADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_perfil_usuario (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Usuário autorizado', 'USUÁRIO', @vLoginAtualizacao, @vDataAtualizacao);

  --Insert em tb_perfil_gestao.
  
  INSERT INTO tb_perfil_gestao (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (1, 'Gestor do contrato', 'GESTOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (2, '1° Substituto do gestor do contrato', '1° SUBSTITUTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (3, '2° Substituto do gestor do contrato', '2° SUBSTITUTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (4, '3° Substituto do gestor do contrato', '3° SUBSTITUTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (5, '4° Substituto do gestor do contrato', '4° SUBSTITUTO', @vLoginAtualizacao, @vDataAtualizacao);


  --Carregamento das variáveis de perfil

  SELECT @vCodPerfilGestor = cod
    FROM tb_perfil_usuario
    WHERE UPPER(sigla) = 'USUÁRIO';

  SELECT @vCodPerfilAdmin = cod
    FROM tb_perfil_usuario
    WHERE UPPER(sigla) = 'ADMINISTRADOR';

  --Insert em tb_usuario

  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilGestor, 'EDSON ARANTES DO NASCIMENTO', 'PELE', '$2a$10$KSJ./ss8UB471O.rxtOVpOSW1W.h7Up9EE2Xg8zZkjA3Qlw/CUNOO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilGestor, 'CRISTIANO RONALDO', 'CRONALDO', '$2a$10$iUkpU7ws5EPgUANP1Gd7Uuz.giSQ4tjilSgffGKkmzIh1zRWGa/Um', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilGestor, 'LIONEL MESSI', 'LMESSI', '$2a$10$2oyyMmYgu9IhUkOJsrAO/OgnUBGRpFeOEGRkbiARTsrOaxAs05Z86', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilAdmin, 'MATHEUS MIRANDA DE SOUSA', 'MMSOUSA', '$2a$10$2x4QECPV08i8MMIhVDFD0eGfBWlOAKOxWc/Xdiei7KuYWUuWu0IlC', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilAdmin, 'VINICIUS DE SOUSA SANTANA', 'VSSOUSA', '$2a$10$dwq63Qgvw0WtP9Vau2e6F.Ne7oSZEcdcOlRgYaR8pf0Uzdk1tytO2', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilAdmin, 'DIOCESIO SANTANNA DA SILVA', 'DIOCESIO', '$2a$10$3watKJAOWSmYWNt6.kIJtOHsAfx7raCAcLgAJB0NNZ95W7/dkZI8G', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (@vCodPerfilAdmin, 'MARIA ELIZABETH CANUTO CALAIS', 'MARIACAL', '$2a$10$tHc1Rr6XfNNZjd6y2.9eY.w13hj1e0uvFSFgTv7Gzz/QpffezNNke', @vLoginAtualizacao, @vDataAtualizacao); 

  --Insert em tb_rubricas
  
  SET IDENTITY_INSERT dbo.tb_rubrica ON;

  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (3, 'Décimo terceiro salário', '13°', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (7, 'Incidência do submódulo 4.1', 'Submódulo 4.1', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (2, 'Terço constitucional', 'Terço constitucional', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (1, 'Férias', 'Férias', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (5, 'Multa do FGTS', 'Multa FGTS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (4, 'FGTS', 'FGTS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (6, 'Penalidade FGTS', 'Penalidade FGTS', @vLoginAtualizacao, @vDataAtualizacao);
  
  SET IDENTITY_INSERT dbo.tb_rubrica OFF;

  --Carregamento das variáveis de gestor
  
  SELECT @vCodGestor1 = cod
    FROM tb_usuario
    WHERE UPPER(login) = 'CRONALDO';

  SELECT @vCodGestor2 = cod
    FROM tb_usuario
    WHERE UPPER(login) = 'LMESSI';

  --Carregamento da variável de perfil de gestão

  SELECT @vCodPerfilGestao = cod
    FROM tb_perfil_gestao
    WHERE UPPER(sigla) = 'GESTOR';

  --Insert em tb_contrato

  INSERT INTO tb_contrato (nome_empresa, cnpj, numero_contrato, numero_processo_stj, se_ativo, login_atualizacao, data_atualizacao) VALUES ('ELETRO CONTROLE ENGENHARIA COMÉRCIO e REPRESENTAÇÃO LTDA.', '00899223000132', '034/2016', '9424/2016', 'S', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_contrato (nome_empresa, cnpj, numero_contrato, numero_processo_stj, se_ativo, login_atualizacao, data_atualizacao) VALUES ('CAPITAL SERVICE - SERVIÇOS PROFISSIONAIS LTDA ME', '08414767000179', '004/2017', '16618/2016', 'S', @vLoginAtualizacao, @vDataAtualizacao);
  
  --Carregamento das variáveis de contrato
  
  SELECT @vCodContrato1 = cod
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = 'ELETRO CONTROLE ENGENHARIA COMÉRCIO e REPRESENTAÇÃO LTDA.';
    
  SELECT @vCodContrato2 = cod
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = 'CAPITAL SERVICE - SERVIÇOS PROFISSIONAIS LTDA ME';

  --Insert em tb_tipo_evento_contratual

  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('APOSTILAMENTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('CONTRATO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('TERMO ADITIVO', @vLoginAtualizacao, @vDataAtualizacao);

  --Carregamento da variável tipo evento
 
  SELECT @vCodEventoContrato = cod
    FROM tb_tipo_evento_contratual
    WHERE UPPER(tipo) = 'CONTRATO';

  --Insert em tb_historico_gestao_contrato

  INSERT INTO tb_historico_gestao_contrato (cod_contrato, cod_usuario, cod_perfil_gestao, data_inicio, login_atualizacao, datA_atualizacao) VALUES (@vCodContrato1, @vCodGestor1, @vCodPerfilGestao, '05/08/2016', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_historico_gestao_contrato (cod_contrato, cod_usuario, cod_perfil_gestao, data_inicio, login_atualizacao, datA_atualizacao) VALUES (@vCodContrato2, @vCodGestor2, @vCodPerfilGestao, '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
	
  --Insert em tb_evento_contratual

  INSERT INTO tb_evento_contratual (cod_contrato, cod_tipo_evento, prorrogacao, data_inicio_vigencia, data_fim_vigencia, data_assinatura, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodEventoContrato, 'N', '05/08/2016', '04/08/2017', '05/08/2016', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_evento_contratual (cod_contrato, cod_tipo_evento, prorrogacao, data_inicio_vigencia, data_fim_vigencia, data_assinatura, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodEventoContrato, 'N', '01/02/2017', '31/01/2018', '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
  
  --Carregamento das variáveis de percentual

  SELECT @vCod13 = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('13°');

  SELECT @vCodSubMod = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Submódulo 4.1');

  SELECT @vCodTercoConstitucional = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Terço constitucional');

  SELECT @vCodFerias = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Férias');

  SELECT @vCodFgts = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('FGTS');

  SELECT @vCodMultaFgts = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Multa FGTS');

  SELECT @vCodPenalidadeFgts = cod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Penalidade FGTS');  

  --Insert em tb_percentual_contrato

  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCod13, 9.09, '05/08/2016', '05/08/2016',@vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodSubMod, 36.8, '05/08/2016', '05/08/2016',@vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodTercoConstitucional, 3.03, '05/08/2016', '05/08/2016',@vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodFerias, 9.09, '05/08/2016', '05/08/2016',@vLoginAtualizacao, @vDataAtualizacao);
    
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCod13, 9.09, '01/02/2017', '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodSubMod, 35.30, '01/02/2017', '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodTercoConstitucional, 3.03, '01/02/2017', '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodFerias, 9.09, '01/02/2017', '01/02/2017', @vLoginAtualizacao, @vDataAtualizacao);
  
  --Insert em tb_percentual_estático

  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodFGTS, 8.0, '01/01/2000', '01/01/2000', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodMultaFGTS, 50.0, '01/01/2000', '01/01/2000', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (@vCodPenalidadeFGTS, 90.0, '01/01/2000', '01/01/2000', @vLoginAtualizacao, @vDataAtualizacao);

  --Insert em tb_tipo_rescisao

  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('SEM JUSTA CAUSA', 'SYSTEM', @vDataAtualizacao);
  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('A PEDIDO', 'SYSTEM', @vDataAtualizacao);
  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('COM JUSTA CAUSA', 'SYSTEM', @vDataAtualizacao);

  --Insert em tb_tipo_restituicao

  INSERT INTO tb_tipo_restituicao (nome, login_atualizacao, data_atualizacao) VALUES ('MOVIMENTAÇÃO', 'SYSTEM', @vDataAtualizacao);
  INSERT INTO tb_tipo_restituicao (nome, login_atualizacao, data_atualizacao) VALUES ('RESGATE', 'SYSTEM', @vDataAtualizacao);

  --Insert em tb_funcao

  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AGENTE DE SEGURANÇA PESSOAL PRIVADA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AGENTE DE SEGURANÇA PESSOAL PRIVADA (SUPERVISOR DE SERVIÇOS)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AJUDANTE DE BOMBEIRO HIDRÁULICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AJUDANTE DE ELETRICISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ALMOXARIFE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ALMOXARIFE (TÉCNICO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE REDE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE AOS MAGISTRADOS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE JR. DE BACKUP', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE JR. DE REDE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA JR.', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE TÉCNICO (INFORMÁTICA)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ASCENSORISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ASSISTENTE DE FOTOGRAFIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR ADMINISTRATIVO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE ALMOXARIFADO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE BIBLIOTECA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE EDUCAÇÃO INFANTIL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE HIGIENIZAÇÃO E ACONDICIONAMENTO DE ACERVOS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MANUTENÇÃO PREDIAL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MARCENARIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MECÂNICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MECÂNICO DE REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE OPERADOR DE CÂMERA UPE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE RECEPÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SERVIÇOS GERAIS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SUPERVISÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SUPERVISÃO DE MARCENARIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE TELEFONIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR TÉCNICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO HIDRÁULICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BORRACHEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('CHEFE DE REPORTAGEM DE RÁDIO E TV', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COPEIRA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COPEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COZINHEIRA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESENHISTA / VIDEOGRAFISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESENHISTA EM CAD', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO JÚNIOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO PLENO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO SÊNIOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DIGITALIZADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DIRETOR DE IMAGEM DE TV - DTV', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DOCUMENTADORA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE IMAGEM, PÓS PRODUÇÃO E FINALIZAÇÃO DE VÍDEO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE TEXTO DE RÁDIO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE TEXTO (TV)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA DE AUTOS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA PLANTONISTA (DIURNO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA PLANTONISTA (NOTURNO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE ELÉTRICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE MECÂNICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE HIDRÁULICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE MECÂNICA E REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO GERAL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO MECÂNICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENFERMEIRA AUDITORA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENFERMEIRA / SUPERVISORA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO COORDENADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO DE SISTEMAS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO ELETRICISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO MECÂNICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO RESIDENTE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO SÊNIOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('FATURISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GARÇOM', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GARÇONETE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GERENTE DE PROJETO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GERENTE TÉCNICO / PREPOSTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GESSEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ILUMINADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('INSTRUMENTADOR TÉCNICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('JARDINEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('JAUZEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('LAVADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('LUSTRADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MAQUIADOR / CABELEREIRO / FIGURINISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MARCENEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO DE REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO LEVE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO PESADO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MÉDICO AUDITOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MEIO OFICIAL ELETRICISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MENSAGEIRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MENSAGEIRO DE GABINETE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE AUTOMAÇÃO DA CENTRAL (DIURNO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE AUTOMAÇÃO DA CENTRAL (NOTURNO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE OPERAÇÃO CAG (DIURNO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MOTORISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OFICIAL ELETRICISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE CÂMERA UPE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE ÁGUA GELADA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE DETECÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE SUPERVISÃO PREDIAL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE ÁUDIO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE ÁUDIO E VÍDEO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE IMPRESSÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE IMPRESSÃO DIGITAL E ACABAMENTOS AFINS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MÁQUINA DE CARACTERES', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MICRO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE VÍDEO ', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE VT', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MESA TELEFÔNICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PEDAGOGA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PEDAGOGO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PINTOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PREPARADOR DE DOCUMENTOS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PREPOSTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PRODUTOR DE JORNALISMO, RÁDIO E TV', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('RECEPCIONISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPÓRTER DE RÁDIO E TV', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPÓRTER DE RÁDIO E TV / APRESENTADOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPORTER FOTOGRÁFICO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REVISOR DE TEXTO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SEGURANÇA PESSOAL PRIVADA RESIDENCIAL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SERVENTE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SONOPLASTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE 1° NÍVEL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE 2° NÍVEL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE DOCUMENTAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE JARDINAGEM', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE MARCENARIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE MENSAGEIRIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE VIGILÂNCIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR GERAL DE SERVENTES', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO CIVIL)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO ELETRICISTA)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO MECÂNICO)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (OPERACIONAL DE RÁDIO E TV)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR / LÍDER DE EQUIPE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISORA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPORTE TÉCNICO INTERNO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO 1° NÍVEL', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO 1° NÍVEL (SAA)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (ASP)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (JUDICIÁRIA)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (APP PREFERÊNCIAL)', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE ÁUDIO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE LEVADOR RESIDENTE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE VÍDEO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM CABEAMENTO E REDE ESTRUTURADA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM DESIGN', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM DETECÇÃO E ALARME DE INCÊNDIO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETRÔNICA I', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETRÔNICA II', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETROTÉCNICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO DE EDIFICAÇÕES', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MÁQUINAS', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO I', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO II', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SISTEMA DE COMUNICAÇÃO OPEN SCAPE VOIP', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM TELEFONIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM INFORMÁTICA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO DE HARDWARE', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO MECÂNICO EM REFRIGERAÇÃO', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TELEFONISTA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('VIGILANTE / VIGILÂNCIA', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('WEB DESIGNER', @vLoginAtualizacao, @vDataAtualizacao);

  --Insert em tb_funcao_contrato

  SELECT @vCodCargo1 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('ENGENHEIRO MECÂNICO');

  SELECT @vCodCargo2 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('ENCARREGADO MECÂNICO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo1, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo2, @vLoginAtualizacao, @vDataAtualizacao);

  SELECT @vCodCargo1 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO');

  SELECT @vCodCargo2 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo1, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo2, @vLoginAtualizacao, @vDataAtualizacao);

  SELECT @vCodCargo1 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO');

  SELECT @vCodCargo2 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('AUXILIAR ADMINISTRATIVO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo1, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato1, @vCodCargo2, @vLoginAtualizacao, @vDataAtualizacao);

  SELECT @vCodCargo1 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)');

  SELECT @vCodCargo2 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodCargo1, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodCargo2, @vLoginAtualizacao, @vDataAtualizacao);

  SELECT @vCodCargo2 = cod
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (@vCodContrato2, @vCodCargo2, @vLoginAtualizacao, @vDataAtualizacao);

  --Insert em tb_funcionarios

  

    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eliseu Padilha',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gilberto Kassab',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Moreira Franco',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Roberto Freire',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Araújo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aloysio Nunes Ferreira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcos Pereira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Blairo Maggi',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Helder Barbalho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Romero Jucá Filho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aécio Neves da Cunha',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Calheiros',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Bezerra Coelho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Rocha',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao) ;
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Humberto Sérgio Costa Lima',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edison Lobão',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cássio Cunha Lima',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jorge Viana',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lidice da Mata',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Agripino Maia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marta Suplicy',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ciro Nogueira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dalírio José Beber',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ivo Cassol',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lindbergh Farias',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vanessa Grazziotin',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Kátia Regina de Abreu',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Afonso Collor de Mello',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Serra',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Braga',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Omar Aziz',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdir Raupp',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eunício Oliveira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Amorim',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Carmo Alves',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Garibaldi Alves Filho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ricardo Ferraço',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Anastasia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulinho da Força',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marco Maia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Carlos Zarattini',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Maia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Carlos Bacelar',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Milton Monti',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Carlos Aleluia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Almeida',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Mário Negromonte Jr.',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Nelson Pellegrino',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jutahy Júnior',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Rosário',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Felipe Maia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ônix Lorenzoni',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jarbas de Andrade Vasconcelos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Paulo da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arthur Oliveira Maia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Yeda Crusius',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Henrique Lustosa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Reinaldo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Paulo Papa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vander Loubet',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Garcia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cacá Leão',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Celso Russomano',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dimas Fabiano Toledo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Pedro Paulo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lúcio Vieira Lima',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paes Landim',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Vilela',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Alfredo Nascimento',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca Dirceu',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Betinho Gomes',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca do PT',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Cândido',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Júlio Lopes',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fábio Faria',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Heráclito Fortes',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Beto Mansur',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Brito',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Décio Lima',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arlindo Chinaglia',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Filho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Robinson Faria',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Tião Viana',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vital do Rêgo Filho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rosalba Ciarlini',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdemar da Costa Neto',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luís Alberto Maguito Vilela',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edvaldo Pereira de Brito',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Oswaldo Borges da Costa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cândido Vaccarezza',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S',@vLoginAtualizacao,@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Martín Andrés Silva Leites',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Kelvin Mateus de Oliveira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Evander da Silva Ferreira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Duvier Orlando Riascos Barahona',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rildo de Andrade Felicíssimo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Wagner Ferreira dos Santos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Glaybson Yago Souza Lisboa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Leandro Luis Desábato',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Marcos de Jesus Ribeiro',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Giovanni Augusto Oliveira Cardoso',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Breno Vinícius Rodrigues Borges',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Andrés Lorenzo Ríos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Thiago Galhardo do Nascimento Rocha',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ramon de Morais Motta',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Wellington Aparecido Martins',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Frickson Rafael Erazo Vivero',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Henrique Silva Milagres',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcelo de Mattos Terra',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rafael Galhardo de Souza',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Werley Ananias da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Vitor Fernandes Pereira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jomar Herculano Lourenço',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Jacinto da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Caio Monteiro Costa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jordi Martins Almeida',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno da Silva Barbosa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gabriel Félix Dos Santos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luiz Gustavo Tavares Conde',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fabrício dos Santos Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Andrey Ramos do Nascimento',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Cosendey Lobo Pinto',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Miguel Kaufmann',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lucas Perdomo Duarte Santos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Pedro Soares Borges',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ricardo Queiroz de Alencastro Graça',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Alan Cardoso de Andrade',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Paolo Guerrero Gonzales',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lucas Tolentino Coelho de Lima',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vinícius José Paixão de Oliveira Júnior',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Diego Ribas da Cunha',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Henrique da Silva Dourado',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Éverton Augusto de Barros Ribeiro',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Orlando Enrique Berrío Meléndez',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Miguel Ángel Trauco Saavedra',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Juan Silveira dos Santos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ederson Honorato Campos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Felipe dos Reis Pereira Vizeu do Carmo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renê Rodrigues Martins',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rômulo Borges Monteiro',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marlos Moreno Durán',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gustavo Leonardo Cuéllar Gallego',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Réver Humberto Alves Araújo',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Diego Alves Carreira',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodinei Marcelo de Almeida',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Willian Souza Arão da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Geuvânio Santos Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lincoln Corrêa dos Santos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jonas Gomes de Sousa',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luiz Rhodolfo Dini Gaioto',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('César Bernardo Dutra',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcos Rogério Ricci Lopes',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gabriel Batista de Souza',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Thiago Rodrigues da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Hugo Gomes dos Santos Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Eduardo Costa Marinho',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Roger Rodrigues da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ángel Rodrigo Romero Villamayor',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fagner Conserva Lemos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jadson Rodrigues da Silva',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ralf de Souza Teles',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcio Passos de Albuquerque',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Danilo Gabriel de Andrade',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maycon de Andrade Barberan',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);
    INSERT INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cássio Ramos',dbo.F_RETURN_RANDOM_CPF(FLOOR(RAND() * (99999999999 - 10000000000) + 10000000000)),'S','SYSTEM',@vDataAtualizacao);

  --Insert em tb_convencao_coletiva

  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS INDUSTRIAS METALURGICAS,MECANICAS E DE MATERIAL ELETRICO DO DISTRITO FEDERAL', 'SIMEB', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DOS TRABALHADORES NAS INDÚSTRIAS DA CONSTRUÇÃO E DO MOBILIÁRIO DE BRASÍLIA', 'STICOMBE', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('FEDERAÇÃO DO COMÉRCIO DE BENS, SERVIÇOS E TURISMO DO DISTRITO FEDERAL', 'FECOMÉRCIO', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DO COMÉRCIO ATACADISTA DE ÁLCOOL E BEBIDAS EM GERAL DO DISTRITO FEDERAL', 'SCAAB', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS DE COMPRA, VENDA LOCAÇÃO E ADMINISTRAÇÃO DE IMÓVEIS RESIDENCIAIS E COMERCIAIS', 'SECOVI', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS PRESTADORAS DE SERVIÇOS E ESPECIALIZADAS EM BOMBEIRO CIVIL DO DISTRITO FEDERAL', 'SEPEBC', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DOS SALÕES DE BARBEIROS, CABELEREIROS, PROFISSIONAIS AUTÔNOMOS NA ÁREA DE BELEZA E INST. DE BELEZA PARA HOMENS E SENHORAS DO DF', 'SINCAAB', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS DE SERVIÇOS DE INFORMÁTICA DO DISTRITO FEDERAL', 'SINDESEI', '05/01/2018', @vLoginAtualizacao, @vDataAtualizacao);
  
  --Carregamento das variáveis de cargo para o contrato 1 e insert em tb_convencao_coletiva

  --Dados do contrato 1

  SELECT @vCodCargoContrato1 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('ENGENHEIRO MECÂNICO')
      AND cc.cod_contrato = @vCodContrato1;

  SELECT @vCodCargoContrato2 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('ENCARREGADO MECÂNICO')
      AND cc.cod_contrato = @vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato1, '05/08/2016',  '05/08/2016', 8237.80, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato2, '05/08/2016',  '05/08/2016', 4016.39, @vLoginAtualizacao, @vDataAtualizacao);
  
  SELECT @vCodCargoContrato1 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO')
      AND cc.cod_contrato = @vCodContrato1;

  SELECT @vCodCargoContrato2 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO')
      AND cc.cod_contrato = @vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato1, '05/08/2016',  '05/08/2016', 1676.43, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato2, '05/08/2016',  '05/08/2016', 1098.30, @vLoginAtualizacao, @vDataAtualizacao);
  
  SELECT @vCodCargoContrato1 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO')
      AND cc.cod_contrato = @vCodContrato1;

  SELECT @vCodCargoContrato2 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('AUXILIAR ADMINISTRATIVO')
      AND cc.cod_contrato = @vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato1, '05/08/2016',  '05/08/2016', 1973.40, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato2, '05/08/2016',  '05/08/2016', 1403.79, @vLoginAtualizacao, @vDataAtualizacao);
  
  --Dados do contrato 2

  SELECT @vCodCargoContrato1 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)')
      AND cc.cod_contrato = @vCodContrato2;

  SELECT @vCodCargoContrato2 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)')
      AND cc.cod_contrato = @vCodContrato2;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato1, '01/02/2017',  '01/02/2017', 5379.99, @vLoginAtualizacao, @vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato2, '01/02/2017',  '01/02/2017', 3448.69, @vLoginAtualizacao, @vDataAtualizacao);
  
  SELECT @vCodCargoContrato1 = cc.cod
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)')
      AND cc.cod_contrato = @vCodContrato2;

  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (@vCodCargoContrato1, '01/02/2017',  '01/02/2017', 3946.83, @vLoginAtualizacao, @vDataAtualizacao);
  
  --Insert em tb_terceirizado_contrato.

  OPEN cur_funcionario

  FETCH NEXT FROM cur_funcionario INTO @vCodFuncionario;

  WHILE @vCount < 52

    BEGIN

      IF (@vCount < 14)

	    BEGIN

          INSERT INTO tb_terceirizado_contrato(cod_contrato, 
                                               cod_terceirizado, 
                                               data_disponibilizacao, 
                                               login_atualizacao, 
                                               data_atualizacao) 
            VALUES (@vCodContrato1,
                    @vCodFuncionario, 
                    @vDataDisponibilizacao, 
                    @vLoginAtualizacao, 
                    @vDataAtualizacao);

		END;

      ELSE

	    IF (@vCount > 13 AND @vCount < 52)

          BEGIN

	        SET @vDataDisponibilizacao = '01/02/2017';

            INSERT INTO tb_terceirizado_contrato(cod_contrato, 
                                                 cod_terceirizado, 
                                                 data_disponibilizacao, 
                                                 login_atualizacao, 
                                                 data_atualizacao) 
              VALUES (@vCodContrato2,
                      @vCodFuncionario, 
                      @vDataDisponibilizacao, 
                      @vLoginAtualizacao, 
                      @vDataAtualizacao);

          END;            

      SET @vCount = @vCount + 1;

      FETCH NEXT FROM cur_funcionario INTO @vCodFuncionario;

  END;

  CLOSE cur_funcionario;

  DEALLOCATE cur_funcionario;

  SET @vCount = 0;

  DECLARE cur_funcionarios_c1 CURSOR FOR 
    SELECT cod AS "cod_terceirizado_contrato"
      FROM tb_terceirizado_contrato
      WHERE cod_contrato = @vCodContrato1;

  DECLARE cur_funcionarios_c2 CURSOR FOR 
    SELECT cod AS "cod_terceirizado_contrato"
      FROM tb_terceirizado_contrato
      WHERE cod_contrato = @vCodContrato2;

  --Atribuição de funções para os terceirizados do contrato 1.

  OPEN cur_funcionarios_c1

  FETCH NEXT FROM cur_funcionarios_c1 INTO @vCodTerceirizadoContrato;

  SET @vCount = 1;

  WHILE @@FETCH_STATUS = 0 

    BEGIN 
    
      IF (@vCount = 1) 

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('ENGENHEIRO MECÂNICO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);
      
        END;

      IF (@vCount = 2)

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('ENCARREGADO MECÂNICO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);

        END;

      IF (@vCount = 3)

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('AUXILIAR ADMINISTRATIVO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
             VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);
        END;

      IF (@vCount = 4 OR @vCount = 5)

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);
        END;

      IF (@vCount > 5 AND @vCount < 10)

        BEGIN 

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);
      
      END;
    
      IF (@vCount > 9 AND @vCount < 15) 

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO')
              AND cc.cod_contrato = @vCodContrato1;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                    '05/08/2016',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);
      
        END;    

      SET @vCount = @vCount + 1;

      FETCH NEXT FROM cur_funcionarios_c1 INTO @vCodTerceirizadoContrato;

    END;

  CLOSE cur_funcionarios_c1;

  DEALLOCATE cur_funcionarios_c1;

  --Atribuição de funções para os terceirizados do contrato 2.

  OPEN cur_funcionarios_c2;

  FETCH NEXT FROM cur_funcionarios_c2 INTO @vCodTerceirizadoContrato;

  SET @vCount = 1;

  WHILE @@FETCH_STATUS = 0 

    BEGIN 

      IF (@vCount < 3) 
      
        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)')
              AND cc.cod_contrato = @vCodContrato2;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                   '01/02/2017',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);

        END;

      IF (@vCount > 2 AND @vCount < 15)

        BEGIN

            SELECT @vCodCargoContrato1 = cc.cod
              FROM tb_funcao_contrato cc
                JOIN tb_funcao c ON c.cod = cc.cod_funcao
              WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)')
                AND cc.cod_contrato = @vCodContrato2;

            INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                                cod_funcao_contrato,
                                                data_inicio,
                                                login_atualizacao,
                                                data_atualizacao)
              VALUES (@vCodTerceirizadoContrato,
                      @vCodCargoContrato1,
                     '01/02/2017',
                      @vLoginAtualizacao,
                      @vDataAtualizacao);

        END;
      

      IF (@vCount > 14 AND @vCount < 39)

        BEGIN

          SELECT @vCodCargoContrato1 = cc.cod
            FROM tb_funcao_contrato cc
              JOIN tb_funcao c ON c.cod = cc.cod_funcao
            WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)')
              AND cc.cod_contrato = @vCodContrato2;

          INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                              cod_funcao_contrato,
                                              data_inicio,
                                              login_atualizacao,
                                              data_atualizacao)
            VALUES (@vCodTerceirizadoContrato,
                    @vCodCargoContrato1,
                   '01/02/2017',
                    @vLoginAtualizacao,
                    @vDataAtualizacao);

        END;
      
      SET @vCount = @vCount + 1;

      FETCH NEXT FROM cur_funcionarios_c2 INTO @vCodTerceirizadoContrato;  

    END;

  CLOSE cur_funcionarios_c2;

  DEALLOCATE cur_funcionarios_c2; 

  PRINT ''

  PRINT 'Script executado com sucesso!'
      
END;

GO
