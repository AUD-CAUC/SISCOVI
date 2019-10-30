-- Gerado por Oracle SQL Developer Data Modeler 17.4.0.355.2121
--   em:        2018-03-06 13:17:12 BRT
--   site:      SQL Server 2012
--   tipo:      SQL Server 2012

USE [SISCOVI]

GO

CREATE TABLE tb_hist_restituicao_dec_ter 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_DEC_TERCEIRO INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL,parcela INTEGER,data_inicio_contagem DATE,valor FLOAT(20),incidencia_submodulo_4_1
FLOAT(20),data_referencia DATE,autorizado CHAR(1),restituido CHAR(1),observacao VARCHAR(500),login_atualizacao VARCHAR(150),data_atualizacao
datetime 
    )

ON "default" 

go

ALTER TABLE TB_HIST_RESTITUICAO_DEC_TER ADD constraint tb_hist_restituicao_dec_ter_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON ) 

     ON "default" 
     
     go

CREATE TABLE tb_hist_restituicao_ferias 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_FERIAS INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL,data_inicio_periodo_aquisitivo DATE,data_fim_periodo_aquisitivo
DATE,data_inicio_usufruto DATE,data_fim_usufruto DATE,dias_vendidos INTEGER,valor_ferias FLOAT,valor_terco_constitucional FLOAT,incid_submod_4_1_ferias
FLOAT,incid_submod_4_1_terco FLOAT,parcela INTEGER,data_referencia DATE,autorizado CHAR,restituido CHAR,observacao VARCHAR(500),login_atualizacao
VARCHAR(150),data_atualizacao datetime 
    )

ON "default" 

go

ALTER TABLE TB_HIST_RESTITUICAO_FERIAS ADD constraint tb_hist_restituicao_ferias_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON ) 

     ON "default" 
     
     go

CREATE TABLE tb_hist_restituicao_rescisao 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_RESCISAO INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL , 
     COD_TIPO_RESCISAO INTEGER NOT NULL , 
     DATA_DESLIGAMENTO DATE NOT NULL , 
     DATA_INICIO_FERIAS DATE , 
     DATA_FIM_FERIAS DATE , 
     DATA_INICIO_FERIAS_PROP DATE , 
     DATA_FIM_FERIAS_PROP DATE , 
     DATA_INICIO_CONTAGEM_DEC_TER DATE NOT NULL , 
     VALOR_DECIMO_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     VALOR_FERIAS FLOAT (20) NOT NULL , 
     VALOR_TERCO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO FLOAT (20) NOT NULL , 
     VALOR_FERIAS_PROP FLOAT (20) NOT NULL , 
     VALOR_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO_PROP FLOAT (20) NOT NULL , 
     MULTA_FGTS_SALARIO FLOAT (20) NOT NULL , 
	 MULTA_FGTS_RESTANTE FLOAT (20) NOT NULL ,
     DATA_REFERENCIA DATE NOT NULL , 
     AUTORIZADO CHAR , 
     RESTITUIDO CHAR , 
     OBSERVACAO VARCHAR (500) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL ) 

ON "default" 

go

ALTER TABLE TB_HIST_RESTITUICAO_RESCISAO ADD constraint tb_hist_restituicao_rescisao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )

     ON "default" 
     
     go

