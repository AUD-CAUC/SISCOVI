package br.jus.stj.siscovi.dao.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateTSQL {

    private Connection connection;

    public UpdateTSQL(Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que atualiza um registro da tabela de restituição de férias.
     *
     * @param pCodRestituicaoFerias;
     * @param pCodTipoRestituicao;
     * @param pInicioPeriodoAquisitivo;
     * @param pFimPeriodoAquisitivo;
     * @param pInicioFerias;
     * @param pFimFerias;
     * @param pDiasVendidos;
     * @param pTotalFerias;
     * @param pTotalTercoConstitucional;
     * @param pTotalIncidenciaFerias;
     * @param pTotalIncidenciaTerco;
     * @param pParcela;
     * @param pAutorizado;
     * @param pRestituido;
     * @param pObservacao;
     * @param pLoginAtualizacao;
     */

    public void UpdateRestituicaoFerias (int pCodRestituicaoFerias,
                                         int pCodTipoRestituicao,
                                         Date pInicioPeriodoAquisitivo,
                                         Date pFimPeriodoAquisitivo,
                                         Date pInicioFerias,
                                         Date pFimFerias,
                                         int pDiasVendidos,
                                         float pTotalFerias,
                                         float pTotalTercoConstitucional,
                                         float pTotalIncidenciaFerias,
                                         float pTotalIncidenciaTerco,
                                         int pParcela,
                                         String pAutorizado,
                                         String pRestituido,
                                         String pObservacao,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;

        if (pRestituido.length() > 1 || pAutorizado.length() > 1) {

            throw new NullPointerException("Argumento em autorizado ou restituído está fora do padrão esperado (S ou N).");

        }

        String vSQLQuerry = "UPDATE tb_restituicao_ferias" +
                " SET COD_TIPO_RESTITUICAO = ?," +
                " DATA_INICIO_PERIODO_AQUISITIVO = ?," +
                " DATA_FIM_PERIODO_AQUISITIVO = ?," +
                " DATA_INICIO_USUFRUTO = ?," +
                " DATA_FIM_USUFRUTO = ?," +
                " DIAS_VENDIDOS = ?," +
                " VALOR_FERIAS = ?," +
                " VALOR_TERCO_CONSTITUCIONAL = ?," +
                " INCID_SUBMOD_4_1_FERIAS = ?," +
                " INCID_SUBMOD_4_1_TERCO = ?," +
                " PARCELA = ?," +
                " DATA_REFERENCIA = GETDATE()," +
                " AUTORIZADO = ?," +
                " RESTITUIDO = ?," +
                " OBSERVACAO = ?," +
                " LOGIN_ATUALIZACAO = ?," +
                " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                " WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(vSQLQuerry);
            preparedStatement.setInt(1, pCodTipoRestituicao);
            preparedStatement.setDate(2, pInicioPeriodoAquisitivo);
            preparedStatement.setDate(3, pFimPeriodoAquisitivo);
            preparedStatement.setDate(4, pInicioFerias);
            preparedStatement.setDate(5, pFimFerias);
            preparedStatement.setInt(6, pDiasVendidos);
            preparedStatement.setFloat(7, pTotalFerias);
            preparedStatement.setFloat(8, pTotalTercoConstitucional);
            preparedStatement.setFloat(9, pTotalIncidenciaFerias);
            preparedStatement.setFloat(10, pTotalIncidenciaTerco);
            preparedStatement.setInt(11, pParcela);
            preparedStatement.setString(12, pAutorizado);
            preparedStatement.setString(13, pRestituido);
            preparedStatement.setString(14, pObservacao);
            preparedStatement.setString(15, pLoginAtualizacao);
            preparedStatement.setInt(16, pCodRestituicaoFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Erro na execução da atualização dos dados da restiuição de férias.");

        }

    }

    /**
     * Método que atualiza um registro da tabela de restituição de décimo terceiro.
     *
     * @param pCodRestituicaoDecimoTerceiro;
     * @param pTipoRestituicao;
     * @param pParcela;
     * @param pInicioContagem;
     * @param pValorDecimoTerceiro;
     * @param pValorIncidencia;
     * @param pAutorizado;
     * @param pRestituido;
     * @param pObservacao;
     * @param pLoginAtualizacao;
     */

    public void UpdateRestituicaoDecimoTerceiro (int pCodRestituicaoDecimoTerceiro,
                                                 int pTipoRestituicao,
                                                 int pParcela,
                                                 Date pInicioContagem,
                                                 float pValorDecimoTerceiro,
                                                 float pValorIncidencia,
                                                 String pAutorizado,
                                                 String pRestituido,
                                                 String pObservacao,
                                                 String pLoginAtualizacao) {

        PreparedStatement preparedStatement;

        if (pRestituido.length() > 1 || pAutorizado.length() > 1) {

            throw new NullPointerException("Argumento em autorizado ou restituído está fora do padrão esperado (S ou N).");

        }

        String vSQLQuerry = "UPDATE tb_restituicao_decimo_terceiro" +
                " SET COD_TIPO_RESTITUICAO = ?," +
                " PARCELA = ?," +
                " DATA_INICIO_CONTAGEM = ?," +
                " VALOR = ?," +
                " INCIDENCIA_SUBMODULO_4_1 = ?," +
                " DATA_REFERENCIA = GETDATE()," +
                " AUTORIZADO = ?," +
                " RESTITUIDO = ?," +
                " OBSERVACAO = ?," +
                " LOGIN_ATUALIZACAO = ?," +
                " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                " WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(vSQLQuerry);
            preparedStatement.setInt(1, pTipoRestituicao);
            preparedStatement.setInt(2, pParcela);
            preparedStatement.setDate(3, pInicioContagem);
            preparedStatement.setFloat(4, pValorDecimoTerceiro);
            preparedStatement.setFloat(5, pValorIncidencia);
            preparedStatement.setString(6, pAutorizado);
            preparedStatement.setString(7, pRestituido);
            preparedStatement.setString(8, pObservacao);
            preparedStatement.setString(9, pLoginAtualizacao);
            preparedStatement.setInt(10, pCodRestituicaoDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro na execução da atualização dos dados da restiuição de décimo tercero.");

        }

    }

    /**
     * Método que atualiza um registro da tabela de restituição de férias.
     *
     * @param pCodRestituicaoRescisao;
     * @param pCodTipoRestituicao;
     * @param pCodTipoRescisao;
     * @param pDataDesligamento;
     * @param pDataInicioFerias;
     * @param pValorDecimoTerceiro;
     * @param pValorIncidenciaDecimoTerceiro;
     * @param pValorFGTSDecimoTerceiro;
     * @param pValorFerias;
     * @param pValorTerco;
     * @param pValorIncidenciaFerias;
     * @param pValorIncidenciaTerco;
     * @param pValorFGTSFerias;
     * @param pValorFGTSTerco;
     * @param pValorFGTSSalario;
     * @param pAutorizado;
     * @param pRestituido;
     * @param pObservacao;
     * @param pLoginAtualizacao;
     */

    public void UpdateRestituicaoRescisao (int pCodRestituicaoRescisao,
                                           int pCodTipoRestituicao,
                                           int pCodTipoRescisao,
                                           Date pDataDesligamento,
                                           Date pDataInicioFerias,
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
                                           String pAutorizado,
                                           String pRestituido,
                                           String pObservacao,
                                           String pLoginAtualizacao) {

        PreparedStatement preparedStatement;

        String vSQLQuery = "UPDATE tb_restituicao_rescisao" +
                " SET COD_TIPO_RESTITUICAO = ?," +
                " COD_TIPO_RESCISAO = ?," +
                " DATA_DESLIGAMENTO = ?," +
                " DATA_INICIO_FERIAS = ?," +
                " VALOR_DECIMO_TERCEIRO = ?," +
                " INCID_SUBMOD_4_1_DEC_TERCEIRO = ?," +
                " INCID_MULTA_FGTS_DEC_TERCEIRO = ?," +
                " VALOR_FERIAS = ?," +
                " VALOR_TERCO = ?," +
                " INCID_SUBMOD_4_1_FERIAS = ?," +
                " INCID_SUBMOD_4_1_TERCO = ?," +
                " INCID_MULTA_FGTS_FERIAS = ?," +
                " INCID_MULTA_FGTS_TERCO = ?," +
                " VALOR_FERIAS_PROP = ?," +
                " VALOR_TERCO_PROP = ?," +
                " INCID_SUBMOD_4_1_FERIAS_PROP = ?," +
                " INCID_SUBMOD_4_1_TERCO_PROP = ?," +
                " INCID_MULTA_FGTS_FERIAS_PROP = ?," +
                " INCID_MULTA_FGTS_TERCO_PROP = ?," +
                " MULTA_FGTS_SALARIO = ?," +
                " DATA_REFERENCIA = GETDATE()," +
                " AUTORIZADO = ?," +
                " RESTITUIDO = ?," +
                " OBSERVACAO = ?," +
                " LOGIN_ATUALIZACAO = ?," +
                " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                " WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(vSQLQuery);
            preparedStatement.setInt(1, pCodTipoRestituicao);
            preparedStatement.setInt(2, pCodTipoRescisao);
            preparedStatement.setDate(3, pDataDesligamento);
            preparedStatement.setDate(4, pDataInicioFerias);
            preparedStatement.setFloat(5, pValorDecimoTerceiro);
            preparedStatement.setFloat(6, pValorIncidenciaDecimoTerceiro);
            preparedStatement.setFloat(7, pValorFGTSDecimoTerceiro);
            preparedStatement.setFloat(8, pValorFerias);
            preparedStatement.setFloat(9, pValorTerco);
            preparedStatement.setFloat(10, pValorIncidenciaFerias);
            preparedStatement.setFloat(11, pValorIncidenciaTerco);
            preparedStatement.setFloat(12, pValorFGTSFerias);
            preparedStatement.setFloat(13, pValorFGTSTerco);
            preparedStatement.setFloat(14, pValorFeriasProporcional);
            preparedStatement.setFloat(15, pValorTercoProporcional);
            preparedStatement.setFloat(16, pValorIncidenciaFeriasProporcional);
            preparedStatement.setFloat(17, pValorIncidenciaTercoProporcional);
            preparedStatement.setFloat(18, pValorFGTSFeriasProporcional);
            preparedStatement.setFloat(19, pValorFGTSTercoProporcional);
            preparedStatement.setFloat(20, pValorFGTSSalario);
            preparedStatement.setString(21,String.valueOf(pAutorizado));
            preparedStatement.setString(22, String.valueOf(pRestituido));
            preparedStatement.setString(23, pObservacao);
            preparedStatement.setString(24, pLoginAtualizacao);
            preparedStatement.setInt(25, pCodRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro na execução da atualização dos dados da restiuição de rescisão.");

        }

    }

    public void UpdateRubrica (int pCodRubrica,
                               String pNome,
                               String pSigla,
                               String pDescricao,
                               String pLoginAtualizacao) {

        PreparedStatement preparedStatement;

        try {

            String sql = "UPDATE TB_RUBRICA" +
                    " SET NOME = ?," +
                    " SIGLA = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?" +
                    " WHERE COD = ?;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pSigla);
            preparedStatement.setString(3, pDescricao);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodRubrica);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a rubrica.");

        }

    }

    public Integer UpdateHistoricoRestituicaoRescisao (int pCodHistoricoRestituicaoRescisao,
                                                       int pCodTbRestituicaoRescisao,
                                                       int pCodTipoRestituicao,
                                                       int pCodTipoRescisao,
                                                       Date pDataDesligamento,
                                                       Date pDataInicioFerias,
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
        int vRetorno = -1;


        try {

            String sql = "UPDATE TB_HIST_RESTITUICAO_RESCISAO" +
                    " SET COD_RESTITUICAO_RESCISAO = ?," +
                    " COD_TIPO_RESTITUICAO = ?," +
                    " COD_TIPO_RESCISAO = ?," +
                    " DATA_DESLIGAMENTO = ?," +
                    " DATA_INICIO_FERIAS = ?," +
                    " VALOR_DECIMO_TERCEIRO = ?," +
                    " INCID_SUBMOD_4_1_DEC_TERCEIRO = ?," +
                    " INCID_MULTA_FGTS_DEC_TERCEIRO = ?," +
                    " VALOR_FERIAS = ?," +
                    " VALOR_TERCO = ?," +
                    " INCID_SUBMOD_4_1_FERIAS = ?," +
                    " INCID_SUBMOD_4_1_TERCO = ?," +
                    " INCID_MULTA_FGTS_FERIAS = ?," +
                    " INCID_MULTA_FGTS_TERCO = ?," +
                    " VALOR_FERIAS_PROP = ?," +
                    " VALOR_TERCO_PROP = ?," +
                    " INCID_SUBMOD_4_1_FERIAS_PROP = ?," +
                    " INCID_SUBMOD_4_1_TERCO_PROP = ?," +
                    " INCID_MULTA_FGTS_FERIAS_PROP = ?," +
                    " INCID_MULTA_FGTS_TERCO_PROP = ?," +
                    " MULTA_FGTS_SALARIO = ?," +
                    " DATA_REFERENCIA = ?," +
                    " AUTORIZADO = ?," +
                    " RESTITUIDO = ?," +
                    " OBSERVACAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodTbRestituicaoRescisao);
            preparedStatement.setInt(2, pCodTipoRestituicao);
            preparedStatement.setInt(3, pCodTipoRescisao);
            preparedStatement.setDate(4, pDataDesligamento);
            preparedStatement.setDate(5, pDataInicioFerias);
            preparedStatement.setFloat(6, pValorDecimoTerceiro);
            preparedStatement.setFloat(7, pIncidSubmod41DecTerceiro);
            preparedStatement.setFloat(8, pIncidMultaFGTSDecTeceriro);
            preparedStatement.setFloat(9, pValorFerias);
            preparedStatement.setFloat(10, pValorTerco);
            preparedStatement.setFloat(11, pIncidSubmod41Ferias);
            preparedStatement.setFloat(12, pIncidSubmod41Terco);
            preparedStatement.setFloat(13, pIncidMultaFGTSFerias);
            preparedStatement.setFloat(14, pIncidMultaFGTSTerco);
            preparedStatement.setFloat(15, pValorFeriasProporcional);
            preparedStatement.setFloat(16, pValorTercoProporcional);
            preparedStatement.setFloat(17, pIncidSubmod41FeriasProporcional);
            preparedStatement.setFloat(18, pIncidSubmod41TercoProporcional);
            preparedStatement.setFloat(19, pIncidMultaFGTSFeriasProporcional);
            preparedStatement.setFloat(20, pIncidMultaFGTSTercoProporcional);
            preparedStatement.setFloat(21, pMultaFGTSSalario);
            preparedStatement.setDate(22, pDataReferencia);
            preparedStatement.setString(23, pAutorizado);
            preparedStatement.setString(24,pRestituido);
            preparedStatement.setString(25, pObservacao);
            preparedStatement.setString(26, pLoginAtualizacao);
            preparedStatement.setInt(27, pCodHistoricoRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar os dados na tabela de histórico de restituição de rescisão. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateSaldoResidualRescisao (int pCodSaldoResidualRescisao,
                                            int pCodRestituicaoRescisao,
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
                                            String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_SALDO_RESIDUAL_RESCISAO" +
                    " SET cod_restituicao_rescisao = ?," +
                    " valor_decimo_terceiro = ?," +
                    " incid_submod_4_1_dec_terceiro = ?," +
                    " incid_multa_fgts_dec_terceiro = ?," +
                    " valor_ferias = ?," +
                    " valor_terco = ?," +
                    " incid_submod_4_1_ferias = ?," +
                    " incid_submod_4_1_terco = ?," +
                    " incid_multa_fgts_ferias = ?," +
                    " incid_multa_fgts_terco = ?," +
                    " valor_ferias_prop = ?," +
                    " valor_terco_prop = ?," +
                    " incid_submod_4_1_ferias_prop = ?," +
                    " incid_submod_4_1_terco_prop = ?," +
                    " incid_multa_fgts_ferias_prop = ?," +
                    " incid_multa_fgts_terco_prop = ?," +
                    " multa_fgts_salario = ?," +
                    " login_atualizacao = ?," +
                    " data_atualizacao = ?" +
                    " WHERE COD = CURRENT_TIMESTAMP";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodRestituicaoRescisao);
            preparedStatement.setFloat(2, pValorDecimoTerceiro);
            preparedStatement.setFloat(3, pValorIncidenciaDecimoTerceiro);
            preparedStatement.setFloat(4, pValorFGTSDecimoTerceiro);
            preparedStatement.setFloat(5, pValorFerias);
            preparedStatement.setFloat(6, pValorTerco);
            preparedStatement.setFloat(7, pValorIncidenciaFerias);
            preparedStatement.setFloat(8, pValorIncidenciaTerco);
            preparedStatement.setFloat(9, pValorFGTSFerias);
            preparedStatement.setFloat(10, pValorFGTSTerco);
            preparedStatement.setFloat(11, pValorFeriasProporcional);
            preparedStatement.setFloat(12, pValorTercoProporcional);
            preparedStatement.setFloat(13, pValorIncidenciaFeriasProporcional);
            preparedStatement.setFloat(14, pValorIncidenciaTercoProporcional);
            preparedStatement.setFloat(15, pValorFGTSFeriasProporcional);
            preparedStatement.setFloat(16, pValorFGTSTercoProporcional);
            preparedStatement.setFloat(17, pValorFGTSSalario);
            preparedStatement.setString(18, pLoginAtualizacao);
            preparedStatement.setInt(19, pCodSaldoResidualRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar atualizar os dados na tabela de saldo residual de rescisão. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

}