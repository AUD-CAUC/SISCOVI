create or replace procedure "P_SISCOVI_DATA_LOAD"
IS

  vDataDisponibilizacao DATE := '05/08/2016';
  vDataAtualizacao DATE := SYSDATE;
  vLoginAtualizacao VARCHAR2(15) := 'SYSTEM';
  vCodPerfilGestor NUMBER;
  vCodPerfilAdmin NUMBER;
  vCodGestor1 NUMBER;
  vCodGestor2 NUMBER;
  vCodContrato1 NUMBER;
  vCodContrato2 NUMBER;
  vCod13 NUMBER;
  vCodSubMod NUMBER;
  vCodTercoConstitucional NUMBER;
  vCodFerias NUMBER;
  vCodFgts NUMBER;
  vCodPenalidadeFGTS NUMBER;
  vCodMultaFGTS NUMBER;
  vCodCargoContrato1 NUMBER;
  vCodCargoContrato2 NUMBER;
  vCodCargo1 NUMBER;
  vCodCargo2 NUMBER;
  vCount NUMBER := 0;
  vCodPerfilUsuario NUMBER;
  vCodPerfilGestao NUMBER;
  vCodEventoContrato NUMBER;
  
  --Cusor funcionário.
  
  CURSOR cur_funcionario IS

    SELECT cod AS cod_funcionario
      FROM tb_terceirizado;

