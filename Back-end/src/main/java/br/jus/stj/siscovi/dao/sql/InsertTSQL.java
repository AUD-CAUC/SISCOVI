package br.jus.stj.siscovi.dao.sql;

import java.sql.*;

public class InsertTSQL {

    private Connection connection;

    public InsertTSQL(Connection connection) {

        this.connection = connection;

    }

    public int InsertTotalMensalAReter(int pCodTerceirizadoContrato,
                                       int pCodFuncaoTerceirizadoContrato,
                                       float pFerias,
                                       float pTercoConstitucional,
                                       float pDecimoTerceiro,
                                       float pIncidenciaSubmodulo41,
                                       float pMultaFgts,
                                       float pTotal,
                                       Date pDataReferencia,
                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;

        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TOTAL_MENSAL_A_RETER");

        try {

            String sql = "INSERT INTO TB_TOTAL_MENSAL_A_RETER (COD, " +
                                                              "COD_TERCEIRIZADO_CONTRATO, " +
                                                              "COD_FUNCAO_TERCEIRIZADO, " +
                                                              "FERIAS, " +
                                                              "TERCO_CONSTITUCIONAL, " +
                                                              "DECIMO_TERCEIRO, " +
                                                              "INCIDENCIA_SUBMODULO_4_1," +
                                                              "MULTA_FGTS, " +
                                                              "TOTAL, " +
                                                              "DATA_REFERENCIA, " +
                                                              "LOGIN_ATUALIZACAO, " +
                                                              "DATA_ATUALIZACAO) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pCodFuncaoTerceirizadoContrato);
            preparedStatement.setFloat(4, pFerias);
            preparedStatement.setFloat(5, pTercoConstitucional);
            preparedStatement.setFloat(6, pDecimoTerceiro);
            preparedStatement.setFloat(7, pIncidenciaSubmodulo41);
            preparedStatement.setFloat(8, pMultaFgts);
            preparedStatement.setFloat(9, pTotal);
            preparedStatement.setDate(10, pDataReferencia);
            preparedStatement.setString(11, pLoginAtualizacao);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {

            throw new RuntimeException("Erro ao tentar inserir os resultados do cálculo de Total Mensal a Reter no banco de dados !");

        }

        return vCod;

    }

    public int InsertRestituicaoFerias (int pCodTerceirizadoContrato,
                                        int pCodTipoRestituicao,
                                        int pDiasVendidos,
                                        Date pInicioFerias,
                                        Date pFimFerias,
                                        Date pInicioPeriodoAquisitivo,
                                        Date pFimPeriodoAquisitivo,
                                        int pParcela,
                                        float pTotalFerias,
                                        float pTotalTercoConstitucional,
                                        float pTotalIncidenciaFerias,
                                        float pTotalIncidenciaTerco,
                                        String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbRestituicaoFerias = consulta.RetornaCodSequenceTbRestituicaoFerias();

        try {

            String sql = "SET IDENTITY_INSERT tb_restituicao_ferias ON;" +
                        " INSERT INTO TB_RESTITUICAO_FERIAS (COD,"+
                                    " COD_TERCEIRIZADO_CONTRATO," +
                                    " COD_TIPO_RESTITUICAO," +
                                    " DATA_INICIO_PERIODO_AQUISITIVO," +
                                    " DATA_FIM_PERIODO_AQUISITIVO," +
                                    " DATA_INICIO_USUFRUTO," +
                                    " DATA_FIM_USUFRUTO," +
                                    " VALOR_FERIAS," +
                                    " VALOR_TERCO_CONSTITUCIONAL," +
                                    " INCID_SUBMOD_4_1_FERIAS," +
                                    " INCID_SUBMOD_4_1_TERCO," +
                                    " PARCELA," +
                                    " DIAS_VENDIDOS," +
                                    " DATA_REFERENCIA," +
                                    " LOGIN_ATUALIZACAO," +
                                    " DATA_ATUALIZACAO)" +
                          " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT tb_restituicao_ferias OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbRestituicaoFerias);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setDate(4, pInicioPeriodoAquisitivo);
            preparedStatement.setDate(5, pFimPeriodoAquisitivo);
            preparedStatement.setDate(6, pInicioFerias);
            preparedStatement.setDate(7, pFimFerias);
            preparedStatement.setFloat(8, pTotalFerias);
            preparedStatement.setFloat(9, pTotalTercoConstitucional);
            preparedStatement.setFloat(10, pTotalIncidenciaFerias);
            preparedStatement.setFloat(11, pTotalIncidenciaTerco);
            preparedStatement.setInt(12, pParcela);
            preparedStatement.setInt(13, pDiasVendidos);
            preparedStatement.setString(14, pLoginAtualizacao);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar inserir os resultados do cálculo de férias no banco de dados!");

        }

        return vCodTbRestituicaoFerias;

    }


    public int InsertSaldoResidualFerias (int pCodTbRestituicaoFerias,
                                           float pValorFerias,
                                           float pValorTerco,
                                           float pIncidenciaFerias,
                                           float pIncidenciaTerco,
                                           String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_SALDO_RESIDUAL_FERIAS");

        try {

            String sql = "SET IDENTITY_INSERT TB_SALDO_RESIDUAL_FERIAS ON;" +
                    " INSERT INTO TB_SALDO_RESIDUAL_FERIAS (COD," +
                                                            " COD_RESTITUICAO_FERIAS," +
                                                               " VALOR_FERIAS," +
                                                               " VALOR_TERCO," +
                                                               " INCID_SUBMOD_4_1_FERIAS," +
                                                               " INCID_SUBMOD_4_1_TERCO," +
                                                               " LOGIN_ATUALIZACAO," +
                                                               " DATA_ATUALIZACAO)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?,  CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_SALDO_RESIDUAL_FERIAS OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodTbRestituicaoFerias);
            preparedStatement.setFloat(3, pValorFerias);
            preparedStatement.setFloat(4, pValorTerco);
            preparedStatement.setFloat(5, pIncidenciaFerias);
            preparedStatement.setFloat(6, pIncidenciaTerco);
            preparedStatement.setString(7, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar inserir os resultados do cálculo de férias no banco de dados!");

        }

        return vCod;

    }

    public int InsertRestituicaoDecimoTerceiro (int pCodTerceirizadoContrato,
                                                int pCodTipoRestituicao,
                                                int pNumeroParcela,
                                                Date pInicioContagem,
                                                float pValorDecimoTerceiro,
                                                float pValorIncidencia,
                                                String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbRestituicaoDecimoTerceiro = consulta.RetornaCodSequenceTbRestituicaoDecimoTerceiro();

        try {

            String sql = "SET IDENTITY_INSERT TB_RESTITUICAO_DECIMO_TERCEIRO ON; " +
                    "INSERT INTO TB_RESTITUICAO_DECIMO_TERCEIRO (COD,"+
                    " COD_TERCEIRIZADO_CONTRATO," +
                    " COD_TIPO_RESTITUICAO," +
                    " PARCELA," +
                    " DATA_INICIO_CONTAGEM," +
                    " VALOR," +
                    " INCIDENCIA_SUBMODULO_4_1," +
                    " DATA_REFERENCIA," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_RESTITUICAO_DECIMO_TERCEIRO OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbRestituicaoDecimoTerceiro);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setInt(4, pNumeroParcela);
            preparedStatement.setDate(5, pInicioContagem);
            preparedStatement.setFloat(6, pValorDecimoTerceiro);
            preparedStatement.setFloat(7, pValorIncidencia);
            preparedStatement.setString(8, pLoginAtualizacao);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao tentar inserir dados na tabela de restituição de décimo terceiro!");

        }

        return vCodTbRestituicaoDecimoTerceiro;

    }

    public int InsertSaldoResidualDecimoTerceiro (int pCodRestituicaoDecimoTerceiro,
                                                  float pValorDecimoTerceiro,
                                                  float pValorIncidencia,
                                                  String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_SALDO_RESIDUAL_DEC_TER");

        try {

            String sql = "SET IDENTITY_INSERT TB_SALDO_RESIDUAL_DEC_TER ON;" +
                    " INSERT INTO TB_SALDO_RESIDUAL_DEC_TER (COD," +
                    " COD_RESTITUICAO_DEC_TERCEIRO," +
                    " VALOR," +
                    " INCIDENCIA_SUBMODULO_4_1," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_SALDO_RESIDUAL_DEC_TER OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodRestituicaoDecimoTerceiro);
            preparedStatement.setFloat(3, pValorDecimoTerceiro);
            preparedStatement.setFloat(4, pValorIncidencia);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar inserir dados na tabela de saldo residual de décimo terceiro!");

        }

        return vCod;

    }