CREATE TABLE tb_contrato

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME_EMPRESA VARCHAR (200) NOT NULL , 
     CNPJ VARCHAR (14) NOT NULL , 
     NUMERO_CONTRATO VARCHAR (50) NOT NULL , 
     NUMERO_PROCESSO_STJ VARCHAR (50) NOT NULL , 
     SE_ATIVO CHAR (1) NOT NULL , 
     OBJETO VARCHAR (500) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_CONTRATO ADD constraint tb_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_convencao_coletiva

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (150) NOT NULL , 
     SIGLA VARCHAR (200) NOT NULL , 
     DATA_BASE DATE NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_CONVENCAO_COLETIVA ADD constraint tb_convencao_coletiva_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_evento_contratual

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_TIPO_EVENTO INTEGER NOT NULL , 
     PRORROGACAO CHAR (1) NOT NULL , 
     ASSUNTO VARCHAR (400) , 
     DATA_INICIO_VIGENCIA DATE NOT NULL , 
     DATA_FIM_VIGENCIA DATE NOT NULL , 
     DATA_ASSINATURA DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_EVENTO_CONTRATUAL ADD constraint tb_evento_contratual_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_funcao

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (150) NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_FUNCAO ADD constraint tb_funcao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 

    GO

    ALTER TABLE tb_funcao add constraint tb_cargo_un unique nonclustered(nome)
     ON "default" 
     
     go

CREATE TABLE tb_funcao_contrato

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_FUNCAO INTEGER NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_FUNCAO_CONTRATO ADD constraint tb_funcao_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 

    GO

    ALTER TABLE tb_funcao_contrato add constraint tb_funcao_contrato_un unique nonclustered(cod_contrato
,cod_funcao)
     ON "default" 
     
     go

CREATE TABLE tb_funcao_terceirizado

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     COD_FUNCAO_CONTRATO INTEGER NOT NULL , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_FUNCAO_TERCEIRIZADO ADD constraint tb_funcao_terceirizado_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_historico_gestao_contrato

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_USUARIO INTEGER NOT NULL , 
     COD_PERFIL_GESTAO INTEGER NOT NULL , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_HISTORICO_GESTAO_CONTRATO ADD constraint tb_hist_gestao_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_percentual_contrato

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_RUBRICA INTEGER NOT NULL , 
     PERCENTUAL FLOAT (20) NOT NULL , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     DATA_ADITAMENTO DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_PERCENTUAL_CONTRATO ADD constraint tb_percentual_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_percentual_estatico

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RUBRICA INTEGER NOT NULL , 
     PERCENTUAL FLOAT (20) NOT NULL , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     DATA_ADITAMENTO DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
     go

ALTER TABLE TB_PERCENTUAL_ESTATICO ADD constraint tb_percentual_estatico_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default"
     
      go

CREATE TABLE tb_perfil_gestao

( cod INTEGER NOT NULL, 
     NOME VARCHAR (150) NOT NULL , 
     SIGLA VARCHAR (50) NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
     go

ALTER TABLE TB_PERFIL_GESTAO ADD constraint tb_perfil_gestao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_perfil_usuario

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (150) NOT NULL , 
     SIGLA VARCHAR (50) NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
     go

ALTER TABLE TB_PERFIL_USUARIO ADD constraint tb_perfil_usuario_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
    GO
    ALTER TABLE tb_perfil_usuario add constraint tb_perfil_sigla_un unique nonclustered(sigla)
     ON "default"
go

CREATE TABLE tb_remuneracao_fun_con

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_FUNCAO_CONTRATO INTEGER NOT NULL , 
     COD_CONVENCAO_COLETIVA INTEGER , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     DATA_ADITAMENTO DATE NOT NULL , 
     REMUNERACAO FLOAT (20) NOT NULL , 
     TRIENIOS FLOAT (20) , 
     ADICIONAIS FLOAT (20) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_REMUNERACAO_FUN_CON ADD constraint tb_remuneracao_fun_con_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_restituicao_decimo_terceiro 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL , 
     PARCELA INTEGER NOT NULL , 
     DATA_INICIO_CONTAGEM DATE NOT NULL , 
     VALOR FLOAT (20) NOT NULL , 
     INCIDENCIA_SUBMODULO_4_1 FLOAT (20) NOT NULL , 
     DATA_REFERENCIA DATE NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     OBSERVACAO CHAR (500) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL )
    ON "default" 
    
    go