BEGIN

  --Insert em tb_perfil_usuario.
  
  INSERT INTO tb_perfil_usuario (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Administrador do sistema', 'ADMINISTRADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_perfil_usuario (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Usuário autorizado', 'USUÁRIO', vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_perfil_gestao.
  
  INSERT INTO tb_perfil_gestao (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Gestor do contrato', 'GESTOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('1° Substituto do gestor do contrato', '1° SUBSTITUTO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_perfil_gestao (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('2° Substituto do gestor do contrato', '2° SUBSTITUTO', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de perfil

  SELECT cod
    INTO vCodPerfilUsuario
    FROM tb_perfil_usuario
    WHERE UPPER(sigla) = 'USUÁRIO';

  SELECT cod
    INTO vCodPerfilAdmin
    FROM tb_perfil_usuario
    WHERE UPPER(sigla) = 'ADMINISTRADOR';

  --Insert em tb_usuario

  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilUsuario, 'EDSON ARANTES DO NASCIMENTO', 'PELE', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilUsuario, 'CRISTIANO RONALDO', 'CRONALDO', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilUsuario, 'LIONEL MESSI', 'LMESSI', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'MATHEUS MIRANDA DE SOUSA', 'VSSOUSA', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'VINICIUS DE SOUSA SANTANA', 'MMSOUSA', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'DIOCESIO SANTANNA DA SILVA', 'DIOCESIO', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, password, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'MARIA ELIZABETH CANUTO CALAIS', 'MARIACAL', 'E10ADC3949BA59ABBE56E057F20F883E', vLoginAtualizacao, vDataAtualizacao); 

  --Insert em tb_rubricas

  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (3, 'Décimo terceiro salário', '13°', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (7, 'Incidência do submódulo 4.1', 'Submódulo 4.1', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (2, 'Terço constitucional', 'Terço constitucional', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (1, 'Férias', 'Férias', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (5, 'Multa do FGTS', 'Multa FGTS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (4, 'FGTS', 'FGTS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubrica (cod, nome, sigla, login_atualizacao, data_atualizacao) VALUES (6, 'Penalidade FGTS', 'Penalidade FGTS', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de gestor
  
  SELECT cod
    INTO vCodGestor1
    FROM tb_usuario
    WHERE UPPER(login) = 'CRONALDO';

  SELECT cod
    INTO vCodGestor2
    FROM tb_usuario
    WHERE UPPER(login) = 'LMESSI';

  --Carregamento da variável de perfil de gestão

  SELECT cod
    INTO vCodPerfilGestao
    FROM tb_perfil_gestao
    WHERE UPPER(sigla) = 'GESTOR';
 
  --Insert em tb_contrato

  INSERT INTO tb_contrato (nome_empresa, cnpj, numero_contrato, numero_processo_stj, se_ativo, login_atualizacao, data_atualizacao) VALUES ('ELETRO CONTROLE ENGENHARIA COMÉRCIO e REPRESENTAÇÃO LTDA.', '00899223000132', '034/2016', '9424/2016', 'S', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_contrato (nome_empresa, cnpj, numero_contrato, numero_processo_stj, se_ativo, login_atualizacao, data_atualizacao) VALUES ('CAPITAL SERVICE - SERVIÇOS PROFISSIONAIS LTDA ME', '08414767000179', '004/2017', '16618/2016', 'S', vLoginAtualizacao, vDataAtualizacao);
  
  --Carregamento das variáveis de contrato

  SELECT cod
    INTO vCodContrato1
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = UPPER('ELETRO CONTROLE ENGENHARIA COMÉRCIO e REPRESENTAÇÃO LTDA.');
    
  SELECT cod
    INTO vCodContrato2
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = UPPER('CAPITAL SERVICE - SERVIÇOS PROFISSIONAIS LTDA ME');

  --Insert em tb_tipo_evento_contratual

  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('APOSTILAMENTO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('CONTRATO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_tipo_evento_contratual (tipo, login_atualizacao, data_atualizacao) VALUES ('TERMO ADITIVO', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento da variável tipo evento
 
  SELECT cod
    INTO vCodEventoContrato
    FROM tb_tipo_evento_contratual
    WHERE UPPER(tipo) = 'CONTRATO';
 
  --Insert em tb_historico_gestao_contrato

  INSERT INTO tb_historico_gestao_contrato (cod_contrato, cod_usuario, cod_perfil_gestao, data_inicio, login_atualizacao, datA_atualizacao) VALUES (vCodContrato1, vCodGestor1, vCodPerfilGestao, '05/08/2016', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_historico_gestao_contrato (cod_contrato, cod_usuario, cod_perfil_gestao, data_inicio, login_atualizacao, datA_atualizacao) VALUES (vCodContrato2, vCodGestor2, vCodPerfilGestao, '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
	
  --Insert em tb_evento_contratual

  INSERT INTO tb_evento_contratual (cod_contrato, cod_tipo_evento, prorrogacao, data_inicio_vigencia, data_fim_vigencia, data_assinatura, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodEventoContrato, 'N', '05/08/2016', '04/08/2017', '05/08/2016', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_evento_contratual (cod_contrato, cod_tipo_evento, prorrogacao, data_inicio_vigencia, data_fim_vigencia, data_assinatura, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodEventoContrato, 'N', '01/02/2017', '31/01/2018', '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
 
  --Carregamento das variáveis de percentual

  SELECT cod
    INTO vCod13
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('13°');

  SELECT cod
    INTO vCodSubMod
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Submódulo 4.1');

  SELECT cod
    INTO vCodTercoConstitucional
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Terço constitucional');

  SELECT cod
    INTO vCodFerias
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Férias');

  SELECT cod
    INTO vCodFgts
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('FGTS');

  SELECT cod
    INTO vCodMultaFgts
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Multa FGTS');

  SELECT cod
    INTO vCodPenalidadeFgts
    FROM tb_rubrica
    WHERE UPPER(sigla) = UPPER('Penalidade FGTS');  

  --Insert em tb_percentual_contrato

  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCod13, 9.09, '05/08/2016', '05/08/2016',vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodSubMod, 16.8, '05/08/2016', '05/08/2016',vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodTercoConstitucional, 3.03, '05/08/2016', '05/08/2016',vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodFerias, 9.09, '05/08/2016', '05/08/2016',vLoginAtualizacao, vDataAtualizacao);
    
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCod13, 9.09, '01/02/2017', '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodSubMod, 35.30, '01/02/2017', '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodTercoConstitucional, 3.03, '01/02/2017', '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodFerias, 9.09, '01/02/2017', '01/02/2017', vLoginAtualizacao, vDataAtualizacao);
  
  --Insert em tb_percentual_estático

  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodFGTS, 8.0, '01/01/2000', '01/01/2000', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodMultaFGTS, 50.0, '01/01/2000', '01/01/2000', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_estatico (cod_rubrica, percentual, data_inicio, data_aditamento, login_atualizacao, data_atualizacao) VALUES (vCodPenalidadeFGTS, 90.0, '01/01/2000', '01/01/2000', vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_tipo_rescisao

  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('SEM JUSTA CAUSA', 'SYSTEM', vDataAtualizacao);
  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('A PEDIDO', 'SYSTEM', vDataAtualizacao);
  INSERT INTO tb_tipo_rescisao (tipo_rescisao, login_atualizacao, data_atualizacao) VALUES ('COM JUSTA CAUSA', 'SYSTEM', vDataAtualizacao);

  --Insert em tb_tipo_restituicao

  INSERT INTO tb_tipo_restituicao (nome, login_atualizacao, data_atualizacao) VALUES ('MOVIMENTAÇÃO', 'SYSTEM', vDataAtualizacao);
  INSERT INTO tb_tipo_restituicao (nome, login_atualizacao, data_atualizacao) VALUES ('RESGATE', 'SYSTEM', vDataAtualizacao);

  --Insert em tb_funcao

  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AGENTE DE SEGURANÇA PESSOAL PRIVADA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AGENTE DE SEGURANÇA PESSOAL PRIVADA (SUPERVISOR DE SERVIÇOS)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AJUDANTE DE BOMBEIRO HIDRÁULICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AJUDANTE DE ELETRICISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ALMOXARIFE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ALMOXARIFE (TÉCNICO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE REDE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE AOS MAGISTRADOS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE JR. DE BACKUP', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE JR. DE REDE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA JR.', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ANALISTA DE SUPORTE TÉCNICO (INFORMÁTICA)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ASCENSORISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ASSISTENTE DE FOTOGRAFIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR ADMINISTRATIVO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE ALMOXARIFADO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE BIBLIOTECA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE EDUCAÇÃO INFANTIL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE HIGIENIZAÇÃO E ACONDICIONAMENTO DE ACERVOS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MANUTENÇÃO PREDIAL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MARCENARIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MECÂNICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE MECÂNICO DE REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE OPERADOR DE CÂMERA UPE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE RECEPÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SERVIÇOS GERAIS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SUPERVISÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE SUPERVISÃO DE MARCENARIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR DE TELEFONIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('AUXILIAR TÉCNICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO HIDRÁULICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('BORRACHEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('CHEFE DE REPORTAGEM DE RÁDIO E TV', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COPEIRA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COPEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('COZINHEIRA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESENHISTA / VIDEOGRAFISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESENHISTA EM CAD', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO JÚNIOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO PLENO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DESIGNER GRÁFICO SÊNIOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DIGITALIZADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DIRETOR DE IMAGEM DE TV - DTV', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('DOCUMENTADORA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE IMAGEM, PÓS PRODUÇÃO E FINALIZAÇÃO DE VÍDEO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE TEXTO DE RÁDIO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('EDITOR DE TEXTO (TV)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA DE AUTOS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA PLANTONISTA (DIURNO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ELETRICISTA PLANTONISTA (NOTURNO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE ELÉTRICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE MECÂNICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE HIDRÁULICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO DE MECÂNICA E REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO GERAL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENCARREGADO MECÂNICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENFERMEIRA AUDITORA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENFERMEIRA / SUPERVISORA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO COORDENADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO DE SISTEMAS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO ELETRICISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO MECÂNICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO RESIDENTE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ENGENHEIRO SÊNIOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('FATURISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GARÇOM', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GARÇONETE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GERENTE DE PROJETO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GERENTE TÉCNICO / PREPOSTO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('GESSEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('ILUMINADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('INSTRUMENTADOR TÉCNICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('JARDINEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('JAUZEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('LAVADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('LUSTRADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MAQUIADOR / CABELEREIRO / FIGURINISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MARCENEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO DE REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO LEVE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MECÂNICO PESADO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MÉDICO AUDITOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MEIO OFICIAL ELETRICISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MENSAGEIRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MENSAGEIRO DE GABINETE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE AUTOMAÇÃO DA CENTRAL (DIURNO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE AUTOMAÇÃO DA CENTRAL (NOTURNO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MONITOR DE OPERAÇÃO CAG (DIURNO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('MOTORISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OFICIAL ELETRICISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE CÂMERA UPE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE ÁGUA GELADA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE DETECÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DA CENTRAL DE SUPERVISÃO PREDIAL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE ÁUDIO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE ÁUDIO E VÍDEO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE IMPRESSÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE IMPRESSÃO DIGITAL E ACABAMENTOS AFINS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MÁQUINA DE CARACTERES', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MICRO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE VÍDEO ', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE VT', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('OPERADOR DE MESA TELEFÔNICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PEDAGOGA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PEDAGOGO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PINTOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PREPARADOR DE DOCUMENTOS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PREPOSTO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('PRODUTOR DE JORNALISMO, RÁDIO E TV', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('RECEPCIONISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPÓRTER DE RÁDIO E TV', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPÓRTER DE RÁDIO E TV / APRESENTADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REPORTER FOTOGRÁFICO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('REVISOR DE TEXTO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SEGURANÇA PESSOAL PRIVADA RESIDENCIAL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SERVENTE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SONOPLASTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE 1° NÍVEL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE 2° NÍVEL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE DOCUMENTAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE JARDINAGEM', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE MARCENARIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE MENSAGEIRIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR DE VIGILÂNCIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR GERAL DE SERVENTES', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO CIVIL)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO ELETRICISTA)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (ENGENHEIRO MECÂNICO)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR TÉCNICO (OPERACIONAL DE RÁDIO E TV)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISOR / LÍDER DE EQUIPE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPERVISORA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('SUPORTE TÉCNICO INTERNO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO 1° NÍVEL', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO 1° NÍVEL (SAA)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (ASP)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (JUDICIÁRIA)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ATENDIMENTO SUPORTE PRESENCIAL (APP PREFERÊNCIAL)', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE ÁUDIO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE LEVADOR RESIDENTE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO DE VÍDEO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM CABEAMENTO E REDE ESTRUTURADA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM DESIGN', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM DETECÇÃO E ALARME DE INCÊNDIO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETRÔNICA I', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETRÔNICA II', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM ELETROTÉCNICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO DE EDIFICAÇÕES', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MÁQUINAS', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO I', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SECRETARIADO II', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM SISTEMA DE COMUNICAÇÃO OPEN SCAPE VOIP', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM TELEFONIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM INFORMÁTICA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO EM MANUTENÇÃO DE HARDWARE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TÉCNICO MECÂNICO EM REFRIGERAÇÃO', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('TELEFONISTA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('VIGILANTE / VIGILÂNCIA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao (nome, login_atualizacao, data_atualizacao) VALUES ('WEB DESIGNER', vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_funcao_contrato

  SELECT cod
    INTO vCodCargo1
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('ENGENHEIRO MECÂNICO');

  SELECT cod
    INTO vCodCargo2
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('ENCARREGADO MECÂNICO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  SELECT cod
    INTO vCodCargo1
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO');

  SELECT cod
    INTO vCodCargo2
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  SELECT cod
    INTO vCodCargo1
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO');

  SELECT cod
    INTO vCodCargo2
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('AUXILIAR ADMINISTRATIVO');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  SELECT cod
    INTO vCodCargo1
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)');

  SELECT cod
    INTO vCodCargo2
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  SELECT cod
    INTO vCodCargo2
    FROM tb_funcao
    WHERE UPPER(nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)');

  INSERT INTO tb_funcao_contrato (cod_contrato, cod_funcao, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_funcionarios

  INSERT ALL

    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eliseu Padilha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gilberto Kassab',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Moreira Franco',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Roberto Freire',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Araújo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aloysio Nunes Ferreira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcos Pereira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Blairo Maggi',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Helder Barbalho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Romero Jucá Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aécio Neves da Cunha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Calheiros',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Bezerra Coelho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Rocha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao) 
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Humberto Sérgio Costa Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edison Lobão',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cássio Cunha Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jorge Viana',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lidice da Mata',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Agripino Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marta Suplicy',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ciro Nogueira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dalírio José Beber',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ivo Cassol',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lindbergh Farias',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vanessa Grazziotin',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Kátia Regina de Abreu',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Afonso Collor de Mello',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Serra',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Braga',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Omar Aziz',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdir Raupp',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eunício Oliveira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Amorim',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Carmo Alves',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Garibaldi Alves Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ricardo Ferraço',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Anastasia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulinho da Força',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marco Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Carlos Zarattini',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Carlos Bacelar',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Milton Monti',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Carlos Aleluia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Almeida',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Mário Negromonte Jr.',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Nelson Pellegrino',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jutahy Júnior',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Rosário',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Felipe Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ônix Lorenzoni',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jarbas de Andrade Vasconcelos',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Paulo da Silva',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arthur Oliveira Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Yeda Crusius',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Henrique Lustosa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Reinaldo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Paulo Papa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vander Loubet',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Garcia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cacá Leão',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Celso Russomano',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dimas Fabiano Toledo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Pedro Paulo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lúcio Vieira Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paes Landim',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Vilela',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Alfredo Nascimento',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca Dirceu',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Betinho Gomes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca do PT',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Cândido',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Júlio Lopes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fábio Faria',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Heráclito Fortes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Beto Mansur',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Brito',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Décio Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arlindo Chinaglia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Robinson Faria',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Tião Viana',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vital do Rêgo Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rosalba Ciarlini',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdemar da Costa Neto',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luís Alberto Maguito Vilela',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edvaldo Pereira de Brito',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Oswaldo Borges da Costa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cândido Vaccarezza',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Martín Andrés Silva Leites',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Kelvin Mateus de Oliveira',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Evander da Silva Ferreira',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Duvier Orlando Riascos Barahona',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rildo de Andrade Felicíssimo',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Wagner Ferreira dos Santos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Glaybson Yago Souza Lisboa',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Leandro Luis Desábato',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Marcos de Jesus Ribeiro',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Giovanni Augusto Oliveira Cardoso',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Breno Vinícius Rodrigues Borges',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Andrés Lorenzo Ríos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Thiago Galhardo do Nascimento Rocha',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ramon de Morais Motta',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Wellington Aparecido Martins',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Frickson Rafael Erazo Vivero',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Henrique Silva Milagres',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcelo de Mattos Terra',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rafael Galhardo de Souza',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Werley Ananias da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Vitor Fernandes Pereira',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jomar Herculano Lourenço',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Jacinto da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Caio Monteiro Costa',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jordi Martins Almeida',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno da Silva Barbosa',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gabriel Félix Dos Santos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luiz Gustavo Tavares Conde',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fabrício dos Santos Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Andrey Ramos do Nascimento',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Cosendey Lobo Pinto',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Miguel Kaufmann',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lucas Perdomo Duarte Santos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Pedro Soares Borges',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ricardo Queiroz de Alencastro Graça',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Alan Cardoso de Andrade',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Paolo Guerrero Gonzales',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lucas Tolentino Coelho de Lima',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vinícius José Paixão de Oliveira Júnior',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Diego Ribas da Cunha',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Henrique da Silva Dourado',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Éverton Augusto de Barros Ribeiro',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Orlando Enrique Berrío Meléndez',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Miguel Ángel Trauco Saavedra',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Juan Silveira dos Santos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ederson Honorato Campos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Felipe dos Reis Pereira Vizeu do Carmo',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renê Rodrigues Martins',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rômulo Borges Monteiro',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marlos Moreno Durán',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gustavo Leonardo Cuéllar Gallego',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Réver Humberto Alves Araújo',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Diego Alves Carreira',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodinei Marcelo de Almeida',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Willian Souza Arão da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Geuvânio Santos Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lincoln Corrêa dos Santos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jonas Gomes de Sousa',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luiz Rhodolfo Dini Gaioto',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('César Bernardo Dutra',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcos Rogério Ricci Lopes',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gabriel Batista de Souza',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Thiago Rodrigues da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Hugo Gomes dos Santos Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Eduardo Costa Marinho',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Roger Rodrigues da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ángel Rodrigo Romero Villamayor',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fagner Conserva Lemos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jadson Rodrigues da Silva',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ralf de Souza Teles',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcio Passos de Albuquerque',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Danilo Gabriel de Andrade',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maycon de Andrade Barberan',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)
    INTO tb_terceirizado (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cássio Ramos',F_RETURN_RANDOM_CPF,'S','SYSTEM',SYSDATE)

  SELECT * FROM DUAL;

  --Insert em tb_convencao_coletiva

  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS INDUSTRIAS METALURGICAS,MECANICAS E DE MATERIAL ELETRICO DO DISTRITO FEDERAL', 'SIMEB', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DOS TRABALHADORES NAS INDÚSTRIAS DA CONSTRUÇÃO E DO MOBILIÁRIO DE BRASÍLIA', 'STICOMBE', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('FEDERAÇÃO DO COMÉRCIO DE BENS, SERVIÇOS E TURISMO DO DISTRITO FEDERAL', 'FECOMÉRCIO', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DO COMÉRCIO ATACADISTA DE ÁLCOOL E BEBIDAS EM GERAL DO DISTRITO FEDERAL', 'SCAAB', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS DE COMPRA, VENDA LOCAÇÃO E ADMINISTRAÇÃO DE IMÓVEIS RESIDENCIAIS E COMERCIAIS', 'SECOVI', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS PRESTADORAS DE SERVIÇOS E ESPECIALIZADAS EM BOMBEIRO CIVIL DO DISTRITO FEDERAL', 'SEPEBC', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DOS SALÕES DE BARBEIROS, CABELEREIROS, PROFISSIONAIS AUTÔNOMOS NA ÁREA DE BELEZA E INST. DE BELEZA PARA HOMENS E SENHORAS DO DF', 'SINCAAB', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (nome, sigla, data_base, login_atualizacao, data_atualizacao) VALUES ('SINDICATO DAS EMPRESAS DE SERVIÇOS DE INFORMÁTICA DO DISTRITO FEDERAL', 'SINDESEI', '05/01/2018', vLoginAtualizacao, vDataAtualizacao);
  
  --Carregamento das variáveis de cargo para o contrato 1 e insert em tb_convencao_coletiva

  --Dados do contrato 1

  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('ENGENHEIRO MECÂNICO')
      AND cc.cod_contrato = vCodContrato1;

  SELECT cc.cod 
    INTO vCodCargoContrato2
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('ENCARREGADO MECÂNICO')
      AND cc.cod_contrato = vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '05/08/2016',  '05/08/2016', 8237.80, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '05/08/2016',  '05/08/2016', 4016.39, vLoginAtualizacao, vDataAtualizacao);
  
  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO')
      AND cc.cod_contrato = vCodContrato1;

  SELECT cc.cod 
    INTO vCodCargoContrato2
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO')
      AND cc.cod_contrato = vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio, data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '05/08/2016',  '05/08/2016', 1676.43, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '05/08/2016',  '05/08/2016', 1098.30, vLoginAtualizacao, vDataAtualizacao);
  
  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO')
      AND cc.cod_contrato = vCodContrato1;

  SELECT cc.cod 
    INTO vCodCargoContrato2
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('AUXILIAR ADMINISTRATIVO')
      AND cc.cod_contrato = vCodContrato1;
  
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '05/08/2016',  '05/08/2016', 1973.40, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '05/08/2016',  '05/08/2016', 1403.79, vLoginAtualizacao, vDataAtualizacao);
  
  --Dados do contrato 2

  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)')
      AND cc.cod_contrato = vCodContrato2;

  SELECT cc.cod 
    INTO vCodCargoContrato2
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)')
      AND cc.cod_contrato = vCodContrato2;

  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '01/02/2017',  '01/02/2017', 5379.99, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '01/02/2017',  '01/02/2017', 3448.69, vLoginAtualizacao, vDataAtualizacao);
  
  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_funcao_contrato cc
      JOIN tb_funcao c ON c.cod = cc.cod_funcao
    WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)')
      AND cc.cod_contrato = vCodContrato2;

  INSERT INTO tb_remuneracao_fun_con (cod_funcao_contrato, data_inicio,  data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '01/02/2017',  '01/02/2017', 3946.83, vLoginAtualizacao, vDataAtualizacao);
  

  --Insert em tb_funcao_funcionario

  FOR cur IN cur_funcionario LOOP

    BEGIN

      IF (vCount < 14) THEN

        INSERT INTO tb_terceirizado_contrato(cod_contrato, 
                                             cod_terceirizado, 
                                             data_disponibilizacao, 
                                             login_atualizacao, 
                                             data_atualizacao) 
          VALUES (vCodContrato1,
                  cur.cod_funcionario, 
                  vDataDisponibilizacao, 
                  vLoginAtualizacao, 
                  vDataAtualizacao);

      ELSE

        IF (vCount > 13 AND vCount < 52) THEN

          vDataDisponibilizacao := '01/02/2017';

          INSERT INTO tb_terceirizado_contrato(cod_contrato, 
                                               cod_terceirizado, 
                                               data_disponibilizacao, 
                                               login_atualizacao, 
                                               data_atualizacao) 
            VALUES (vCodContrato2,
                    cur.cod_funcionario, 
                    vDataDisponibilizacao, 
                    vLoginAtualizacao, 
                    vDataAtualizacao);

        END IF;

      END IF;            

      vCount := vCount + 1;
      
    END;
  
  END LOOP;

  vCount := 1;

  --Atribuição de funções para os terceirizados do contrato 1

  FOR c4 IN (SELECT cod
               FROM tb_terceirizado_contrato
               WHERE cod_contrato = vCodContrato1) LOOP
    
    IF (vCount = 1) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('ENGENHEIRO MECÂNICO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount = 2) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('ENCARREGADO MECÂNICO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount = 3) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('AUXILIAR ADMINISTRATIVO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount = 4 OR vCount = 5) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('TÉCNICO ELETRICISTA EM REFRIGERAÇÃO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount > 5 AND vCount < 10) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('AUXILIAR DE REFRIGERAÇÃO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount > 9 AND vCount < 15) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('TÉCNICO MECÂNICO EM REFRIGERAÇÃO')
          AND cc.cod_contrato = vCodContrato1;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c4.cod,
                vCodCargoContrato1,
                '05/08/2016',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;    

    vCount := vCount + 1;

  END LOOP;

  vCount := 1;

  --Atribuição de funções para os terceirizados do contrato 2

  FOR c5 IN (SELECT cod
               FROM tb_terceirizado_contrato
               WHERE cod_contrato = vCodContrato2) LOOP
    
    IF (vCount < 3) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL LÍDER (DIURNO 12x36)')
          AND cc.cod_contrato = vCodContrato2;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c5.cod,
                vCodCargoContrato1,
                '01/02/2017',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount > 2 AND vCount < 15) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (NOTURNO 12X36)')
          AND cc.cod_contrato = vCodContrato2;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c5.cod,
                vCodCargoContrato1,
                '01/02/2017',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    IF (vCount > 14 AND vCount < 39) THEN

      SELECT cc.cod 
        INTO vCodCargoContrato1
        FROM tb_funcao_contrato cc
          JOIN tb_funcao c ON c.cod = cc.cod_funcao
        WHERE UPPER(c.nome) = UPPER('BOMBEIRO CIVIL BÁSICO (DIURNO 12X36)')
          AND cc.cod_contrato = vCodContrato2;

      INSERT INTO tb_funcao_terceirizado (cod_terceirizado_contrato,
                                          cod_funcao_contrato,
                                          data_inicio,
                                          login_atualizacao,
                                          data_atualizacao)
        VALUES (c5.cod,
                vCodCargoContrato1,
                '01/02/2017',
                vLoginAtualizacao,
                vDataAtualizacao);
      
    END IF;

    vCount := vCount + 1;

  END LOOP;

END;