    public int InsertRestituicaoRescisao (int pCodTerceirizadoContrato,
                                          int pCodTipoRestituicao,
                                          int pCodTipoRescisao,
                                          Date pDataDesligamento,
                                          Date pDataInicioFeriasIntegrais,
                                          Date pDataFimFeriasIntegrais,
                                          Date pDataInicioFeriasProporcionais,
                                          Date pDataFimFeriasProporcionais,
                                          Date pDataInicioContagemDecTer,
                                          float pValorDecimoTerceiro,
                                          float pValorIncidenciaDecimoTerceiro,
                                          float pValorFGTSDecimoTerceiro,
                                          float pValorFerias,
                                          float pValorTerco,
                                          float pValorIncidenciaFerias,
                                          float pValorIncidenciaTerco,
                                          float pValorFGTSFerias,
                                          float pValorFGTSTerco,
                                          float pValorFeriasProporcional,
                                          float pValorTercoProporcional,
                                          float pValorIncidenciaFeriasProporcional,
                                          float pValorIncidenciaTercoProporcional,
                                          float pValorFGTSFeriasProporcional,
                                          float pValorFGTSTercoProporcional,
                                          float pValorFGTSSalario,
                                          float pValorFGTSRestante,
                                          String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbRestituicaoRescisao = consulta.RetornaCodSequenceTbRestituicaoRescisao();

        try {

            String sql = "SET IDENTITY_INSERT tb_restituicao_rescisao ON;" +
                        " INSERT INTO tb_restituicao_rescisao (COD,"+
                                                             " COD_TERCEIRIZADO_CONTRATO," +
                                                             " COD_TIPO_RESTITUICAO," +
                                                             " COD_TIPO_RESCISAO," +
                                                             " DATA_DESLIGAMENTO," +
                                                             " DATA_INICIO_FERIAS," +
                                                             " DATA_FIM_FERIAS," +
                                                             " DATA_INICIO_FERIAS_PROP," +
                                                             " DATA_FIM_FERIAS_PROP," +
                                                             " DATA_INICIO_CONTAGEM_DEC_TER," +
                                                             " VALOR_DECIMO_TERCEIRO," +
                                                             " INCID_SUBMOD_4_1_DEC_TERCEIRO," +
                                                             " INCID_MULTA_FGTS_DEC_TERCEIRO," +
                                                             " VALOR_FERIAS," +
                                                             " VALOR_TERCO," +
                                                             " INCID_SUBMOD_4_1_FERIAS," +
                                                             " INCID_SUBMOD_4_1_TERCO," +
                                                             " INCID_MULTA_FGTS_FERIAS," +
                                                             " INCID_MULTA_FGTS_TERCO," +
                                                             " VALOR_FERIAS_PROP," +
                                                             " VALOR_TERCO_PROP," +
                                                             " INCID_SUBMOD_4_1_FERIAS_PROP," +
                                                             " INCID_SUBMOD_4_1_TERCO_PROP," +
                                                             " INCID_MULTA_FGTS_FERIAS_PROP," +
                                                             " INCID_MULTA_FGTS_TERCO_PROP," +
                                                             " MULTA_FGTS_SALARIO," +
                                                             " MULTA_FGTS_RESTANTE," +
                                                             " DATA_REFERENCIA," +
                                                             " LOGIN_ATUALIZACAO," +
                                                             " DATA_ATUALIZACAO)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT tb_restituicao_rescisao OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbRestituicaoRescisao);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setInt(4, pCodTipoRescisao);
            preparedStatement.setDate(5, pDataDesligamento);
            preparedStatement.setDate(6, pDataInicioFeriasIntegrais);
            preparedStatement.setDate(7, pDataFimFeriasIntegrais);
            preparedStatement.setDate(8, pDataInicioFeriasProporcionais);
            preparedStatement.setDate(9, pDataFimFeriasProporcionais);
            preparedStatement.setDate(10, pDataInicioContagemDecTer);
            preparedStatement.setFloat(11, pValorDecimoTerceiro);
            preparedStatement.setFloat(12, pValorIncidenciaDecimoTerceiro);
            preparedStatement.setFloat(13, pValorFGTSDecimoTerceiro);
            preparedStatement.setFloat(14, pValorFerias);
            preparedStatement.setFloat(15, pValorTerco);
            preparedStatement.setFloat(16, pValorIncidenciaFerias);
            preparedStatement.setFloat(17, pValorIncidenciaTerco);
            preparedStatement.setFloat(18, pValorFGTSFerias);
            preparedStatement.setFloat(19, pValorFGTSTerco);
            preparedStatement.setFloat(20, pValorFeriasProporcional);
            preparedStatement.setFloat(21, pValorTercoProporcional);
            preparedStatement.setFloat(22, pValorIncidenciaFeriasProporcional);
            preparedStatement.setFloat(23, pValorIncidenciaTercoProporcional);
            preparedStatement.setFloat(24, pValorFGTSFeriasProporcional);
            preparedStatement.setFloat(25, pValorFGTSTercoProporcional);
            preparedStatement.setFloat(26, pValorFGTSSalario);
            preparedStatement.setFloat(27, pValorFGTSRestante);
            preparedStatement.setString(28, pLoginAtualizacao);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar inserir dados na tabela de restituição de rescisão.");

        }

        return vCodTbRestituicaoRescisao;

    }