ALTER TABLE TB_RESTITUICAO_DECIMO_TERCEIRO ADD constraint tb_restituicao_dec_ter_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_restituicao_ferias 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL , 
     DATA_INICIO_PERIODO_AQUISITIVO DATE NOT NULL , 
     DATA_FIM_PERIODO_AQUISITIVO DATE NOT NULL , 
     DATA_INICIO_USUFRUTO DATE NOT NULL , 
     DATA_FIM_USUFRUTO DATE NOT NULL , 
     DIAS_VENDIDOS INTEGER NOT NULL , 
     VALOR_FERIAS FLOAT (20) NOT NULL , 
     VALOR_TERCO_CONSTITUCIONAL FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS FLOAT (20) , 
     INCID_SUBMOD_4_1_TERCO FLOAT (20) NOT NULL , 
     PARCELA INTEGER NOT NULL , 
     DATA_REFERENCIA DATE NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     OBSERVACAO CHAR (500) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL )
    ON "default"
    
    go

ALTER TABLE TB_RESTITUICAO_FERIAS ADD constraint tb_restituicao_ferias_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_restituicao_rescisao 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     COD_TIPO_RESTITUICAO INTEGER NOT NULL , 
     COD_TIPO_RESCISAO INTEGER NOT NULL , 
     DATA_DESLIGAMENTO DATE NOT NULL , 
     DATA_INICIO_FERIAS DATE , 
     DATA_FIM_FERIAS DATE , 
     DATA_INICIO_FERIAS_PROP DATE , 
     DATA_FIM_FERIAS_PROP DATE , 
     DATA_INICIO_CONTAGEM_DEC_TER DATE NOT NULL , 
     VALOR_DECIMO_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     VALOR_FERIAS FLOAT (20) NOT NULL , 
     VALOR_TERCO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO FLOAT (20) NOT NULL , 
     VALOR_FERIAS_PROP FLOAT (20) NOT NULL , 
     VALOR_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO_PROP FLOAT (20) NOT NULL , 
     MULTA_FGTS_SALARIO FLOAT (20) NOT NULL , 
	 MULTA_FGTS_RESTANTE FLOAT (20) NOT NULL ,
     DATA_REFERENCIA DATE NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     OBSERVACAO CHAR (500) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL )

    ON "default"
    
    go

ALTER TABLE TB_RESTITUICAO_RESCISAO ADD constraint tb_restituicao_rescisao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     
     ON "default" 
     
     go

CREATE TABLE tb_retro_percentual_estatico

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_PERCENTUAL_ESTATICO INTEGER NOT NULL , 
     INICIO DATE NOT NULL , 
     FIM DATE NOT NULL , 
     DATA_COBRANCA DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_RETRO_PERCENTUAL_ESTATICO ADD constraint tb_retro_percent_estatico_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_retroatividade_percentual

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_PERCENTUAL_CONTRATO INTEGER NOT NULL , 
     INICIO DATE NOT NULL , 
     FIM DATE NOT NULL , 
     DATA_COBRANCA DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
    go

ALTER TABLE TB_RETROATIVIDADE_PERCENTUAL ADD constraint tb_retro_percentual_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
    go

CREATE TABLE tb_retroatividade_remuneracao

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_REM_FUNCAO_CONTRATO INTEGER NOT NULL , 
     INICIO DATE NOT NULL , 
     FIM DATE NOT NULL , 
     DATA_COBRANCA DATE NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_RETROATIVIDADE_REMUNERACAO ADD constraint tb_retro_remuneracao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_retroatividade_total_mensal

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TOTAL_MENSAL_A_RETER INTEGER NOT NULL , 
     FERIAS FLOAT (20) NOT NULL , 
     TERCO_CONSTITUCIONAL FLOAT (20) NOT NULL , 
     DECIMO_TERCEIRO FLOAT (20) NOT NULL , 
     INCIDENCIA_SUBMODULO_4_1 FLOAT (20) NOT NULL , 
     MULTA_FGTS FLOAT (20) NOT NULL , 
     TOTAL FLOAT (20) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_RETROATIVIDADE_TOTAL_MENSAL ADD constraint tb_retroativ_total_mensal_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_rubrica

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (150) NOT NULL , 
     SIGLA VARCHAR (50) NOT NULL , 
     DESCRICAO VARCHAR (400) , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_RUBRICA ADD constraint tb_rubrica_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 

    GO

    ALTER TABLE tb_rubrica add constraint tb_rubricas_sigla_un unique nonclustered(sigla)
     ON "default" 
     
     go

