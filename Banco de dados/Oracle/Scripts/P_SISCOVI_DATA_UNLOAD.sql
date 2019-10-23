create procedure "P_SISCOVI_DATA_UNLOAD"
AS

  --Deleta os dados de todas as tabelas.

BEGIN

  DROP TABLE tb_retroatividade_total_mensal;

  DROP TABLE tb_total_mensal_a_reter;

  DROP TABLE tb_restituicao_ferias;

  DROP TABLE tb_restituicao_decimo_terceiro;

  DROP TABLE tb_restituicao_rescisao;

  DROP TABLE tb_tipo_resgate;

  DROP TABLE tb_tipo_rescisao;

  DROP TABLE tb_cargo_funcionario;

  DROP TABLE tb_retroatividade_convencao;

  DROP TABLE tb_convencao_coletiva;

  DROP TABLE tb_terceirizado;

  DROP TABLE tb_cargo_contrato;

  DROP TABLE tb_retroatividade_percentual;

  DROP TABLE tb_percentual_contrato;

  DROP TABLE tb_rubrica;

  DROP TABLE tb_vigencia_contrato;
  
  DROP TABLE tb_historico_gestor_contrato;

  DROP TABLE tb_contrato;

  DROP TABLE tb_usuario;

  DROP TABLE tb_perfil;

  DROP TABLE tb_cargo;

  DBMS_OUTPUT.PUT_LINE('Script executado com sucesso');

END;