    public int InsertSaldoResidualRescisao (int pCodRestituicaoRescisao,
                                            float pValorDecimoTerceiro,
                                            float pValorIncidenciaDecimoTerceiro,
                                            float pValorFGTSDecimoTerceiro,
                                            float pValorFerias,
                                            float pValorTerco,
                                            float pValorIncidenciaFerias,
                                            float pValorIncidenciaTerco,
                                            float pValorFGTSFerias,
                                            float pValorFGTSTerco,
                                            float pValorFeriasProporcional,
                                            float pValorTercoProporcional,
                                            float pValorIncidenciaFeriasProporcional,
                                            float pValorIncidenciaTercoProporcional,
                                            float pValorFGTSFeriasProporcional,
                                            float pValorFGTSTercoProporcional,
                                            float pValorFGTSSalario,
                                            float pValorFGTSRestante,
                                            String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_SALDO_RESIDUAL_RESCISAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_SALDO_RESIDUAL_RESCISAO ON;" +
                    " INSERT INTO TB_SALDO_RESIDUAL_RESCISAO (cod," +
                    " cod_restituicao_rescisao," +
                    " valor_decimo_terceiro," +
                    " incid_submod_4_1_dec_terceiro," +
                    " incid_multa_fgts_dec_terceiro," +
                    " valor_ferias," +
                    " valor_terco," +
                    " incid_submod_4_1_ferias," +
                    " incid_submod_4_1_terco," +
                    " incid_multa_fgts_ferias," +
                    " incid_multa_fgts_terco," +
                    " valor_ferias_prop," +
                    " valor_terco_prop," +
                    " incid_submod_4_1_ferias_prop," +
                    " incid_submod_4_1_terco_prop," +
                    " incid_multa_fgts_ferias_prop," +
                    " incid_multa_fgts_terco_prop," +
                    " multa_fgts_salario," +
                    " multa_fgts_restante," +
                    " login_atualizacao," +
                    " data_atualizacao)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_SALDO_RESIDUAL_RESCISAO OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodRestituicaoRescisao);
            preparedStatement.setFloat(3, pValorDecimoTerceiro);
            preparedStatement.setFloat(4, pValorIncidenciaDecimoTerceiro);
            preparedStatement.setFloat(5, pValorFGTSDecimoTerceiro);
            preparedStatement.setFloat(6, pValorFerias);
            preparedStatement.setFloat(7, pValorTerco);
            preparedStatement.setFloat(8, pValorIncidenciaFerias);
            preparedStatement.setFloat(9, pValorIncidenciaTerco);
            preparedStatement.setFloat(10, pValorFGTSFerias);
            preparedStatement.setFloat(11, pValorFGTSTerco);
            preparedStatement.setFloat(12, pValorFeriasProporcional);
            preparedStatement.setFloat(13, pValorTercoProporcional);
            preparedStatement.setFloat(14, pValorIncidenciaFeriasProporcional);
            preparedStatement.setFloat(15, pValorIncidenciaTercoProporcional);
            preparedStatement.setFloat(16, pValorFGTSFeriasProporcional);
            preparedStatement.setFloat(17, pValorFGTSTercoProporcional);
            preparedStatement.setFloat(18, pValorFGTSSalario);
            preparedStatement.setFloat(19, pValorFGTSRestante);
            preparedStatement.setString(20, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar inserir dados na tabela de saldo residual de rescisão.");

        }

        return vCod;

    }

    public Integer InsertHistoricoRestituicaoDecimoTerceiro (int pCodTbRestituicaoDecTer,
                                                             int pCodTipoRestituicao,
                                                             int pParcela,
                                                             Date pDataInicioContagem,
                                                             float pValor,
                                                             float pIncidenciaSubmodulo41,
                                                             Date pDataReferencia,
                                                             String pAutorizado,
                                                             String pRestituido,
                                                             String pObservacao,
                                                             String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbHistRestituicaoDecTer = consulta.RetornaCodSequenceTbHistRestituicaoDecTer();

        try {

            String sql = "SET IDENTITY_INSERT TB_HIST_RESTITUICAO_DEC_TER ON;" +
                         " INSERT INTO TB_HIST_RESTITUICAO_DEC_TER (COD," +
                                                                  " COD_RESTITUICAO_DEC_TERCEIRO," +
                                                                  " COD_TIPO_RESTITUICAO," +
                                                                  " PARCELA," +
                                                                  " DATA_INICIO_CONTAGEM," +
                                                                  " VALOR," +
                                                                  " INCIDENCIA_SUBMODULO_4_1," +
                                                                  " DATA_REFERENCIA," +
                                                                  " AUTORIZADO," +
                                                                  " RESTITUIDO," +
                                                                  " OBSERVACAO," +
                                                                  " LOGIN_ATUALIZACAO," +
                                                                  " DATA_ATUALIZACAO)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                       " SET IDENTITY_INSERT TB_HIST_RESTITUICAO_DEC_TER OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbHistRestituicaoDecTer);
            preparedStatement.setInt(2, pCodTbRestituicaoDecTer);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setInt(4, pParcela);
            preparedStatement.setDate(5, pDataInicioContagem);
            preparedStatement.setFloat(6, pValor);
            preparedStatement.setFloat(7, pIncidenciaSubmodulo41);
            preparedStatement.setDate(8, pDataReferencia);
            preparedStatement.setString(9, pAutorizado);
            preparedStatement.setString(10, pRestituido);
            preparedStatement.setString(11, pObservacao);
            preparedStatement.setString(12, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir dados na tabela de histórico de restituição de décimo terceiro.");

        }

        return vCodTbHistRestituicaoDecTer;

    }