CREATE TABLE tb_saldo_residual_dec_ter 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_DEC_TERCEIRO INTEGER NOT NULL , 
     VALOR FLOAT (20) NOT NULL , 
     INCIDENCIA_SUBMODULO_4_1 FLOAT (20) NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
    go

ALTER TABLE TB_SALDO_RESIDUAL_DEC_TER ADD constraint tb_saldo_residual_dec_ter_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_saldo_residual_ferias 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_FERIAS INTEGER NOT NULL , 
     VALOR_FERIAS FLOAT (20) NOT NULL , 
     VALOR_TERCO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO FLOAT (20) NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
     go

ALTER TABLE TB_SALDO_RESIDUAL_FERIAS ADD constraint tb_saldo_residual_ferias_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_saldo_residual_rescisao 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_RESTITUICAO_RESCISAO INTEGER NOT NULL , 
     VALOR_DECIMO_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_DEC_TERCEIRO FLOAT (20) NOT NULL , 
     VALOR_FERIAS FLOAT (20) NOT NULL , 
     VALOR_TERCO FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO FLOAT (20) NOT NULL , 
     VALOR_FERIAS_PROP FLOAT (20) NOT NULL , 
     VALOR_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_SUBMOD_4_1_TERCO_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_FERIAS_PROP FLOAT (20) NOT NULL , 
     INCID_MULTA_FGTS_TERCO_PROP FLOAT (20) NOT NULL , 
     MULTA_FGTS_SALARIO FLOAT (20) NOT NULL , 
	 MULTA_FGTS_RESTANTE FLOAT (20) NOT NULL ,
     AUTORIZADO CHAR (1) , 
     RESTITUIDO CHAR (1) , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL )

    ON "default" 
    
    go

ALTER TABLE TB_SALDO_RESIDUAL_RESCISAO ADD constraint tb_saldo_residual_rescisao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_terceirizado

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (150) NOT NULL , 
     CPF VARCHAR (11) NOT NULL , 
     ATIVO CHAR (1) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_TERCEIRIZADO ADD constraint tb_terceirizado_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
    GO
    ALTER TABLE tb_terceirizado add constraint tb_terceirizado_cpf_un unique nonclustered(cpf)
     ON "default" 
     
     go

CREATE TABLE tb_terceirizado_contrato

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_CONTRATO INTEGER NOT NULL , 
     COD_TERCEIRIZADO INTEGER NOT NULL , 
     DATA_DISPONIBILIZACAO DATE NOT NULL , 
     DATA_DESLIGAMENTO DATE , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
     go

ALTER TABLE TB_TERCEIRIZADO_CONTRATO ADD constraint tb_terceirizado_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default"
     
      go

CREATE TABLE tb_tipo_evento_contratual

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     TIPO VARCHAR (100) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_TIPO_EVENTO_CONTRATUAL ADD constraint tb_tipo_evento_contratual_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_tipo_rescisao

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     TIPO_RESCISAO VARCHAR (50) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_TIPO_RESCISAO ADD constraint tb_tipo_rescisao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_tipo_restituicao

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     NOME VARCHAR (50) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_TIPO_RESTITUICAO ADD constraint tb_tipo_restituicao_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
     
     go

