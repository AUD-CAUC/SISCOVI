create or replace procedure "PROC_DB_LOAD"
IS

BEGIN

DECLARE

  vDataDisponibilizacao DATE := '29/01/2017';
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
  vCodAbono NUMBER;
  vCodFerias NUMBER;
  vCodFgts NUMBER;
  vCodCargoContrato1 NUMBER;
  vCodCargoContrato2 NUMBER;
  vCodCargo1 NUMBER;
  vCodCargo2 NUMBER;
  vCount NUMBER := 0;
  
  --Cusor funcionário.
  
  CURSOR cur_funcionario IS

    SELECT cod AS cod_funcionario
      FROM tb_funcionario;

BEGIN

  --Insert em tb_perfil.
  
  INSERT INTO tb_perfil (sigla, login_atualizacao, data_atualizacao) VALUES ('ADMINISTRADOR', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_perfil (sigla, login_atualizacao, data_atualizacao) VALUES ('GESTOR', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de perfil

  SELECT cod
    INTO vCodPerfilGestor
    FROM tb_perfil
    WHERE UPPER(sigla) = 'GESTOR';

  SELECT cod
    INTO vCodPerfilAdmin
    FROM tb_perfil
    WHERE UPPER(sigla) = 'ADMINISTRADOR';

  --Insert em tb_usuario

  INSERT INTO tb_usuario (cod_perfil, nome, login, login_atualizacao, data_atualizacao) VALUES (vCodPerfilGestor, 'SHAKA DE VIRGEM', 'VSHAKA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, login_atualizacao, data_atualizacao) VALUES (vCodPerfilGestor, 'LELOUCH LAMPEROUGE', 'LEROUGE', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'MATHEUS MIRANDA DE SOUSA', 'VSSOUSA', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_usuario (cod_perfil, nome, login, login_atualizacao, data_atualizacao) VALUES (vCodPerfilAdmin, 'VINICIUS DE SOUSA SANTANA', 'MMSOUSA', vLoginAtualizacao, vDataAtualizacao); 

  --Insert em tb_rubricas

  INSERT INTO tb_rubricas (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Décimo terceiro salário', '13°', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubricas (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Incidência do submódulo 4.1', 'Submódulo 4.1', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubricas (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Abono de férias', 'Abono', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubricas (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Férias', 'Férias', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_rubricas (nome, sigla, login_atualizacao, data_atualizacao) VALUES ('Multa do FGTS', 'Multa FGTS', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de gestor
  
  SELECT cod
    INTO vCodGestor1
    FROM tb_usuario
    WHERE UPPER(login) = 'VSHAKA';

  SELECT cod
    INTO vCodGestor2
    FROM tb_usuario
    WHERE UPPER(login) = 'LEROUGE';

  --Insert em tb_contrato

  INSERT INTO tb_contrato (cod_gestor, nome_empresa, cnpj, numero_contrato, data_inicio, se_ativo, login_atualizacao, data_atualizacao) VALUES (vCodGestor1, 'ESPARTA SEGURANÇA LTDA', '65616094000173', 63, '29/07/2017', 'S',vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_contrato (cod_gestor, nome_empresa, cnpj, numero_contrato, data_inicio, se_ativo, login_atualizacao, data_atualizacao) VALUES (vCodGestor2, 'BRASFORT ADMINISTRAÇÃO E SERVIÇOS LTDA', '46048365000197', 70, '22/02/2017', 'S',vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de contrato
  
  SELECT cod
    INTO vCodContrato1
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = 'ESPARTA SEGURANÇA LTDA';
    
  SELECT cod
    INTO vCodContrato2
    FROM tb_contrato
    WHERE UPPER(nome_empresa) = 'BRASFORT ADMINISTRAÇÃO E SERVIÇOS LTDA';
	
  --Insert em tb_vigencia_contrato

  INSERT INTO tb_vigencia_contrato (cod_contrato, data_inicio_vigencia, data_fim_vigencia, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, '27/01/2017', '31/08/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_vigencia_contrato (cod_contrato, data_inicio_vigencia, data_fim_vigencia, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, '22/02/2017', '16/10/2017', vLoginAtualizacao, vDataAtualizacao);

  --Carregamento das variáveis de percentual

  SELECT cod
    INTO vCod13
    FROM tb_rubricas
    WHERE UPPER(sigla) = UPPER('13°');

  SELECT cod
    INTO vCodSubMod
    FROM tb_rubricas
    WHERE UPPER(sigla) = UPPER('Submódulo 4.1');

  SELECT cod
    INTO vCodAbono
    FROM tb_rubricas
    WHERE UPPER(sigla) = UPPER('Abono');

  SELECT cod
    INTO vCodFerias
    FROM tb_rubricas
    WHERE UPPER(sigla) = UPPER('Férias');

  SELECT cod
    INTO vCodFgts
    FROM tb_rubricas
    WHERE UPPER(sigla) = UPPER('Multa FGTS');

  --Insert em tb_percentual_contrato

  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCod13, '9,09', '29/01/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodSubMod, '7,81', '29/01/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodAbono, '3,03', '29/01/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodFerias, '9,09', '29/01/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodFgts, '4,36', '29/01/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCod13, '9,09', '22/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodSubMod, '7,81', '22/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodAbono, '3,03', '22/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodFerias, '9,09', '22/02/2017', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_percentual_contrato (cod_contrato, cod_rubrica, percentual, data_inicio, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodFgts, '4,36', '22/02/2017', vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_cargo

  INSERT INTO tb_cargo (nome, login_atualizacao, data_atualizacao) VALUES ('Agente de Segurança', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_cargo (nome, login_atualizacao, data_atualizacao) VALUES ('Segurança Patrimonial', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_cargo (nome, login_atualizacao, data_atualizacao) VALUES ('Secretariado Executivo', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_cargo (nome, login_atualizacao, data_atualizacao) VALUES ('Assistente Executivo', vLoginAtualizacao, vDataAtualizacao);
      
  --Insert em tb_cargo_contrato

  SELECT cod
    INTO vCodCargo1
    FROM tb_cargo
    WHERE UPPER(nome) = UPPER('Agente de Segurança');

  SELECT cod
    INTO vCodCargo2
    FROM tb_cargo
    WHERE UPPER(nome) = UPPER('Segurança Patrimonial');

  INSERT INTO tb_cargo_contrato (cod_contrato, cod_cargo, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_cargo_contrato (cod_contrato, cod_cargo, login_atualizacao, data_atualizacao) VALUES (vCodContrato1, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  SELECT cod
    INTO vCodCargo1
    FROM tb_cargo
    WHERE UPPER(nome) = UPPER('Secretariado Executivo');

  SELECT cod
    INTO vCodCargo2
    FROM tb_cargo
    WHERE UPPER(nome) = UPPER('Assistente Executivo');

  INSERT INTO tb_cargo_contrato (cod_contrato, cod_cargo, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodCargo1, vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_cargo_contrato (cod_contrato, cod_cargo, login_atualizacao, data_atualizacao) VALUES (vCodContrato2, vCodCargo2, vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_funcionarios

  INSERT ALL

    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eliseu Padilha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Gilberto Kassab',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Moreira Franco',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Roberto Freire',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Bruno Araújo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aloysio Nunes Ferreira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marcos Pereira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Blairo Maggi',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Helder Barbalho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Romero Jucá Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Aécio Neves da Cunha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Calheiros',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Bezerra Coelho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Rocha',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao) 
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Humberto Sérgio Costa Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edison Lobão',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cássio Cunha Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jorge Viana',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lidice da Mata',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Agripino Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marta Suplicy',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ciro Nogueira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dalírio José Beber',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ivo Cassol',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lindbergh Farias',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vanessa Grazziotin',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Kátia Regina de Abreu',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fernando Afonso Collor de Mello',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Serra',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Braga',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Omar Aziz',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdir Raupp',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eunício Oliveira',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Eduardo Amorim',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Carmo Alves',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Garibaldi Alves Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ricardo Ferraço',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Anastasia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulinho da Força',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Marco Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Carlos Zarattini',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Carlos Bacelar',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Milton Monti',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Carlos Aleluia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Almeida',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Mário Negromonte Jr.',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Nelson Pellegrino',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jutahy Júnior',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Maria do Rosário',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Felipe Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Ônix Lorenzoni',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Jarbas de Andrade Vasconcelos',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Paulo da Silva',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arthur Oliveira Maia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Yeda Crusius',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paulo Henrique Lustosa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('José Reinaldo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('João Paulo Papa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vander Loubet',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rodrigo Garcia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cacá Leão',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Celso Russomano',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Dimas Fabiano Toledo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Pedro Paulo',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Lúcio Vieira Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Paes Landim',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Daniel Vilela',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Alfredo Nascimento',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca Dirceu',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Betinho Gomes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Zeca do PT',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vicente Cândido',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Júlio Lopes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Fábio Faria',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Heráclito Fortes',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Beto Mansur',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Antônio Brito',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Décio Lima',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Arlindo Chinaglia',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Renan Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Robinson Faria',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Tião Viana',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Vital do Rêgo Filho',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Rosalba Ciarlini',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Valdemar da Costa Neto',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Luís Alberto Maguito Vilela',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Edvaldo Pereira de Brito',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Oswaldo Borges da Costa',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)
    INTO tb_funcionario (nome,cpf,ativo,login_atualizacao,data_atualizacao) VALUES ('Cândido Vaccarezza',F_RETURN_RANDOM_CPF,'S',vLoginAtualizacao,vDataAtualizacao)

  SELECT * FROM DUAL;
  
  --Carregamento das variáveis de cargo para o contrato 1

  SELECT cc.cod 
    INTO vCodCargoContrato1
    FROM tb_cargo_contrato cc
      JOIN tb_cargo c ON c.cod = cc.cod_cargo
    WHERE UPPER(c.nome) = UPPER('Agente de Segurança')
      AND cc.cod_contrato = vCodContrato1;

  SELECT cc.cod 
    INTO vCodCargoContrato2
    FROM tb_cargo_contrato cc
      JOIN tb_cargo c ON c.cod = cc.cod_cargo
    WHERE UPPER(c.nome) = UPPER('Segurança Patrimonial')
      AND cc.cod_contrato = vCodContrato1;

  --Insert em tb_convencao_coletiva
  
  INSERT INTO tb_convencao_coletiva (cod_cargo_contrato, data_convencao, data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '29/01/2017', '29/01/2017', '1142,68', vLoginAtualizacao, vDataAtualizacao);
  INSERT INTO tb_convencao_coletiva (cod_cargo_contrato, data_convencao, data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '29/01/2017', '29/01/2017', '1239,80', vLoginAtualizacao, vDataAtualizacao);

  --Insert em tb_cargo_funcionario

  FOR cur IN cur_funcionario LOOP

    BEGIN

      IF (vCount < 30) THEN

        INSERT INTO tb_cargo_funcionario(cod_funcionario, 
                                         cod_cargo_contrato, 
                                         data_disponibilizacao, 
                                         login_atualizacao, 
                                         data_atualizacao) 
          VALUES (cur.cod_funcionario, 
                  vCodCargoContrato1,
                  vDataDisponibilizacao, 
                  vLoginAtualizacao, 
                  vDataAtualizacao);

      ELSE

        IF (vCount >= 30 AND vCount < 40) THEN

          INSERT INTO tb_cargo_funcionario(cod_funcionario, 
                                           cod_cargo_contrato, 
                                           data_disponibilizacao, 
                                           login_atualizacao, 
                                           data_atualizacao) 
            VALUES (cur.cod_funcionario, 
                    vCodCargoContrato2,
                    vDataDisponibilizacao, 
                    vLoginAtualizacao, 
                    vDataAtualizacao);

        ELSE

          IF (vCount >= 40 AND vCount < 75) THEN
          
            INSERT INTO tb_cargo_funcionario(cod_funcionario, 
                                      cod_cargo_contrato, 
                                      data_disponibilizacao, 
                                      login_atualizacao, 
                                      data_atualizacao) 
              VALUES (cur.cod_funcionario, 
                      vCodCargoContrato1,
                      vDataDisponibilizacao, 
                      vLoginAtualizacao, 
                      vDataAtualizacao);
            
          ELSE

            IF (vCount >= 75 AND vCount < 90) THEN

              INSERT INTO tb_cargo_funcionario(cod_funcionario, 
                                               cod_cargo_contrato, 
                                               data_disponibilizacao, 
                                               login_atualizacao, 
                                               data_atualizacao) 
                VALUES (cur.cod_funcionario, 
                        vCodCargoContrato2,
                        vDataDisponibilizacao, 
                        vLoginAtualizacao, 
                        vDataAtualizacao);
            
            END IF;   

          END IF;

        END IF;

      END IF;

      vCount := vCount + 1;

      IF (vCount = 40) THEN

      --Carregamento das variáveis dos postos de trabalho para o contrato 2  

        SELECT cc.cod 
          INTO vCodCargoContrato1
          FROM tb_cargo_contrato cc
            JOIN tb_cargo c ON c.cod = cc.cod_cargo
		  WHERE UPPER(c.nome) = UPPER('Secretariado Executivo')
		    AND cc.cod_contrato = vCodContrato2;

        SELECT cc.cod 
          INTO vCodCargoContrato2
          FROM tb_cargo_contrato cc
            JOIN tb_cargo c ON c.cod = cc.cod_cargo
          WHERE UPPER(c.nome) = UPPER('Assistente Executivo')
            AND cc.cod_contrato = vCodContrato2;
            
        vDataDisponibilizacao := '22/02/2017';

        INSERT INTO tb_convencao_coletiva (cod_cargo_contrato, data_convencao, data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato1, '22/01/2017', '22/01/2017', '1320,40', vLoginAtualizacao, vDataAtualizacao);
	    INSERT INTO tb_convencao_coletiva (cod_cargo_contrato, data_convencao, data_aditamento, remuneracao, login_atualizacao, data_atualizacao) VALUES (vCodCargoContrato2, '22/02/2017', '22/01/2017', '1518,80', vLoginAtualizacao, vDataAtualizacao);

      END IF;

    END;
  
  END LOOP;   
      
END;

END;