    public Integer InsertHistoricoRestituicaoRescisao (int pCodTbRestituicaoRescisao,
                                                       int pCodTipoRestituicao,
                                                       int pCodTipoRescisao,
                                                       Date pDataDesligamento,
                                                       Date pDataInicioFeriasIntegrais,
                                                       Date pDataFimFeriasIntegrais,
                                                       Date pDataInicioFeriasProporcionais,
                                                       Date pDataFimFeriasProporcionais,
                                                       Date pDataInicioContagemDecTer,
                                                       float pValorDecimoTerceiro,
                                                       float pIncidSubmod41DecTerceiro,
                                                       float pIncidMultaFGTSDecTeceriro,
                                                       float pValorFerias,
                                                       float pValorTerco,
                                                       float pIncidSubmod41Ferias,
                                                       float pIncidSubmod41Terco,
                                                       float pIncidMultaFGTSFerias,
                                                       float pIncidMultaFGTSTerco,
                                                       float pValorFeriasProporcional,
                                                       float pValorTercoProporcional,
                                                       float pIncidSubmod41FeriasProporcional,
                                                       float pIncidSubmod41TercoProporcional,
                                                       float pIncidMultaFGTSFeriasProporcional,
                                                       float pIncidMultaFGTSTercoProporcional,
                                                       float pMultaFGTSSalario,
                                                       Date pDataReferencia,
                                                       String pAutorizado,
                                                       String pRestituido,
                                                       String pObservacao,
                                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbHistRestituicaoRescisao = consulta.RetornaCodSequenceTbHistRestituicaoRescisao();

        try {

            String sql = "SET IDENTITY_INSERT TB_HIST_RESTITUICAO_RESCISAO ON;" +
                         " INSERT INTO TB_HIST_RESTITUICAO_RESCISAO (COD," +
                                                                   " COD_RESTITUICAO_RESCISAO," +
                                                                   " COD_TIPO_RESTITUICAO," +
                                                                   " COD_TIPO_RESCISAO," +
                                                                   " DATA_DESLIGAMENTO," +
                                                                   " DATA_INICIO_FERIAS," +
                                                                   " DATA_FIM_FERIAS," +
                                                                   " DATA_INICIO_FERIAS_PROP," +
                                                                   " DATA_FIM_FERIAS_PROP," +
                                                                   " DATA_INICIO_CONTAGEM_DEC_TER," +
                                                                   " VALOR_DECIMO_TERCEIRO," +
                                                                   " INCID_SUBMOD_4_1_DEC_TERCEIRO," +
                                                                   " INCID_MULTA_FGTS_DEC_TERCEIRO," +
                                                                   " VALOR_FERIAS," +
                                                                   " VALOR_TERCO," +
                                                                   " INCID_SUBMOD_4_1_FERIAS," +
                                                                   " INCID_SUBMOD_4_1_TERCO," +
                                                                   " INCID_MULTA_FGTS_FERIAS," +
                                                                   " INCID_MULTA_FGTS_TERCO," +
                                                                   " VALOR_FERIAS_PROP," +
                                                                   " VALOR_TERCO_PROP," +
                                                                   " INCID_SUBMOD_4_1_FERIAS_PROP," +
                                                                   " INCID_SUBMOD_4_1_TERCO_PROP," +
                                                                   " INCID_MULTA_FGTS_FERIAS_PROP," +
                                                                   " INCID_MULTA_FGTS_TERCO_PROP," +
                                                                   " MULTA_FGTS_SALARIO," +
                                                                   " DATA_REFERENCIA," +
                                                                   " AUTORIZADO," +
                                                                   " RESTITUIDO," +
                                                                   " OBSERVACAO," +
                                                                   " LOGIN_ATUALIZACAO," +
                                                                   " DATA_ATUALIZACAO)" +
                           " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                         " SET IDENTITY_INSERT TB_HIST_RESTITUICAO_RESCISAO OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbHistRestituicaoRescisao);
            preparedStatement.setInt(2, pCodTbRestituicaoRescisao);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setInt(4, pCodTipoRescisao);
            preparedStatement.setDate(5, pDataDesligamento);
            preparedStatement.setDate(6, pDataInicioFeriasIntegrais);
            preparedStatement.setDate(7, pDataFimFeriasIntegrais);
            preparedStatement.setDate(8, pDataInicioFeriasProporcionais);
            preparedStatement.setDate(9, pDataFimFeriasProporcionais);
            preparedStatement.setDate(10, pDataInicioContagemDecTer);
            preparedStatement.setFloat(11, pValorDecimoTerceiro);
            preparedStatement.setFloat(12, pIncidSubmod41DecTerceiro);
            preparedStatement.setFloat(13, pIncidMultaFGTSDecTeceriro);
            preparedStatement.setFloat(14, pValorFerias);
            preparedStatement.setFloat(15, pValorTerco);
            preparedStatement.setFloat(16, pIncidSubmod41Ferias);
            preparedStatement.setFloat(17, pIncidSubmod41Terco);
            preparedStatement.setFloat(18, pIncidMultaFGTSFerias);
            preparedStatement.setFloat(19, pIncidMultaFGTSTerco);
            preparedStatement.setFloat(20, pValorFeriasProporcional);
            preparedStatement.setFloat(21, pValorTercoProporcional);
            preparedStatement.setFloat(22, pIncidSubmod41FeriasProporcional);
            preparedStatement.setFloat(23, pIncidSubmod41TercoProporcional);
            preparedStatement.setFloat(24, pIncidMultaFGTSFeriasProporcional);
            preparedStatement.setFloat(25, pIncidMultaFGTSTercoProporcional);
            preparedStatement.setFloat(26, pMultaFGTSSalario);
            preparedStatement.setDate(27, pDataReferencia);
            preparedStatement.setString(28, pAutorizado);
            preparedStatement.setString(29,pRestituido);
            preparedStatement.setString(30, pObservacao);
            preparedStatement.setString(31, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir dados na tabela de histórico de restituição de rescisão.");

        }

        return vCodTbHistRestituicaoRescisao;

    }