CREATE TABLE tb_total_mensal_a_reter 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     COD_FUNCAO_TERCEIRIZADO INTEGER NOT NULL , 
     FERIAS FLOAT (20) NOT NULL , 
     TERCO_CONSTITUCIONAL FLOAT (20) NOT NULL , 
     DECIMO_TERCEIRO FLOAT (20) NOT NULL , 
     INCIDENCIA_SUBMODULO_4_1 FLOAT (20) NOT NULL , 
     MULTA_FGTS FLOAT (20) NOT NULL , 
     TOTAL FLOAT (20) NOT NULL , 
     DATA_REFERENCIA DATE NOT NULL , 
     AUTORIZADO CHAR (1) , 
     RETIDO CHAR (1) ,
	 PEDIDO CHAR (1) DEFAULT 'N',
     OBSERVACAO VARCHAR(500), 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default"
    
    go

ALTER TABLE TB_TOTAL_MENSAL_A_RETER ADD constraint tb_total_mensal_a_reter_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
	 
	 go

CREATE TABLE tb_usuario

( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_PERFIL INTEGER NOT NULL , 
     NOME VARCHAR (150) NOT NULL , 
     LOGIN VARCHAR (100) NOT NULL , 
     PASSWORD VARCHAR (150) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (100) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
    
    go

ALTER TABLE TB_USUARIO ADD constraint tb_usuario_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
    GO
    ALTER TABLE tb_usuario add constraint tb_usuario_un unique nonclustered(login)
     ON "default"
     
      go

CREATE TABLE tb_trienio_terc_contrato 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     COD_TERCEIRIZADO_CONTRATO INTEGER NOT NULL , 
     NUMERO_DE_TRIENIOS INTEGER NOT NULL , 
     DATA_INICIO DATE NOT NULL , 
     DATA_FIM DATE , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO DATETIME NOT NULL 
    )
    ON "default" 
	
	go

CREATE TABLE tb_percentual_dinamico 
    ( cod INTEGER NOT NULL IDENTITY NOT FOR REPLICATION , 
     PERCENTUAL FLOAT (20) NOT NULL , 
     LOGIN_ATUALIZACAO VARCHAR (150) NOT NULL , 
     DATA_ATUALIZACAO datetime NOT NULL ) 
	 go

ALTER TABLE TB_PERCENTUAL_DINAMICO ADD constraint tb_percentual_dinamico_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON ) 
	 go


ALTER TABLE TB_TRIENIO_TERC_CONTRATO ADD constraint tb_trienio_terc_contrato_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON )
     ON "default" 
	 
	 go

CREATE TABLE tb_saldo_conta_vinculada (
    cod                 INTEGER NOT NULL,
    cod_contrato        INTEGER NOT NULL,
    saldo               FLOAT(20) NOT NULL,
    data_saldo          DATE NOT NULL,
    login_atualizacao   VARCHAR(150) NOT NULL,
    data_atualizacao    datetime NOT NULL
)

go

ALTER TABLE TB_SALDO_CONTA_VINCULADA ADD constraint tb_saldo_conta_vinculada_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON ) 
     
     go

ALTER TABLE TB_SALDO_CONTA_VINCULADA
    ADD CONSTRAINT tb_saldo_conta_vinculada_fk FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

    CREATE TABLE tb_lista_percentuais (
    cod                 INTEGER NOT NULL,
    percentual          FLOAT(3) NOT NULL,
    login_atualizacao   VARCHAR(150) NOT NULL,
    data_atualizacao    datetime NOT NULL
)

go

ALTER TABLE TB_LISTA_PERCENTUAIS ADD constraint tb_lista_percentuais_pk PRIMARY KEY CLUSTERED (COD)
     WITH (
     ALLOW_PAGE_LOCKS = ON , 
     ALLOW_ROW_LOCKS = ON ) 
     
     go


ALTER TABLE TB_TRIENIO_TERC_CONTRATO
    ADD CONSTRAINT tb_trienio_terc_contrato_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
	
	go

