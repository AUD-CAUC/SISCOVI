create or replace procedure "P_SISCOVI_DATABASE_CREATION"
AS

-- Gerado por Oracle SQL Developer Data Modeler 17.4.0.355.2121
--   em:        2018-05-29 14:42:23 BRT
--   site:      Oracle Database 12c
--   tipo:      Oracle Database 12c

CREATE TABLE tb_contrato (
    cod                   INTEGER NOT NULL,
    nome_empresa          VARCHAR2(200) NOT NULL,
    cnpj                  VARCHAR2(14 CHAR) NOT NULL,
    numero_contrato       VARCHAR2(50 CHAR) NOT NULL,
    numero_processo_stj   VARCHAR2(50 CHAR) NOT NULL,
    se_ativo              CHAR(1 CHAR) NOT NULL,
    objeto                VARCHAR2(500 CHAR),
    login_atualizacao     VARCHAR2(100) NOT NULL,
    data_atualizacao      TIMESTAMP NOT NULL
);

ALTER TABLE tb_contrato ADD CONSTRAINT tb_contrato_pk PRIMARY KEY ( cod );

CREATE TABLE tb_convencao_coletiva (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    sigla               VARCHAR2(200 CHAR) NOT NULL,
    data_base           DATE NOT NULL,
    descricao           VARCHAR2(400 CHAR),
    login_atualizacao   VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_convencao_coletiva ADD CONSTRAINT tb_convencao_coletiva_pk PRIMARY KEY ( cod );

CREATE TABLE tb_evento_contratual (
    cod                    INTEGER NOT NULL,
    cod_contrato           INTEGER NOT NULL,
    cod_tipo_evento        INTEGER NOT NULL,
    prorrogacao            CHAR(1) NOT NULL,
    assunto                VARCHAR2(400 CHAR),
    data_inicio_vigencia   DATE NOT NULL,
    data_fim_vigencia      DATE NOT NULL,
    data_assinatura        DATE NOT NULL,
    login_atualizacao      VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao       TIMESTAMP NOT NULL
);

ALTER TABLE tb_evento_contratual ADD CONSTRAINT tb_evento_contratual_pk PRIMARY KEY ( cod );

CREATE TABLE tb_funcao (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    descricao           VARCHAR2(400 CHAR),
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_funcao ADD CONSTRAINT tb_funcao_pk PRIMARY KEY ( cod );

ALTER TABLE tb_funcao ADD CONSTRAINT tb_cargo_un UNIQUE ( nome );

CREATE TABLE tb_funcao_contrato (
    cod                 INTEGER NOT NULL,
    cod_contrato        INTEGER NOT NULL,
    cod_funcao          INTEGER NOT NULL,
    descricao           VARCHAR2(400 CHAR),
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_funcao_contrato ADD CONSTRAINT tb_funcao_contrato_pk PRIMARY KEY ( cod );

ALTER TABLE tb_funcao_contrato ADD CONSTRAINT tb_funcao_contrato_un UNIQUE ( cod_contrato,
cod_funcao );

CREATE TABLE tb_funcao_terceirizado (
    cod                         INTEGER NOT NULL,
    cod_terceirizado_contrato   INTEGER NOT NULL,
    cod_funcao_contrato         INTEGER NOT NULL,
    data_inicio                 DATE NOT NULL,
    data_fim                    DATE,
    login_atualizacao           VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao            TIMESTAMP NOT NULL
);

ALTER TABLE tb_funcao_terceirizado ADD CONSTRAINT tb_funcao_terceirizado_pk PRIMARY KEY ( cod );

CREATE TABLE tb_historico_gestao_contrato (
    cod                 INTEGER NOT NULL,
    cod_contrato        INTEGER NOT NULL,
    cod_usuario         INTEGER NOT NULL,
    cod_perfil_gestao   INTEGER NOT NULL,
    data_inicio         DATE NOT NULL,
    data_fim            DATE,
    login_atualizacao   VARCHAR2(150) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_historico_gestao_contrato ADD CONSTRAINT tb_hist_gestao_contrato_pk PRIMARY KEY ( cod );

CREATE TABLE tb_percentual_contrato (
    cod                 INTEGER NOT NULL,
    cod_contrato        INTEGER NOT NULL,
    cod_rubrica         INTEGER NOT NULL,
    percentual          FLOAT(20) NOT NULL,
    data_inicio         DATE NOT NULL,
    data_fim            DATE,
    data_aditamento     DATE NOT NULL,
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_percentual_contrato ADD CONSTRAINT tb_percentual_contrato_pk PRIMARY KEY ( cod );

CREATE TABLE tb_percentual_estatico (
    cod                 INTEGER NOT NULL,
    cod_rubrica         INTEGER NOT NULL,
    percentual          FLOAT(20) NOT NULL,
    data_inicio         DATE NOT NULL,
    data_fim            DATE,
    data_aditamento     DATE NOT NULL,
    login_atualizacao   VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_percentual_estatico ADD CONSTRAINT tb_percentual_estatico_pk PRIMARY KEY ( cod );

CREATE TABLE tb_perfil_gestao (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    sigla               VARCHAR2(50) NOT NULL,
    descricao           VARCHAR2(400 CHAR),
    login_atualizacao   VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_perfil_gestao ADD CONSTRAINT tb_perfil_gestao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_perfil_usuario (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    sigla               VARCHAR2(50) NOT NULL,
    descricao           VARCHAR2(400),
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_perfil_usuario ADD CONSTRAINT tb_perfil_usuario_pk PRIMARY KEY ( cod );

ALTER TABLE tb_perfil_usuario ADD CONSTRAINT tb_perfil_sigla_un UNIQUE ( sigla );

CREATE TABLE tb_remuneracao_fun_con (
    cod                      INTEGER NOT NULL,
    cod_funcao_contrato      INTEGER NOT NULL,
    cod_convencao_coletiva   INTEGER,
    data_inicio              DATE NOT NULL,
    data_fim                 DATE,
    data_aditamento          DATE NOT NULL,
    remuneracao              FLOAT(20) NOT NULL,
    trienios                 FLOAT(20),
    adicionais               FLOAT(20),
    login_atualizacao        VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao         TIMESTAMP NOT NULL
);

ALTER TABLE tb_remuneracao_fun_con ADD CONSTRAINT tb_remuneracao_fun_con_pk PRIMARY KEY ( cod );

CREATE TABLE tb_restituicao_decimo_terceiro (
    cod                         INTEGER NOT NULL,
    cod_terceirizado_contrato   INTEGER NOT NULL,
    cod_tipo_restituicao        INTEGER NOT NULL,
    parcela                     INTEGER NOT NULL,
    data_inicio_contagem        DATE NOT NULL,
    valor                       FLOAT(20) NOT NULL,
    incidencia_submodulo_4_1    FLOAT(20) NOT NULL,
    data_referencia             DATE NOT NULL,
    login_atualizacao           VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao            TIMESTAMP NOT NULL
);

ALTER TABLE tb_restituicao_decimo_terceiro ADD CONSTRAINT tb_restituicao_dec_ter_pk PRIMARY KEY ( cod );

CREATE TABLE tb_restituicao_ferias (
    cod                              INTEGER NOT NULL,
    cod_terceirizado_contrato        INTEGER NOT NULL,
    cod_tipo_restituicao             INTEGER NOT NULL,
    data_inicio_periodo_aquisitivo   DATE NOT NULL,
    data_fim_periodo_aquisitivo      DATE NOT NULL,
    data_inicio_usufruto             DATE NOT NULL,
    data_fim_usufruto                DATE NOT NULL,
    dias_vendidos                    INTEGER NOT NULL,
    valor_ferias                     FLOAT(20) NOT NULL,
    valor_terco_constitucional       FLOAT(20) NOT NULL,
    incid_submod_4_1_ferias          FLOAT(20),
    incid_submod_4_1_terco           FLOAT(20) NOT NULL,
    se_proporcional                  CHAR(1 CHAR) NOT NULL,
    data_referencia                  DATE NOT NULL,
    login_atualizacao                VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao                 TIMESTAMP NOT NULL
);

ALTER TABLE tb_restituicao_ferias ADD CONSTRAINT tb_restituicao_ferias_pk PRIMARY KEY ( cod );

CREATE TABLE tb_restituicao_rescisao (
    cod                             INTEGER NOT NULL,
    cod_terceirizado_contrato       INTEGER NOT NULL,
    cod_tipo_restituicao            INTEGER NOT NULL,
    cod_tipo_rescisao               INTEGER NOT NULL,
    data_desligamento               DATE NOT NULL,
    valor_decimo_terceiro           FLOAT(20) NOT NULL,
    incid_submod_4_1_dec_terceiro   FLOAT(20) NOT NULL,
    incid_multa_fgts_dec_terceiro   FLOAT(20) NOT NULL,
    valor_ferias                    FLOAT(20) NOT NULL,
    valor_terco                     FLOAT(20) NOT NULL,
    incid_submod_4_1_ferias         FLOAT(20) NOT NULL,
    incid_submod_4_1_terco          FLOAT(20) NOT NULL,
    incid_multa_fgts_ferias         FLOAT(20) NOT NULL,
    incid_multa_fgts_terco          FLOAT(20) NOT NULL,
    multa_fgts_salario              FLOAT(20) NOT NULL,
    data_referencia                 DATE NOT NULL,
    login_atualizacao               VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao                TIMESTAMP NOT NULL
);

ALTER TABLE tb_restituicao_rescisao ADD CONSTRAINT tb_restituicao_rescisao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_retro_percentual_estatico (
    cod                       INTEGER NOT NULL,
    cod_contrato              INTEGER NOT NULL,
    cod_percentual_estatico   INTEGER NOT NULL,
    inicio                    DATE NOT NULL,
    fim                       DATE NOT NULL,
    data_cobranca             DATE NOT NULL,
    login_atualizacao         VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao          TIMESTAMP NOT NULL
);

ALTER TABLE tb_retro_percentual_estatico ADD CONSTRAINT tb_retro_percent_estatico_pk PRIMARY KEY ( cod );

CREATE TABLE tb_retroatividade_percentual (
    cod                       INTEGER NOT NULL,
    cod_percentual_contrato   INTEGER NOT NULL,
    inicio                    DATE NOT NULL,
    fim                       DATE NOT NULL,
    data_cobranca             DATE NOT NULL,
    login_atualizacao         VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao          DATE NOT NULL
);

ALTER TABLE tb_retroatividade_percentual ADD CONSTRAINT tb_retro_percentual_pk PRIMARY KEY ( cod );

CREATE TABLE tb_retroatividade_remuneracao (
    cod                       INTEGER NOT NULL,
    cod_rem_funcao_contrato   INTEGER NOT NULL,
    inicio                    DATE NOT NULL,
    fim                       DATE NOT NULL,
    data_cobranca             DATE NOT NULL,
    login_atualizacao         VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao          DATE NOT NULL
);

ALTER TABLE tb_retroatividade_remuneracao ADD CONSTRAINT tb_retro_remuneracao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_retroatividade_total_mensal (
    cod                        INTEGER NOT NULL,
    cod_total_mensal_a_reter   INTEGER NOT NULL,
    ferias                     FLOAT(20) NOT NULL,
    terco_constitucional       FLOAT(20) NOT NULL,
    decimo_terceiro            FLOAT(20) NOT NULL,
    incidencia_submodulo_4_1   FLOAT(20) NOT NULL,
    multa_fgts                 FLOAT(20) NOT NULL,
    total                      FLOAT(20) NOT NULL,
    login_atualizacao          VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao           TIMESTAMP NOT NULL
);

ALTER TABLE tb_retroatividade_total_mensal ADD CONSTRAINT tb_retroativ_total_mensal_pk PRIMARY KEY ( cod );

CREATE TABLE tb_rubrica (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    sigla               VARCHAR2(50 CHAR) NOT NULL,
    descricao           VARCHAR2(400 CHAR),
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_rubrica ADD CONSTRAINT tb_rubrica_pk PRIMARY KEY ( cod );

ALTER TABLE tb_rubrica ADD CONSTRAINT tb_rubricas_sigla_un UNIQUE ( sigla );

CREATE TABLE tb_saldo_residual_dec_ter (
    cod                            INTEGER NOT NULL,
    cod_restituicao_dec_terceiro   INTEGER NOT NULL,
    valor                          FLOAT(20) NOT NULL,
    incidencia_submodulo_4_1       FLOAT(20) NOT NULL,
    restituido                     CHAR(1 CHAR) NOT NULL,
    login_atualizacao              VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao               TIMESTAMP NOT NULL
);

ALTER TABLE tb_saldo_residual_dec_ter ADD CONSTRAINT tb_saldo_residual_dec_ter_pk PRIMARY KEY ( cod );

CREATE TABLE tb_saldo_residual_ferias (
    cod                       INTEGER NOT NULL,
    cod_restituicao_ferias    INTEGER NOT NULL,
    valor_ferias              FLOAT(20) NOT NULL,
    valor_terco               FLOAT(20) NOT NULL,
    incid_submod_4_1_ferias   FLOAT(20) NOT NULL,
    incid_submod_4_1_terco    FLOAT(20) NOT NULL,
    restituido                CHAR(1 CHAR) NOT NULL,
    login_atualizacao         VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao          TIMESTAMP NOT NULL
);

ALTER TABLE tb_saldo_residual_ferias ADD CONSTRAINT tb_saldo_residual_ferias_pk PRIMARY KEY ( cod );

CREATE TABLE tb_saldo_residual_rescisao (
    cod                             INTEGER NOT NULL,
    cod_restituicao_rescisao        INTEGER NOT NULL,
    valor_decimo_terceiro           FLOAT(20),
    incid_submod_4_1_dec_terceiro   FLOAT(20) NOT NULL,
    incid_multa_fgts_dec_terceiro   FLOAT(20) NOT NULL,
    valor_ferias                    FLOAT(20),
    valor_terco                     FLOAT(20),
    incid_submod_4_1_ferias         FLOAT(20) NOT NULL,
    incid_submod_4_1_terco          FLOAT(20) NOT NULL,
    incid_multa_fgts_ferias         FLOAT(20) NOT NULL,
    incid_multa_fgts_terco          FLOAT(20) NOT NULL,
    multa_fgts_salario              FLOAT(20) NOT NULL,
    restituido                      CHAR(1 CHAR) NOT NULL,
    login_atualizacao               VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao                TIMESTAMP NOT NULL
);

ALTER TABLE tb_saldo_residual_rescisao ADD CONSTRAINT tb_saldo_residual_rescisao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_terceirizado (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(150) NOT NULL,
    cpf                 VARCHAR2(11 CHAR) NOT NULL,
    ativo               CHAR(1 CHAR) NOT NULL,
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_terceirizado ADD CONSTRAINT tb_terceirizado_pk PRIMARY KEY ( cod );

ALTER TABLE tb_terceirizado ADD CONSTRAINT tb_terceirizado_cpf_un UNIQUE ( cpf );

CREATE TABLE tb_terceirizado_contrato (
    cod                     INTEGER NOT NULL,
    cod_contrato            INTEGER NOT NULL,
    cod_terceirizado        INTEGER NOT NULL,
    data_disponibilizacao   DATE NOT NULL,
    data_desligamento       DATE,
    login_atualizacao       VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao        TIMESTAMP NOT NULL
);

ALTER TABLE tb_terceirizado_contrato ADD CONSTRAINT tb_terceirizado_contrato_pk PRIMARY KEY ( cod );

CREATE TABLE tb_tipo_evento_contratual (
    cod                 INTEGER NOT NULL,
    tipo                VARCHAR2(100 CHAR) NOT NULL,
    login_atualizacao   VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_tipo_evento_contratual ADD CONSTRAINT tb_tipo_evento_contratual_pk PRIMARY KEY ( cod );

CREATE TABLE tb_tipo_rescisao (
    cod                 INTEGER NOT NULL,
    tipo_rescisao       VARCHAR2(50 CHAR) NOT NULL,
    login_atualizacao   VARCHAR2(150 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_tipo_rescisao ADD CONSTRAINT tb_tipo_rescisao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_tipo_restituicao (
    cod                 INTEGER NOT NULL,
    nome                VARCHAR2(50 CHAR) NOT NULL,
    login_atualizacao   VARCHAR2(150) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_tipo_restituicao ADD CONSTRAINT tb_tipo_restituicao_pk PRIMARY KEY ( cod );

CREATE TABLE tb_total_mensal_a_reter (
    cod                         INTEGER NOT NULL,
    cod_terceirizado_contrato   INTEGER NOT NULL,
    ferias                      FLOAT(20) NOT NULL,
    terco_constitucional        FLOAT(20) NOT NULL,
    decimo_terceiro             FLOAT(20) NOT NULL,
    incidencia_submodulo_4_1    FLOAT(20) NOT NULL,
    multa_fgts                  FLOAT(20) NOT NULL,
    total                       FLOAT(20) NOT NULL,
    data_referencia             DATE NOT NULL,
    login_atualizacao           VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao            TIMESTAMP NOT NULL
);

ALTER TABLE tb_total_mensal_a_reter ADD CONSTRAINT tb_total_mensal_a_reter_pk PRIMARY KEY ( cod );

CREATE TABLE tb_usuario (
    cod                 INTEGER NOT NULL,
    cod_perfil          INTEGER NOT NULL,
    nome                VARCHAR2(150 CHAR) NOT NULL,
    login               VARCHAR2(100 CHAR) NOT NULL,
    login_atualizacao   VARCHAR2(100 CHAR) NOT NULL,
    data_atualizacao    TIMESTAMP NOT NULL
);

ALTER TABLE tb_usuario ADD CONSTRAINT tb_usuario_pk PRIMARY KEY ( cod );

ALTER TABLE tb_usuario ADD CONSTRAINT tb_usuario__un UNIQUE ( login );

ALTER TABLE tb_evento_contratual
    ADD CONSTRAINT tb_evento_contratual_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod );

ALTER TABLE tb_evento_contratual
    ADD CONSTRAINT tb_evento_contratual_fk2 FOREIGN KEY ( cod_tipo_evento )
        REFERENCES tb_tipo_evento_contratual ( cod );

ALTER TABLE tb_funcao_contrato
    ADD CONSTRAINT tb_funcao_contrato_fk1 FOREIGN KEY ( cod_funcao )
        REFERENCES tb_funcao ( cod );

ALTER TABLE tb_funcao_contrato
    ADD CONSTRAINT tb_funcao_contrato_fk2 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod );

ALTER TABLE tb_funcao_terceirizado
    ADD CONSTRAINT tb_funcao_terceirizado_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod );

ALTER TABLE tb_funcao_terceirizado
    ADD CONSTRAINT tb_funcao_terceirizado_fk2 FOREIGN KEY ( cod_funcao_contrato )
        REFERENCES tb_funcao_contrato ( cod );

ALTER TABLE tb_historico_gestao_contrato
    ADD CONSTRAINT tb_hist_gestao_contrato_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod );

ALTER TABLE tb_historico_gestao_contrato
    ADD CONSTRAINT tb_hist_gestao_contrato_fk2 FOREIGN KEY ( cod_usuario )
        REFERENCES tb_usuario ( cod );

ALTER TABLE tb_historico_gestao_contrato
    ADD CONSTRAINT tb_hist_gestao_contrato_fk3 FOREIGN KEY ( cod_perfil_gestao )
        REFERENCES tb_perfil_gestao ( cod );

ALTER TABLE tb_percentual_contrato
    ADD CONSTRAINT tb_percentual_contrato_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod );

ALTER TABLE tb_percentual_contrato
    ADD CONSTRAINT tb_percentual_contrato_fk2 FOREIGN KEY ( cod_rubrica )
        REFERENCES tb_rubrica ( cod );

ALTER TABLE tb_percentual_estatico
    ADD CONSTRAINT tb_percentual_estatico_fk1 FOREIGN KEY ( cod_rubrica )
        REFERENCES tb_rubrica ( cod );

ALTER TABLE tb_remuneracao_fun_con
    ADD CONSTRAINT tb_remuneracao_fun_con_fk1 FOREIGN KEY ( cod_funcao_contrato )
        REFERENCES tb_funcao_contrato ( cod );

ALTER TABLE tb_remuneracao_fun_con
    ADD CONSTRAINT tb_remuneracao_fun_con_fk2 FOREIGN KEY ( cod_convencao_coletiva )
        REFERENCES tb_convencao_coletiva ( cod );

ALTER TABLE tb_restituicao_decimo_terceiro
    ADD CONSTRAINT tb_restituicao_dec_ter_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod );

ALTER TABLE tb_restituicao_decimo_terceiro
    ADD CONSTRAINT tb_restituicao_dec_ter_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod );

ALTER TABLE tb_saldo_residual_dec_ter
    ADD CONSTRAINT tb_restituicao_dec_terceiro_fk FOREIGN KEY ( cod_restituicao_dec_terceiro )
        REFERENCES tb_restituicao_decimo_terceiro ( cod );

ALTER TABLE tb_restituicao_ferias
    ADD CONSTRAINT tb_restituicao_ferias_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod );

ALTER TABLE tb_saldo_residual_ferias
    ADD CONSTRAINT tb_restituicao_ferias_fk1v2 FOREIGN KEY ( cod_restituicao_ferias )
        REFERENCES tb_restituicao_ferias ( cod );

ALTER TABLE tb_restituicao_ferias
    ADD CONSTRAINT tb_restituicao_ferias_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod );

ALTER TABLE tb_restituicao_rescisao
    ADD CONSTRAINT tb_restituicao_rescisao_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod );

ALTER TABLE tb_restituicao_rescisao
    ADD CONSTRAINT tb_restituicao_rescisao_fk2 FOREIGN KEY ( cod_tipo_rescisao )
        REFERENCES tb_tipo_rescisao ( cod );

ALTER TABLE tb_restituicao_rescisao
    ADD CONSTRAINT tb_restituicao_rescisao_fk3 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod );

ALTER TABLE tb_retro_percentual_estatico
    ADD CONSTRAINT tb_retro_percent_estatico_fk1 FOREIGN KEY ( cod_percentual_estatico )
        REFERENCES tb_percentual_estatico ( cod );

ALTER TABLE tb_retroatividade_percentual
    ADD CONSTRAINT tb_retro_percentual_fk1 FOREIGN KEY ( cod_percentual_contrato )
        REFERENCES tb_percentual_contrato ( cod );

ALTER TABLE tb_retroatividade_remuneracao
    ADD CONSTRAINT tb_retro_remuneracao_fk1 FOREIGN KEY ( cod_rem_funcao_contrato )
        REFERENCES tb_remuneracao_fun_con ( cod );

ALTER TABLE tb_retroatividade_total_mensal
    ADD CONSTRAINT tb_retro_total_mensal_fk1 FOREIGN KEY ( cod_total_mensal_a_reter )
        REFERENCES tb_total_mensal_a_reter ( cod );

ALTER TABLE tb_saldo_residual_rescisao
    ADD CONSTRAINT tb_saldo_residual_rescisao_fk1 FOREIGN KEY ( cod_restituicao_rescisao )
        REFERENCES tb_restituicao_rescisao ( cod );

ALTER TABLE tb_terceirizado_contrato
    ADD CONSTRAINT tb_terceirizado_contrato_fk1 FOREIGN KEY ( cod_terceirizado )
        REFERENCES tb_terceirizado ( cod );

ALTER TABLE tb_terceirizado_contrato
    ADD CONSTRAINT tb_terceirizado_contrato_fk2 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod );

ALTER TABLE tb_total_mensal_a_reter
    ADD CONSTRAINT tb_total_mensal_a_reter_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod );

ALTER TABLE tb_usuario
    ADD CONSTRAINT tb_usuario_perfil_fk FOREIGN KEY ( cod_perfil )
        REFERENCES tb_perfil_usuario ( cod );

CREATE SEQUENCE tb_contrato_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_contrato_cod_trg BEFORE
    INSERT ON tb_contrato
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_contrato_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_convencao_coletiva_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_convencao_coletiva_cod_trg BEFORE
    INSERT ON tb_convencao_coletiva
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_convencao_coletiva_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_evento_contratual_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_evento_contratual_cod_trg BEFORE
    INSERT ON tb_evento_contratual
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_evento_contratual_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_funcao_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_funcao_cod_trg BEFORE
    INSERT ON tb_funcao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_funcao_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_funcao_contrato_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_funcao_contrato_cod_trg BEFORE
    INSERT ON tb_funcao_contrato
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_funcao_contrato_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_funcao_terceirizado_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_funcao_terceirizado_cod_trg BEFORE
    INSERT ON tb_funcao_terceirizado
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_funcao_terceirizado_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_historico_gestao_contrato_c START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_historico_gestao_contrato_c BEFORE
    INSERT ON tb_historico_gestao_contrato
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_historico_gestao_contrato_c.nextval;
END;
/

CREATE SEQUENCE tb_percentual_contrato_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_percentual_contrato_cod_trg BEFORE
    INSERT ON tb_percentual_contrato
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_percentual_contrato_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_percentual_estatico_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_percentual_estatico_cod_trg BEFORE
    INSERT ON tb_percentual_estatico
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_percentual_estatico_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_perfil_gestao_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_perfil_gestao_cod_trg BEFORE
    INSERT ON tb_perfil_gestao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_perfil_gestao_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_perfil_usuario_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_perfil_usuario_cod_trg BEFORE
    INSERT ON tb_perfil_usuario
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_perfil_usuario_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_remuneracao_fun_con_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_remuneracao_fun_con_cod_trg BEFORE
    INSERT ON tb_remuneracao_fun_con
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_remuneracao_fun_con_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_rest_dec_ter_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_rest_dec_ter_trg BEFORE
    INSERT ON tb_restituicao_decimo_terceiro
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_rest_dec_ter_seq.nextval;
END;
/

CREATE SEQUENCE tb_restituicao_ferias_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_restituicao_ferias_cod_trg BEFORE
    INSERT ON tb_restituicao_ferias
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_restituicao_ferias_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_restituicao_rescisao_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_restituicao_rescisao_cod BEFORE
    INSERT ON tb_restituicao_rescisao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_restituicao_rescisao_cod.nextval;
END;
/

CREATE SEQUENCE tb_retro_percentual_estatico_c START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_retro_percentual_estatico_c BEFORE
    INSERT ON tb_retro_percentual_estatico
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_retro_percentual_estatico_c.nextval;
END;
/

CREATE SEQUENCE tb_retroatividade_percentual_c START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_retroatividade_percentual_c BEFORE
    INSERT ON tb_retroatividade_percentual
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_retroatividade_percentual_c.nextval;
END;
/

CREATE SEQUENCE tb_retroatividade_remuneracao_ START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_retroatividade_remuneracao_ BEFORE
    INSERT ON tb_retroatividade_remuneracao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_retroatividade_remuneracao_.nextval;
END;
/

CREATE SEQUENCE tb_ret_tot_men_a_reter_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_ret_tot_men_a_reter_cod_trg BEFORE
    INSERT ON tb_retroatividade_total_mensal
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_ret_tot_men_a_reter_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_rubrica_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_rubrica_cod_trg BEFORE
    INSERT ON tb_rubrica
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_rubrica_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_saldo_residual_dec_ter_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_saldo_residual_dec_ter_cod BEFORE
    INSERT ON tb_saldo_residual_dec_ter
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_saldo_residual_dec_ter_cod.nextval;
END;
/

CREATE SEQUENCE tb_saldo_residual_ferias_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_saldo_residual_ferias_cod BEFORE
    INSERT ON tb_saldo_residual_ferias
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_saldo_residual_ferias_cod.nextval;
END;
/

CREATE SEQUENCE tb_saldo_residual_rescisao_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_saldo_residual_rescisao_cod BEFORE
    INSERT ON tb_saldo_residual_rescisao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_saldo_residual_rescisao_cod.nextval;
END;
/

CREATE SEQUENCE tb_terceirizado_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_terceirizado_cod_trg BEFORE
    INSERT ON tb_terceirizado
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_terceirizado_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_terceirizado_contrato_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_terceirizado_contrato_cod BEFORE
    INSERT ON tb_terceirizado_contrato
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_terceirizado_contrato_cod.nextval;
END;
/

CREATE SEQUENCE tb_tipo_evento_contratual_cod START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_tipo_evento_contratual_cod BEFORE
    INSERT ON tb_tipo_evento_contratual
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_tipo_evento_contratual_cod.nextval;
END;
/

CREATE SEQUENCE tb_tipo_rescisao_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_tipo_rescisao_cod_trg BEFORE
    INSERT ON tb_tipo_rescisao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_tipo_rescisao_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_tipo_restituicao_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_tipo_restituicao_cod_trg BEFORE
    INSERT ON tb_tipo_restituicao
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_tipo_restituicao_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_tot_men_a_reter_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_tot_men_a_reter_cod_trg BEFORE
    INSERT ON tb_total_mensal_a_reter
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_tot_men_a_reter_cod_seq.nextval;
END;
/

CREATE SEQUENCE tb_usuario_cod_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER tb_usuario_cod_trg BEFORE
    INSERT ON tb_usuario
    FOR EACH ROW
    WHEN ( new.cod IS NULL )
BEGIN
    :new.cod := tb_usuario_cod_seq.nextval;
END;
/



-- Relatório do Resumo do Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                            30
-- CREATE INDEX                             0
-- ALTER TABLE                             68
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                          30
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                         30
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- TSDP POLICY                              0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0

END "P_SISCOVI_DATABASE_CREATION";