    public Integer InsertHistoricoRestituicaoFerias (int pCodTbRestituicaoFerias,
                                                     int pCodTipoRestituicao,
                                                     Date pInicioPeriodoAquisitivo,
                                                     Date pFimPeriodoAquisitivo,
                                                     Date pInicioFerias,
                                                     Date pFimFerias,
                                                     float pTotalFerias,
                                                     float pTotalTercoConstitucional,
                                                     float pTotalIncidenciaFerias,
                                                     float pTotalIncidenciaTerco,
                                                     int pParcela,
                                                     Date pDataReferencia,
                                                     int pDiasVendidos,
                                                     String pAutorizado,
                                                     String pRestituido,
                                                     String pObservacao,
                                                     String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodTbHistRestituicaoFerias = consulta.RetornaCodSequenceTbHistRestituicaoFerias();

        try {

            String sql = "SET IDENTITY_INSERT TB_HIST_RESTITUICAO_FERIAS ON;" +
                    " INSERT INTO TB_HIST_RESTITUICAO_FERIAS (COD," +
                    " COD_RESTITUICAO_FERIAS," +
                    " COD_TIPO_RESTITUICAO," +
                    " DATA_INICIO_PERIODO_AQUISITIVO," +
                    " DATA_FIM_PERIODO_AQUISITIVO," +
                    " DATA_INICIO_USUFRUTO," +
                    " DATA_FIM_USUFRUTO," +
                    " VALOR_FERIAS," +
                    " VALOR_TERCO_CONSTITUCIONAL," +
                    " INCID_SUBMOD_4_1_FERIAS," +
                    " INCID_SUBMOD_4_1_TERCO," +
                    " PARCELA," +
                    " DIAS_VENDIDOS," +
                    " DATA_REFERENCIA," +
                    " AUTORIZADO," +
                    " RESTITUIDO," +
                    " OBSERVACAO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_HIST_RESTITUICAO_FERIAS OFF;";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vCodTbHistRestituicaoFerias);
            preparedStatement.setInt(2, pCodTbRestituicaoFerias);
            preparedStatement.setInt(3, pCodTipoRestituicao);
            preparedStatement.setDate(4, pInicioPeriodoAquisitivo);
            preparedStatement.setDate(5, pFimPeriodoAquisitivo);
            preparedStatement.setDate(6, pInicioFerias);
            preparedStatement.setDate(7, pFimFerias);
            preparedStatement.setFloat(8, pTotalFerias);
            preparedStatement.setFloat(9, pTotalTercoConstitucional);
            preparedStatement.setFloat(10, pTotalIncidenciaFerias);
            preparedStatement.setFloat(11, pTotalIncidenciaTerco);
            preparedStatement.setInt(12, pParcela);
            preparedStatement.setInt(13, pDiasVendidos);
            preparedStatement.setDate(14, pDataReferencia);
            preparedStatement.setString(15, pAutorizado);
            preparedStatement.setString(16, pRestituido);
            preparedStatement.setString(17, pObservacao);
            preparedStatement.setString(18, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível inserir dados na tabela de histórico de restituição de férias.");

        }

        return vCodTbHistRestituicaoFerias;

    }


    public int InsertRubrica (String pNome,
                              String pSigla,
                              String pDescricao,
                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCodRubrica = consulta.RetornaCodSequenceTable("TB_RUBRICA");

        try {

            String sql = "SET IDENTITY_INSERT TB_RUBRICA ON;" +
                        " INSERT INTO TB_RUBRICA (COD," +
                                                " NOME," +
                                                " SIGLA," +
                                                " DESCRICAO," +
                                                " LOGIN_ATUALIZACAO," +
                                                " DATA_ATUALIZACAO)" +
                          " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_RUBRICA OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCodRubrica);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pSigla);
            preparedStatement.setString(4, pDescricao);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a nova rubrica.");

        }

        return vCodRubrica;

    }

    public int InsertContrato (String pNomeEmpresa,
                               String pCnpj,
                               String pNumeroContrato,
                               String pNumeroProcessoSTJ,
                               String pSeAtivo,
                               String pObjeto,
                               String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_CONTRATO ON;" +
                            " INSERT INTO TB_CONTRATO (COD," +
                                                     " NOME_EMPRESA," +
                                                     " CNPJ," +
                                                     " NUMERO_CONTRATO," +
                                                     " NUMERO_PROCESSO_STJ," +
                                                     " SE_ATIVO," +
                                                     " OBJETO," +
                                                     " LOGIN_ATUALIZACAO," +
                                                     " DATA_ATUALIZACAO)" +
                                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNomeEmpresa);
            preparedStatement.setString(3, pCnpj);
            preparedStatement.setString(4, pNumeroContrato);
            preparedStatement.setString(5, pNumeroProcessoSTJ);
            preparedStatement.setString(6, pSeAtivo);
            preparedStatement.setString(7, pObjeto);
            preparedStatement.setString(8, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo contrato.");

        }