ALTER TABLE TB_EVENTO_CONTRATUAL
    ADD CONSTRAINT tb_evento_contratual_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_EVENTO_CONTRATUAL
    ADD CONSTRAINT tb_evento_contratual_fk2 FOREIGN KEY ( cod_tipo_evento )
        REFERENCES tb_tipo_evento_contratual ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_FUNCAO_CONTRATO
    ADD CONSTRAINT tb_funcao_contrato_fk1 FOREIGN KEY ( cod_funcao )
        REFERENCES tb_funcao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_FUNCAO_CONTRATO
    ADD CONSTRAINT tb_funcao_contrato_fk2 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_FUNCAO_TERCEIRIZADO
    ADD CONSTRAINT tb_funcao_terceirizado_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_FUNCAO_TERCEIRIZADO
    ADD CONSTRAINT tb_funcao_terceirizado_fk2 FOREIGN KEY ( cod_funcao_contrato )
        REFERENCES tb_funcao_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_HISTORICO_GESTAO_CONTRATO
    ADD CONSTRAINT tb_hist_gestao_contrato_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_HISTORICO_GESTAO_CONTRATO
    ADD CONSTRAINT tb_hist_gestao_contrato_fk2 FOREIGN KEY ( cod_usuario )
        REFERENCES tb_usuario ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_HISTORICO_GESTAO_CONTRATO
    ADD CONSTRAINT tb_hist_gestao_contrato_fk3 FOREIGN KEY ( cod_perfil_gestao )
        REFERENCES tb_perfil_gestao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_PERCENTUAL_CONTRATO
    ADD CONSTRAINT tb_percentual_contrato_fk1 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_PERCENTUAL_CONTRATO
    ADD CONSTRAINT tb_percentual_contrato_fk2 FOREIGN KEY ( cod_rubrica )
        REFERENCES tb_rubrica ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_PERCENTUAL_ESTATICO
    ADD CONSTRAINT tb_percentual_estatico_fk1 FOREIGN KEY ( cod_rubrica )
        REFERENCES tb_rubrica ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_REMUNERACAO_FUN_CON
    ADD CONSTRAINT tb_remuneracao_fun_con_fk1 FOREIGN KEY ( cod_funcao_contrato )
        REFERENCES tb_funcao_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_REMUNERACAO_FUN_CON
    ADD CONSTRAINT tb_remuneracao_fun_con_fk2 FOREIGN KEY ( cod_convencao_coletiva )
        REFERENCES tb_convencao_coletiva ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_RESTITUICAO_DECIMO_TERCEIRO
    ADD CONSTRAINT tb_restituicao_dec_ter_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RESTITUICAO_DECIMO_TERCEIRO
    ADD CONSTRAINT tb_restituicao_dec_ter_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_SALDO_RESIDUAL_DEC_TER
    ADD CONSTRAINT tb_restituicao_dec_terceiro_fk FOREIGN KEY ( cod_restituicao_dec_terceiro )
        REFERENCES tb_restituicao_decimo_terceiro ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_RESTITUICAO_FERIAS
    ADD CONSTRAINT tb_restituicao_ferias_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_SALDO_RESIDUAL_FERIAS
    ADD CONSTRAINT tb_restituicao_ferias_fk1v2 FOREIGN KEY ( cod_restituicao_ferias )
        REFERENCES tb_restituicao_ferias ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_RESTITUICAO_FERIAS
    ADD CONSTRAINT tb_restituicao_ferias_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_restituicao_rescisao_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_restituicao_rescisao_fk2 FOREIGN KEY ( cod_tipo_rescisao )
        REFERENCES tb_tipo_rescisao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_restituicao_rescisao_fk3 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
     go