        return vCod;

    }

    public int InsertConvencaoColetiva (String pNome,
                                        String pSigla,
                                        Date pDataBase,
                                        String pDescricao,
                                        String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_CONVENCAO_COLETIVA");

        try {

            String sql = "SET IDENTITY_INSERT TB_CONVENCAO_COLETIVA ON;" +
                            " INSERT INTO TB_CONVENCAO_COLETIVA (COD," +
                                                               " NOME," +
                                                               " SIGLA," +
                                                               " DATA_BASE," +
                                                               " DESCRICAO," +
                                                               " LOGIN_ATUALIZACAO," +
                                                               " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_CONVENCAO_COLETIVA OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pSigla);
            preparedStatement.setDate(4, pDataBase);
            preparedStatement.setString(5, pDescricao);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a nova convenção.");

        }

        return vCod;

    }

    public int InsertEventoContratual (int pCodContrato,
                                       int pCodTipoEvento,
                                       String pProrrogacao,
                                       String pAssunto,
                                       Date pDataInicioVigencia,
                                       Date pDataFimVigencia,
                                       Date pDataAssinatura,
                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_EVENTO_CONTRATUAL");

        try {

            String sql = "SET IDENTITY_INSERT TB_EVENTO_CONTRATUAL ON;" +
                            " INSERT INTO TB_EVENTO_CONTRATUAL (COD," +
                                                              " COD_CONTRATO," +
                                                              " COD_TIPO_EVENTO," +
                                                              " PRORROGACAO," +
                                                              " ASSUNTO," +
                                                              " DATA_INICIO_VIGENCIA," +
                                                              " DATA_FIM_VIGENCIA," +
                                                              " DATA_ASSINATURA," +
                                                              " LOGIN_ATUALIZACAO," +
                                                              " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                         " SET IDENTITY_INSERT TB_EVENTO_CONTRATUAL OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodTipoEvento);
            preparedStatement.setString(4, pProrrogacao);
            preparedStatement.setString(5, pAssunto);
            preparedStatement.setDate(6, pDataInicioVigencia);
            preparedStatement.setDate(7, pDataFimVigencia);
            preparedStatement.setDate(8, pDataAssinatura);
            preparedStatement.setString(9, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo evento contratual.");

        }

        return vCod;

    }

    public int InsertFuncao (String pNome,
                             String pDescricao,
                             String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_FUNCAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_FUNCAO ON;" +
                            " INSERT INTO TB_FUNCAO (COD," +
                                                   " NOME," +
                                                   " DESCRICAO," +
                                                   " LOGIN_ATUALIZACAO," +
                                                   " DATA_ATUALIZACAO)" +
                              " VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_FUNCAO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pDescricao);
            preparedStatement.setString(4, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a nova função.");

        }

        return vCod;

    }

    public int InsertFuncaoContrato (int pCodContrato,
                                     int pCodFuncao,
                                     String pDescricao,
                                     String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_FUNCAO_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_FUNCAO_CONTRATO ON;" +
                            " INSERT INTO TB_FUNCAO_CONTRATO (COD," +
                                                            " COD_CONTRATO," +
                                                            " COD_FUNCAO," +
                                                            " DESCRICAO," +
                                                            " LOGIN_ATUALIZACAO," +
                                                            " DATA_ATUALIZACAO)" +
                              " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_FUNCAO_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodFuncao);
            preparedStatement.setString(4, pDescricao);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a nova função do contrato.");

        }

        return vCod;

    }

    public int InsertFuncaoTerceirizado (int pCodTerceirizadoContrato,
                                         int pCodFuncaoContrato,
                                         Date pDataInicio,
                                         Date pDataFim,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_FUNCAO_TERCEIRIZADO");

        try {

            String sql = "SET IDENTITY_INSERT TB_FUNCAO_TERCEIRIZADO ON;" +
                         " INSERT INTO TB_FUNCAO_TERCEIRIZADO (COD," +
                                                             " COD_TERCEIRIZADO_CONTRATO," +
                                                             " COD_FUNCAO_CONTRATO," +
                                                             " DATA_INICIO," +
                                                             " DATA_FIM," +
                                                             " LOGIN_ATUALIZACAO," +
                                                             " DATA_ATUALIZACAO)" +
                                 " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_FUNCAO_TERCEIRIZADO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pCodFuncaoContrato);
            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atribuir a função ao terceirizado.");

        }

        return vCod;

    }

    public int InsertHistoricoGestaoContrato (int pCodContrato,
                                              int pCodUsuario,
                                              int pCodPerfilGestao,
                                              Date pDataInicio,
                                              Date pDataFim,
                                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_HISTORICO_GESTAO_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_HISTORICO_GESTAO_CONTRATO ON;" +
                            " INSERT INTO TB_HISTORICO_GESTAO_CONTRATO (COD," +
                                                                      " COD_CONTRATO," +
                                                                      " COD_USUARIO," +
                                                                      " COD_PERFIL_GESTAO," +
                                                                      " DATA_INICIO," +
                                                                      " DATA_FIM," +
                                                                      " LOGIN_ATUALIZACAO," +
                                                                      " DATA_ATUALIZACAO)" +
                              " VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_HISTORICO_GESTAO_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodUsuario);
            preparedStatement.setInt(4, pCodPerfilGestao);
            preparedStatement.setDate(5, pDataInicio);
            preparedStatement.setDate(6, pDataFim);
            preparedStatement.setString(7, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível registrar a alteração no histórico de gestão do contrato.");

        }

        return vCod;

    }

    public int InsertPercentualContrato (int pCodContrato,
                                         int pCodRubrica,
                                         float pPercentual,
                                         Date pDataInicio,
                                         Date pDataFim,
                                         Date pDataAditamento,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_PERCENTUAL_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_PERCENTUAL_CONTRATO ON;" +
                            " INSERT INTO TB_PERCENTUAL_CONTRATO (COD," +
                                                                " COD_CONTRATO," +
                                                                " COD_RUBRICA," +
                                                                " PERCENTUAL," +
                                                                " DATA_INICIO," +
                                                                " DATA_FIM," +
                                                                " DATA_ADITAMENTO," +
                                                                " LOGIN_ATUALIZACAO," +
                                                                " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_PERCENTUAL_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodRubrica);
            preparedStatement.setFloat(4, pPercentual);
            preparedStatement.setDate(5, pDataInicio);
            preparedStatement.setDate(6, pDataFim);
            preparedStatement.setDate(7, pDataAditamento);
            preparedStatement.setString(8, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo percentual.");

        }

        return vCod;

    }

    public int InsertPercentualDinamico (float pPercentual, String pLoginAtualizacao) {
        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        try {
            String sql = " INSERT INTO TB_PERCENTUAL_DINAMICO (PERCENTUAL, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO)" +
                            "VALUES (?, ?, CURRENT_TIMESTAMP);";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setFloat(1, pPercentual);
            preparedStatement.setString(2, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo percentual.");
        }
        return 1;
    }

    public int InsertPercentualEstatico (int pCodRubrica,
                                         float pPercentual,
                                         Date pDataInicio,
                                         Date pDataFim,
                                         Date pDataAditamento,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_PERCENTUAL_ESTATICO");

        try {

            String sql = "SET IDENTITY_INSERT TB_PERCENTUAL_ESTATICO ON;" +
                            " INSERT INTO TB_PERCENTUAL_ESTATICO (COD," +
                                                                " COD_RUBRICA," +
                                                                " PERCENTUAL," +
                                                                " DATA_INICIO," +
                                                                " DATA_FIM," +
                                                                " DATA_ADITAMENTO," +
                                                                " LOGIN_ATUALIZACAO," +
                                                                " DATA_ATUALIZACAO)" +
                              " VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_PERCENTUAL_ESTATICO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodRubrica);
            preparedStatement.setFloat(3, pPercentual);
            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setDate(6, pDataAditamento);
            preparedStatement.setString(7, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo percentual.");

        }

        return vCod;

    }

    public int InsertPerfilGestao (String pNome,
                                   String pSigla,
                                   String pDescricao,
                                   String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_PERFIL_GESTAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_PERFIL_GESTAO ON;" +
                            " INSERT INTO TB_PERFIL_GESTAO (COD," +
                                                          " NOME," +
                                                          " SIGLA," +
                                                          " DESCRICAO," +
                                                          " LOGIN_ATUALIZACAO," +
                                                          " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_PERFIL_GESTAO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pSigla);
            preparedStatement.setString(4, pDescricao);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo perfil de gestão.");

        }

        return vCod;

    }

    public int InsertPerfilUsuario (String pNome,
                                    String pSigla,
                                    String pDescricao,
                                    String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_PERFIL_USUARIO");

        try {

            String sql = "SET IDENTITY_INSERT TB_PERFIL_USUARIO ON;" +
                                " INSERT INTO TB_PERFIL_USUARIO (COD," +
                                                               " NOME," +
                                                               " SIGLA," +
                                                               " DESCRICAO," +
                                                               " LOGIN_ATUALIZACAO," +
                                                               " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_PERFIL_USUARIO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pSigla);
            preparedStatement.setString(4, pDescricao);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo perfil de usuário.");

        }

        return vCod;

    }

    public int InsertRemuneracaoFunCon (int pCodFuncaoContrato,
                                        Integer pCodConvencaoColetiva,
                                        Date pDataInicio,
                                        Date pDataFim,
                                        Date pDataAditamento,
                                        float pRemuneracao,
                                        float pAdicionais,
                                        float pTrienios,
                                        String pLoginAtualizacao) throws NullPointerException {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_REMUNERACAO_FUN_CON");

        try {

            String sql = "SET IDENTITY_INSERT TB_REMUNERACAO_FUN_CON ON;" +
                            " INSERT INTO TB_REMUNERACAO_FUN_CON (COD," +
                                                                " COD_FUNCAO_CONTRATO," +
                                                                " COD_CONVENCAO_COLETIVA," +
                                                                " DATA_INICIO," +
                                                                " DATA_FIM," +
                                                                " DATA_ADITAMENTO," +
                                                                " REMUNERACAO," +
                                                                " ADICIONAIS," +
                                                                " TRIENIOS," +
                                                                " LOGIN_ATUALIZACAO," +
                                                                " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_REMUNERACAO_FUN_CON OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodFuncaoContrato);

            if (pCodConvencaoColetiva == null || pCodConvencaoColetiva == 0) {

                preparedStatement.setNull(3, java.sql.Types.INTEGER);

            }

            else {

                preparedStatement.setInt(3, pCodConvencaoColetiva);

            }

            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setDate(6, pDataAditamento);
            preparedStatement.setFloat(7, pRemuneracao);
            preparedStatement.setFloat(8, pAdicionais);
            preparedStatement.setFloat(9, pTrienios);
            preparedStatement.setString(10, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a nova remuneração de função.");

        }

        return vCod;

    }

    public int InsertRetroPercentualEstatico (int pCodContrato,
                                              int pCodPercentualEstatico,
                                              Date pInicio,
                                              Date pFim,
                                              Date pDataCobranca,
                                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_RETRO_PERCENTUAL_ESTATICO");

        try {

            String sql = "SET IDENTITY_INSERT TB_RETRO_PERCENTUAL_ESTATICO ON;" +
                            " INSERT INTO TB_RETRO_PERCENTUAL_ESTATICO (COD," +
                                                                      " COD_CONTRATO," +
                                                                      " COD_PERCENTUAL_ESTATICO," +
                                                                      " INICIO," +
                                                                      " FIM," +
                                                                      " DATA_COBRANCA," +
                                                                      " LOGIN_ATUALIZACAO," +
                                                                      " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_RETRO_PERCENTUAL_ESTATICO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodPercentualEstatico);
            preparedStatement.setDate(4, pInicio);
            preparedStatement.setDate(5, pFim);
            preparedStatement.setDate(6, pDataCobranca);
            preparedStatement.setString(7, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a retroatividade do percentual estático.");

        }

        return vCod;

    }

    public int InsertRetroatividadePercentual (int pCodPercentualContrato,
                                               Date pInicio,
                                               Date pFim,
                                               Date pDataCobranca,
                                               String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_RETROATIVIDADE_PERCENTUAL");

        try {

            String sql = "SET IDENTITY_INSERT TB_RETROATIVIDADE_PERCENTUAL ON;" +
                            " INSERT INTO TB_RETROATIVIDADE_PERCENTUAL (COD," +
                                                                      " COD_PERCENTUAL_CONTRATO," +
                                                                      " INICIO," +
                                                                      " FIM," +
                                                                      " DATA_COBRANCA," +
                                                                      " LOGIN_ATUALIZACAO," +
                                                                      " DATA_ATUALIZACAO)" +
                                " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                         " SET IDENTITY_INSERT TB_RETROATIVIDADE_PERCENTUAL OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodPercentualContrato);
            preparedStatement.setDate(3, pInicio);
            preparedStatement.setDate(4, pFim);
            preparedStatement.setDate(5, pDataCobranca);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a retroatividade do percentual do contrato.");

        }

        return vCod;

    }

    public int InsertRetroatividadeRemuneracao (int pCodRemFuncaoContrato,
                                                Date pInicio,
                                                Date pFim,
                                                Date pDataCobranca,
                                                String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_RETROATIVIDADE_REMUNERACAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_RETROATIVIDADE_REMUNERACAO ON;" +
                            " INSERT INTO TB_RETROATIVIDADE_REMUNERACAO (COD," +
                                                                       " COD_REM_FUNCAO_CONTRATO," +
                                                                       " INICIO," +
                                                                       " FIM," +
                                                                       " DATA_COBRANCA," +
                                                                       " LOGIN_ATUALIZACAO," +
                                                                       " DATA_ATUALIZACAO)" +
                                 " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_RETROATIVIDADE_REMUNERACAO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodRemFuncaoContrato);
            preparedStatement.setDate(3, pInicio);
            preparedStatement.setDate(4, pFim);
            preparedStatement.setDate(5, pDataCobranca);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a retroatividade da remuneração.");

        }

        return vCod;

    }

    public int InsertRetroatividadeTotalMensal (int pCodTotalMensalAReter,
                                                float pFerias,
                                                float pTercoConstitucional,
                                                float pDecimoTerceiro,
                                                float pIncidenciaSubmodulo41,
                                                float pMultaFgts,
                                                float pTotal,
                                                String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_RETROATIVIDADE_TOTAL_MENSAL");

        try {

            String sql = "SET IDENTITY_INSERT TB_RETROATIVIDADE_TOTAL_MENSAL ON;" +
                              " INSERT INTO TB_RETROATIVIDADE_TOTAL_MENSAL (COD," +
                                                                          " COD_TOTAL_MENSAL_A_RETER," +
                                                                          " FERIAS," +
                                                                          " TERCO_CONSTITUCIONAL," +
                                                                          " DECIMO_TERCEIRO," +
                                                                          " INCIDENCIA_SUBMODULO_4_1," +
                                                                          " MULTA_FGTS," +
                                                                          " TOTAL," +
                                                                          " LOGIN_ATUALIZACAO," +
                                                                          " DATA_ATUALIZACAO)" +
                                 " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_RETROATIVIDADE_TOTAL_MENSAL OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodTotalMensalAReter);
            preparedStatement.setFloat(3, pFerias);
            preparedStatement.setFloat(4, pTercoConstitucional);
            preparedStatement.setFloat(5, pDecimoTerceiro);
            preparedStatement.setFloat(6, pIncidenciaSubmodulo41);
            preparedStatement.setFloat(7, pMultaFgts);
            preparedStatement.setFloat(8, pTotal);
            preparedStatement.setString(9, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir a retroatividade de total mensal respectiva à retenção.");

        }

        return vCod;

    }

    public int InsertTerceirizado (String pNome,
                                   String pCpf,
                                   String pAtivo,
                                   String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TERCEIRIZADO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TERCEIRIZADO ON;" +
                    " INSERT INTO TB_TERCEIRIZADO (COD," +
                    " NOME," +
                    " CPF," +
                    " ATIVO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TERCEIRIZADO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pCpf);
            preparedStatement.setString(4, pAtivo);
            preparedStatement.setString(5, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo terceirizado.");

        }

        return vCod;

    }

    public int InsertTerceirizadoContrato (int pCodContrato,
                                           int pCodTerceirizado,
                                           Date pDataDisponibilizacao,
                                           Date pDataDesligamento,
                                           String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TERCEIRIZADO_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TERCEIRIZADO_CONTRATO ON;" +
                    " INSERT INTO TB_TERCEIRIZADO_CONTRATO (COD," +
                    " COD_CONTRATO," +
                    " COD_TERCEIRIZADO," +
                    " DATA_DISPONIBILIZACAO," +
                    " DATA_DESLIGAMENTO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TERCEIRIZADO_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodContrato);
            preparedStatement.setInt(3, pCodTerceirizado);
            preparedStatement.setDate(4, pDataDisponibilizacao);
            preparedStatement.setDate(5, pDataDesligamento);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o terceirizado no contrato.");

        }

        return vCod;

    }

    public int InsertTipoEventoContratual (String pTipo,
                                           String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TIPO_EVENTO_CONTRATUAL");

        try {

            String sql = "SET IDENTITY_INSERT TB_TIPO_EVENTO_CONTRATUAL ON;" +
                    " INSERT INTO TB_TIPO_EVENTO_CONTRATUAL (COD," +
                    " TIPO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TIPO_EVENTO_CONTRATUAL OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pTipo);
            preparedStatement.setString(3, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o tipo de evento contratual.");

        }

        return vCod;

    }

    public int InsertTipoRescisao (String pTipoRescisao,
                                   String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TIPO_RESCISAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TIPO_RESCISAO ON;" +
                    " INSERT INTO TB_TIPO_RESCISAO (COD," +
                    " TIPO_RESCISAO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TIPO_RESCISAO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pTipoRescisao);
            preparedStatement.setString(3, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o tipo de rescisão.");

        }

        return vCod;

    }

    public int InsertTipoRestituicao (String pNome,
                                      String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TIPO_RESTITUICAO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TIPO_RESTITUICAO ON;" +
                    " INSERT INTO TB_TIPO_RESTITUICAO (COD," +
                    " NOME," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TIPO_RESTITUICAO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o tipo de restituição.");

        }

        return vCod;

    }

    public int InsertTrienioTercContrato (int pCodTerceirizadoContrato,
                                          int pNumeroDeTrienios,
                                          Date pDataInicio,
                                          Date pDataFim,
                                          String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TRIENIO_TERC_CONTRATO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TRIENIO_TERC_CONTRATO ON;" +
                            " INSERT INTO TB_TRIENIO_TERC_CONTRATO (COD," +
                                                                  " COD_TERCEIRIZADO_CONTRATO," +
                                                                  " NUMERO_DE_TRIENIOS," +
                                                                  " DATA_INICIO," +
                                                                  " DATA_FIM," +
                                                                  " LOGIN_ATUALIZACAO," +
                                                                  " DATA_ATUALIZACAO)" +
                            " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_TRIENIO_TERC_CONTRATO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);
            preparedStatement.setInt(3, pNumeroDeTrienios);
            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o triênio para o terceirizado.");

        }

        return vCod;

    }

    public int InsertUsuario (int pCodPerfil,
                              String pNome,
                              String pLogin,
                              String pPassword,
                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_USUARIO");

        try {

            String sql = "SET IDENTITY_INSERT TB_USUARIO ON;" +
                            " INSERT INTO TB_USUARIO (COD," +
                                                    " COD_PERFIL," +
                                                    " NOME," +
                                                    " LOGIN," +
                                                    " PASSWORD," +
                                                    " LOGIN_ATUALIZACAO," +
                                                    " DATA_ATUALIZACAO)" +
                                " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                        " SET IDENTITY_INSERT TB_USUARIO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setInt(2, pCodPerfil);
            preparedStatement.setString(3, pNome);
            preparedStatement.setString(4, pLogin);
            preparedStatement.setString(5, pPassword);
            preparedStatement.setString(6, pLoginAtualizacao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo usuário.");

        }

        return vCod;

    }

}