ALTER TABLE TB_RETRO_PERCENTUAL_ESTATICO
    ADD CONSTRAINT tb_retro_percent_estatico_fk1 FOREIGN KEY ( cod_percentual_estatico )
        REFERENCES tb_percentual_estatico ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RETROATIVIDADE_PERCENTUAL
    ADD CONSTRAINT tb_retro_percentual_fk1 FOREIGN KEY ( cod_percentual_contrato )
        REFERENCES tb_percentual_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RETROATIVIDADE_REMUNERACAO
    ADD CONSTRAINT tb_retro_remuneracao_fk1 FOREIGN KEY ( cod_rem_funcao_contrato )
        REFERENCES tb_remuneracao_fun_con ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_RETROATIVIDADE_TOTAL_MENSAL
    ADD CONSTRAINT tb_retro_total_mensal_fk1 FOREIGN KEY ( cod_total_mensal_a_reter )
        REFERENCES tb_total_mensal_a_reter ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_SALDO_RESIDUAL_RESCISAO
    ADD CONSTRAINT tb_saldo_residual_rescisao_fk1 FOREIGN KEY ( cod_restituicao_rescisao )
        REFERENCES tb_restituicao_rescisao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_TERCEIRIZADO_CONTRATO
    ADD CONSTRAINT tb_terceirizado_contrato_fk1 FOREIGN KEY ( cod_terceirizado )
        REFERENCES tb_terceirizado ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_TERCEIRIZADO_CONTRATO
    ADD CONSTRAINT tb_terceirizado_contrato_fk2 FOREIGN KEY ( cod_contrato )
        REFERENCES tb_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_TOTAL_MENSAL_A_RETER
    ADD CONSTRAINT tb_total_mensal_a_reter_fk1 FOREIGN KEY ( cod_terceirizado_contrato )
        REFERENCES tb_terceirizado_contrato ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_TOTAL_MENSAL_A_RETER
    ADD CONSTRAINT tb_total_mensal_a_reter_fk2 FOREIGN KEY ( cod_funcao_terceirizado )
        REFERENCES tb_funcao_terceirizado ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_USUARIO
    ADD CONSTRAINT tb_usuario_perfil_fk FOREIGN KEY ( cod_perfil )
        REFERENCES tb_perfil_usuario ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 

    go

ALTER TABLE TB_HIST_RESTITUICAO_DEC_TER
    ADD CONSTRAINT tb_hist_rest_dec_ter_fk1 FOREIGN KEY ( cod_restituicao_dec_terceiro )
        REFERENCES tb_restituicao_decimo_terceiro ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_DEC_TER
    ADD CONSTRAINT tb_hist_rest_dec_ter_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_FERIAS
    ADD CONSTRAINT tb_hist_restituicao_ferias_fk1 FOREIGN KEY ( cod_restituicao_ferias )
        REFERENCES tb_restituicao_ferias ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_FERIAS
    ADD CONSTRAINT tb_hist_restituicao_ferias_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action 
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_hist_restituicao_rescisao_fk1 FOREIGN KEY ( cod_restituicao_rescisao )
        REFERENCES tb_restituicao_rescisao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_hist_restituicao_rescisao_fk2 FOREIGN KEY ( cod_tipo_restituicao )
        REFERENCES tb_tipo_restituicao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
    go

ALTER TABLE TB_HIST_RESTITUICAO_RESCISAO
    ADD CONSTRAINT tb_hist_restituicao_rescisao_fk3 FOREIGN KEY ( cod_tipo_rescisao )
        REFERENCES tb_tipo_rescisao ( cod )
ON DELETE NO ACTION 
    ON UPDATE no action
    
    go

------------------------------------------------------------------------------------------------


-- Relatório do Resumo do Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                            20
-- CREATE INDEX                             0
-- ALTER TABLE                             47
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE DATABASE                          0
-- CREATE DEFAULT                           0
-- CREATE INDEX ON VIEW                     0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE ROLE                              0
-- CREATE RULE                              0
-- CREATE SCHEMA                            0
-- CREATE SEQUENCE                          0
-- CREATE PARTITION FUNCTION                0
-- CREATE PARTITION SCHEME                  0
-- 
-- DROP DATABASE                            0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
