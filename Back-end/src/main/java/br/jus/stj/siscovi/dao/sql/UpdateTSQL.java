package br.jus.stj.siscovi.dao.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

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

    public void UpdateRestituicaoFerias(int pCodRestituicaoFerias,
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

    public void UpdateRestituicaoDecimoTerceiro(int pCodRestituicaoDecimoTerceiro,
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
     * @param pDataInicioFeriasIntegrais;
     * @param pDataFimFeriasIntegrais;
     * @param pDataInicioFeriasProporcionais;
     * @param pDataFimFeriasProporcionais;
     * @param pDataInicioContagemDecTer;
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

    public void UpdateRestituicaoRescisao(int pCodRestituicaoRescisao,
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
                " DATA_FIM_FERIAS = ?," +
                " DATA_INICIO_FERIAS_PROP = ?," +
                " DATA_FIM_FERIAS_PROP = ?," +
                " DATA_INICIO_CONTAGEM_DEC_TER = ?," +
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
            preparedStatement.setDate(4, pDataInicioFeriasIntegrais);
            preparedStatement.setDate(5, pDataFimFeriasIntegrais);
            preparedStatement.setDate(6, pDataInicioFeriasProporcionais);
            preparedStatement.setDate(7, pDataFimFeriasProporcionais);
            preparedStatement.setDate(8, pDataInicioContagemDecTer);
            preparedStatement.setFloat(9, pValorDecimoTerceiro);
            preparedStatement.setFloat(10, pValorIncidenciaDecimoTerceiro);
            preparedStatement.setFloat(11, pValorFGTSDecimoTerceiro);
            preparedStatement.setFloat(12, pValorFerias);
            preparedStatement.setFloat(13, pValorTerco);
            preparedStatement.setFloat(14, pValorIncidenciaFerias);
            preparedStatement.setFloat(15, pValorIncidenciaTerco);
            preparedStatement.setFloat(16, pValorFGTSFerias);
            preparedStatement.setFloat(17, pValorFGTSTerco);
            preparedStatement.setFloat(18, pValorFeriasProporcional);
            preparedStatement.setFloat(19, pValorTercoProporcional);
            preparedStatement.setFloat(20, pValorIncidenciaFeriasProporcional);
            preparedStatement.setFloat(21, pValorIncidenciaTercoProporcional);
            preparedStatement.setFloat(22, pValorFGTSFeriasProporcional);
            preparedStatement.setFloat(23, pValorFGTSTercoProporcional);
            preparedStatement.setFloat(24, pValorFGTSSalario);
            preparedStatement.setString(25, String.valueOf(pAutorizado));
            preparedStatement.setString(26, String.valueOf(pRestituido));
            preparedStatement.setString(27, pObservacao);
            preparedStatement.setString(28, pLoginAtualizacao);
            preparedStatement.setInt(29, pCodRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro na execução da atualização dos dados da restiuição de rescisão.");

        }

    }

    public void UpdateRubrica(int pCodRubrica,
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
                    " WHERE COD = ?";

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

    public int UpdateContrato(int pCodContrato,
                              String pNomeEmpresa,
                              String pCnpj,
                              String pNumeroContrato,
                              String pNumeroProcessoSTJ,
                              String pSeAtivo,
                              String pObjeto,
                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_CONTRATO" +
                    " SET NOME_EMPRESA = ?," +
                    " CNPJ = ?," +
                    " NUMERO_CONTRATO = ?," +
                    " NUMERO_PROCESSO_STJ = ?," +
                    " SE_ATIVO = ?," +
                    " OBJETO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNomeEmpresa);
            preparedStatement.setString(2, pCnpj);
            preparedStatement.setString(3, pNumeroContrato);
            preparedStatement.setString(4, pNumeroProcessoSTJ);
            preparedStatement.setString(5, pSeAtivo);
            preparedStatement.setString(6, pObjeto);
            preparedStatement.setString(7, pLoginAtualizacao);
            preparedStatement.setInt(8, pCodContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o contrato. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateConvencaoColetiva(int pCodConvencaoColetiva,
                                       String pNome,
                                       String pSigla,
                                       Date pDataBase,
                                       String pDescricao,
                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_CONVENCAO_COLETIVA" +
                    " SET NOME = ?," +
                    " SIGLA = ?," +
                    " DATA_BASE = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pSigla);
            preparedStatement.setDate(3, pDataBase);
            preparedStatement.setString(4, pDescricao);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodConvencaoColetiva);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a convenção. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateEventoContratual(int pCodEventoContratual,
                                      int pCodContrato,
                                      int pCodTipoEvento,
                                      String pProrrogacao,
                                      String pAssunto,
                                      Date pDataInicioVigencia,
                                      Date pDataFimVigencia,
                                      Date pDataAssinatura,
                                      String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_EVENTO_CONTRATUAL" +
                    " SET COD_CONTRATO = ?," +
                    " COD_TIPO_EVENTO = ?," +
                    " PRORROGACAO = ?," +
                    " ASSUNTO = ?," +
                    " DATA_INICIO_VIGENCIA = ?," +
                    " DATA_FIM_VIGENCIA = ?," +
                    " DATA_ASSINATURA = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodTipoEvento);
            preparedStatement.setString(3, pProrrogacao);
            preparedStatement.setString(4, pAssunto);
            preparedStatement.setDate(5, pDataInicioVigencia);
            preparedStatement.setDate(6, pDataFimVigencia);
            preparedStatement.setDate(7, pDataAssinatura);
            preparedStatement.setString(8, pLoginAtualizacao);
            preparedStatement.setInt(9, pCodEventoContratual);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o evento contratual. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateFuncao(int pCodFuncao,
                            String pNome,
                            String pDescricao,
                            String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_FUNCAO" +
                    " SET NOME = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pDescricao);
            preparedStatement.setString(3, pLoginAtualizacao);
            preparedStatement.setInt(4, pCodFuncao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a função. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateFuncaoContrato(int pCodFuncaoContrato,
                                    int pCodContrato,
                                    int pCodFuncao,
                                    String pDescricao,
                                    String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_FUNCAO_CONTRATO" +
                    " SET COD_CONTRATO = ?," +
                    " COD_FUNCAO = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodFuncao);
            preparedStatement.setString(3, pDescricao);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodFuncaoContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a função do contrato. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateFuncaoTerceirizado(int pCodFuncaoTerceirizado,
                                        int pCodTerceirizadoContrato,
                                        int pCodFuncaoContrato,
                                        Date pDataInicio,
                                        Date pDataFim,
                                        String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_FUNCAO_TERCEIRIZADO" +
                    " SET COD_TERCEIRIZADO_CONTRATO = ?," +
                    " COD_FUNCAO_CONTRATO = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setInt(2, pCodFuncaoContrato);
            preparedStatement.setDate(3, pDataInicio);
            preparedStatement.setDate(4, pDataFim);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodFuncaoTerceirizado);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a função ao terceirizado. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateHistoricoGestaoContrato(int pCodHistoricoGestaoContrato,
                                             int pCodContrato,
                                             int pCodUsuario,
                                             int pCodPerfilGestao,
                                             Date pDataInicio,
                                             Date pDataFim,
                                             String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_HISTORICO_GESTAO_CONTRATO" +
                    " SET COD_CONTRATO = ?," +
                    " COD_USUARIO = ?," +
                    " COD_PERFIL_GESTAO = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodUsuario);
            preparedStatement.setInt(3, pCodPerfilGestao);
            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setString(6, pLoginAtualizacao);
            preparedStatement.setInt(7, pCodHistoricoGestaoContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o histórico de gestão do contrato. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdatePercentualContrato(int pCodPercentualContrato,
                                        int pCodContrato,
                                        int pCodRubrica,
                                        float pPercentual,
                                        Date pDataInicio,
                                        Date pDataFim,
                                        Date pDataAditamento,
                                        String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_PERCENTUAL_CONTRATO" +
                    " SET COD_CONTRATO = ?," +
                    " COD_RUBRICA = ?," +
                    " PERCENTUAL = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " DATA_ADITAMENTO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodRubrica);
            preparedStatement.setFloat(3, pPercentual);
            preparedStatement.setDate(4, pDataInicio);
            preparedStatement.setDate(5, pDataFim);
            preparedStatement.setDate(6, pDataAditamento);
            preparedStatement.setString(7, pLoginAtualizacao);
            preparedStatement.setInt(8, pCodPercentualContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o percentual. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdatePercentualEstatico(int pCodPercentualEstatico,
                                        int pCodRubrica,
                                        float pPercentual,
                                        Date pDataInicio,
                                        Date pDataFim,
                                        Date pDataAditamento,
                                        String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_PERCENTUAL_ESTATICO" +
                    " SET COD_RUBRICA = ?," +
                    " PERCENTUAL = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " DATA_ADITAMENTO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRubrica);
            preparedStatement.setFloat(2, pPercentual);
            preparedStatement.setDate(3, pDataInicio);
            preparedStatement.setDate(4, pDataFim);
            preparedStatement.setDate(5, pDataAditamento);
            preparedStatement.setString(6, pLoginAtualizacao);
            preparedStatement.setInt(7, pCodPercentualEstatico);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o percentual. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdatePerfilGestao(int pCodPerfilGestao,
                                  String pNome,
                                  String pSigla,
                                  String pDescricao,
                                  String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_PERFIL_GESTAO" +
                    " SET NOME = ?," +
                    " SIGLA = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pSigla);
            preparedStatement.setString(3, pDescricao);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodPerfilGestao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o perfil de gestão. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdatePerfilUsuario(int pCodPerfilUsuario,
                                   String pNome,
                                   String pSigla,
                                   String pDescricao,
                                   String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_PERFIL_USUARIO" +
                    " SET NOME = ?," +
                    " SIGLA = ?," +
                    " DESCRICAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pSigla);
            preparedStatement.setString(3, pDescricao);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodPerfilUsuario);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o perfil de usuário. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateRemuneracaoFunCon(int pCodRemuneracaoFunCon,
                                       int pCodFuncaoContrato,
                                       Integer pCodConvencaoColetiva,
                                       Date pDataInicio,
                                       Date pDataFim,
                                       Date pDataAditamento,
                                       float pRemuneracao,
                                       float pAdicionais,
                                       float pTrienios,
                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_REMUNERACAO_FUN_CON" +
                    " SET COD_FUNCAO_CONTRATO = ?," +
                    " COD_CONVENCAO_COLETIVA = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " DATA_ADITAMENTO = ?," +
                    " REMUNERACAO = ?," +
                    " ADICIONAIS = ?," +
                    " TRIENIOS = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodFuncaoContrato);

            if (pCodConvencaoColetiva == null) {

                preparedStatement.setNull(2, java.sql.Types.INTEGER);

            } else {

                preparedStatement.setInt(2, pCodConvencaoColetiva);

            }

            preparedStatement.setDate(3, pDataInicio);
            preparedStatement.setDate(4, pDataFim);
            preparedStatement.setDate(5, pDataAditamento);
            preparedStatement.setFloat(6, pRemuneracao);
            preparedStatement.setFloat(7, pAdicionais);
            preparedStatement.setFloat(8, pTrienios);
            preparedStatement.setString(9, pLoginAtualizacao);
            preparedStatement.setInt(10, pCodRemuneracaoFunCon);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a remuneração de função. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateRetroPercentualEstatico(int pCodRetroPercentualEstatico,
                                             int pCodContrato,
                                             int pCodPercentualEstatico,
                                             Date pInicio,
                                             Date pFim,
                                             Date pDataCobranca,
                                             String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_RETRO_PERCENTUAL_ESTATICO" +
                    " SET COD_CONTRATO = ?," +
                    " COD_PERCENTUAL_ESTATICO = ?," +
                    " INICIO = ?," +
                    " FIM = ?," +
                    " DATA_COBRANCA = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodPercentualEstatico);
            preparedStatement.setDate(3, pInicio);
            preparedStatement.setDate(4, pFim);
            preparedStatement.setDate(5, pDataCobranca);
            preparedStatement.setString(6, pLoginAtualizacao);
            preparedStatement.setInt(7, pCodRetroPercentualEstatico);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a retroatividade do percentual estático. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateRetroatividadePercentual(int pCodRetroatividadePercentual,
                                              int pCodPercentualContrato,
                                              Date pInicio,
                                              Date pFim,
                                              Date pDataCobranca,
                                              String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_RETROATIVIDADE_PERCENTUAL" +
                    " SET COD_PERCENTUAL_CONTRATO = ?," +
                    " INICIO = ?," +
                    " FIM = ?," +
                    " DATA_COBRANCA = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodPercentualContrato);
            preparedStatement.setDate(2, pInicio);
            preparedStatement.setDate(3, pFim);
            preparedStatement.setDate(4, pDataCobranca);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodRetroatividadePercentual);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a retroatividade do percentual do contrato. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateRetroatividadeRemuneracao(int pCodRetroatividadeRemuneracao,
                                               int pCodRemFuncaoContrato,
                                               Date pInicio,
                                               Date pFim,
                                               Date pDataCobranca,
                                               String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_RETROATIVIDADE_REMUNERACAO" +
                    " SET COD_REM_FUNCAO_CONTRATO = ?," +
                    " INICIO = ?," +
                    " FIM = ?," +
                    " DATA_COBRANCA = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRemFuncaoContrato);
            preparedStatement.setDate(2, pInicio);
            preparedStatement.setDate(3, pFim);
            preparedStatement.setDate(4, pDataCobranca);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodRetroatividadeRemuneracao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a retroatividade da remuneração. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateRetroatividadeTotalMensal(int pCodRetroatividadeTotalMensal,
                                               int pCodTotalMensalAReter,
                                               float pFerias,
                                               float pTercoConstitucional,
                                               float pDecimoTerceiro,
                                               float pIncidenciaSubmodulo41,
                                               float pMultaFgts,
                                               float pTotal,
                                               String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_RETROATIVIDADE_TOTAL_MENSAL" +
                    " SET COD_TOTAL_MENSAL_A_RETER = ?," +
                    " FERIAS = ?," +
                    " TERCO_CONSTITUCIONAL = ?," +
                    " DECIMO_TERCEIRO = ?," +
                    " INCIDENCIA_SUBMODULO_4_1 = ?," +
                    " MULTA_FGTS = ?," +
                    " TOTAL = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodTotalMensalAReter);
            preparedStatement.setFloat(2, pFerias);
            preparedStatement.setFloat(3, pTercoConstitucional);
            preparedStatement.setFloat(4, pDecimoTerceiro);
            preparedStatement.setFloat(5, pIncidenciaSubmodulo41);
            preparedStatement.setFloat(6, pMultaFgts);
            preparedStatement.setFloat(7, pTotal);
            preparedStatement.setString(8, pLoginAtualizacao);
            preparedStatement.setInt(9, pCodRetroatividadeTotalMensal);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar a retroatividade de total mensal respectiva à retenção. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTerceirizado(int pCodTerceirizado,
                                  String pNome,
                                  String pCpf,
                                  String pAtivo,
                                  String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TERCEIRIZADO" +
                    " SET NOME = ?," +
                    " CPF = ?," +
                    " ATIVO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pCpf);
            preparedStatement.setString(3, pAtivo);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodTerceirizado);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o terceirizado. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTerceirizadoContrato(int pCodTerceirizadoContrato,
                                          int pCodContrato,
                                          int pCodTerceirizado,
                                          Date pDataDisponibilizacao,
                                          Date pDataDesligamento,
                                          String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TERCEIRIZADO_CONTRATO" +
                    " SET COD_CONTRATO = ?," +
                    " COD_TERCEIRIZADO = ?," +
                    " DATA_DISPONIBILIZACAO = ?," +
                    " DATA_DESLIGAMENTO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodTerceirizado);
            preparedStatement.setDate(3, pDataDisponibilizacao);
            preparedStatement.setDate(4, pDataDesligamento);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodTerceirizadoContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o registro do terceirizado no contrato. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTipoEventoContratual(int pCodTipoEventoContratual,
                                          String pTipo,
                                          String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TIPO_EVENTO_CONTRATUAL" +
                    " SET TIPO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pTipo);
            preparedStatement.setString(2, pLoginAtualizacao);
            preparedStatement.setInt(3, pCodTipoEventoContratual);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o tipo de evento contratual. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTipoRescisao(int pCodTipoRescisao,
                                  String pTipoRescisao,
                                  String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TIPO_RESCISAO" +
                    " SET TIPO_RESCISAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pTipoRescisao);
            preparedStatement.setString(2, pLoginAtualizacao);
            preparedStatement.setInt(3, pCodTipoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o tipo de rescisão. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTipoRestituicao(int pCodTipoRestituicao,
                                     String pNome,
                                     String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TIPO_RESTITUICAO" +
                    " SET NOME = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pNome);
            preparedStatement.setString(2, pLoginAtualizacao);
            preparedStatement.setInt(3, pCodTipoRestituicao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o tipo de restituição. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateTrienioTercContrato(int pCodTrienioTercContrato,
                                         int pCodTerceirizadoContrato,
                                         int pNumeroDeTrienios,
                                         Date pDataInicio,
                                         Date pDataFim,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TRIENIO_TERC_CONTRATO" +
                    " SET COD_TERCEIRIZADO_CONTRATO = ?," +
                    " NUMERO_DE_TRIENIOS = ?," +
                    " DATA_INICIO = ?," +
                    " DATA_FIM = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setInt(1, pNumeroDeTrienios);
            preparedStatement.setDate(3, pDataInicio);
            preparedStatement.setDate(4, pDataFim);
            preparedStatement.setString(5, pLoginAtualizacao);
            preparedStatement.setInt(6, pCodTrienioTercContrato);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o triênio do terceirizado. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateUsuario(int pCodUsuario,
                             int pCodPerfil,
                             String pNome,
                             String pLogin,
                             String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_USUARIO" +
                    " SET COD_PERFIL = ?," +
                    " NOME = ?," +
                    " LOGIN = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodPerfil);
            preparedStatement.setString(2, pNome);
            preparedStatement.setString(3, pLogin);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodUsuario);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar o usuário. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public Integer UpdateHistoricoRestituicaoDecimoTerceiro(int pCodHistoricoRestituicaoDecimoTerceiro,
                                                            int pCodTbRestituicaoDecTer,
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
        int vRetorno = -1;


        try {

            String sql = "UPDATE TB_HIST_RESTITUICAO_DEC_TER" +
                    " SET COD_RESTITUICAO_DEC_TERCEIRO = ?," +
                    " COD_TIPO_RESTITUICAO = ?," +
                    " PARCELA = ?," +
                    " DATA_INICIO_CONTAGEM = ?," +
                    " VALOR = ?," +
                    " INCIDENCIA_SUBMODULO_4_1 = ?," +
                    " DATA_REFERENCIA = ?," +
                    " AUTORIZADO = ?," +
                    " RESTITUIDO = ?," +
                    " OBSERVACAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodTbRestituicaoDecTer);
            preparedStatement.setInt(2, pCodTipoRestituicao);
            preparedStatement.setInt(3, pParcela);
            preparedStatement.setDate(4, pDataInicioContagem);
            preparedStatement.setFloat(5, pValor);
            preparedStatement.setFloat(6, pIncidenciaSubmodulo41);
            preparedStatement.setDate(7, pDataReferencia);
            preparedStatement.setString(8, pAutorizado);
            preparedStatement.setString(9, pRestituido);
            preparedStatement.setString(10, pObservacao);
            preparedStatement.setString(11, pLoginAtualizacao);
            preparedStatement.setInt(12, pCodHistoricoRestituicaoDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar os dados na tabela de histórico de restituição de décimo terceiro. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public Integer UpdateHistoricoRestituicaoRescisao(int pCodHistoricoRestituicaoRescisao,
                                                      int pCodTbRestituicaoRescisao,
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
        int vRetorno = -1;


        try {

            String sql = "UPDATE TB_HIST_RESTITUICAO_RESCISAO" +
                    " SET COD_RESTITUICAO_RESCISAO = ?," +
                    " COD_TIPO_RESTITUICAO = ?," +
                    " COD_TIPO_RESCISAO = ?," +
                    " DATA_DESLIGAMENTO = ?," +
                    " DATA_INICIO_FERIAS = ?," +
                    " DATA_FIM_FERIAS = ?," +
                    " DATA_INICIO_FERIAS_PROP = ?," +
                    " DATA_FIM_FERIAS_PROP = ?," +
                    " DATA_INICIO_CONTAGEM_DEC_TER = ?," +
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
            preparedStatement.setDate(5, pDataInicioFeriasIntegrais);
            preparedStatement.setDate(6, pDataFimFeriasIntegrais);
            preparedStatement.setDate(7, pDataInicioFeriasProporcionais);
            preparedStatement.setDate(8, pDataFimFeriasProporcionais);
            preparedStatement.setDate(9, pDataInicioContagemDecTer);
            preparedStatement.setFloat(10, pValorDecimoTerceiro);
            preparedStatement.setFloat(11, pIncidSubmod41DecTerceiro);
            preparedStatement.setFloat(12, pIncidMultaFGTSDecTeceriro);
            preparedStatement.setFloat(13, pValorFerias);
            preparedStatement.setFloat(14, pValorTerco);
            preparedStatement.setFloat(15, pIncidSubmod41Ferias);
            preparedStatement.setFloat(16, pIncidSubmod41Terco);
            preparedStatement.setFloat(17, pIncidMultaFGTSFerias);
            preparedStatement.setFloat(18, pIncidMultaFGTSTerco);
            preparedStatement.setFloat(19, pValorFeriasProporcional);
            preparedStatement.setFloat(20, pValorTercoProporcional);
            preparedStatement.setFloat(21, pIncidSubmod41FeriasProporcional);
            preparedStatement.setFloat(22, pIncidSubmod41TercoProporcional);
            preparedStatement.setFloat(23, pIncidMultaFGTSFeriasProporcional);
            preparedStatement.setFloat(24, pIncidMultaFGTSTercoProporcional);
            preparedStatement.setFloat(25, pMultaFGTSSalario);
            preparedStatement.setDate(26, pDataReferencia);
            preparedStatement.setString(27, pAutorizado);
            preparedStatement.setString(28, pRestituido);
            preparedStatement.setString(29, pObservacao);
            preparedStatement.setString(30, pLoginAtualizacao);
            preparedStatement.setInt(31, pCodHistoricoRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível atualizar os dados na tabela de histórico de restituição de rescisão. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }


    public Integer UpdateHistoricoRestituicaoFerias(int pCodHistoricoRestituicaoFerias,
                                                    int pCodTbRestituicaoFerias,
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
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_HIST_RESTITUICAO_FERIAS" +
                    " SET COD_RESTITUICAO_FERIAS = ?," +
                    " COD_TIPO_RESTITUICAO = ?," +
                    " DATA_INICIO_PERIODO_AQUISITIVO = ?," +
                    " DATA_FIM_PERIODO_AQUISITIVO = ?," +
                    " DATA_INICIO_USUFRUTO = ?," +
                    " DATA_FIM_USUFRUTO = ?," +
                    " VALOR_FERIAS = ?," +
                    " VALOR_TERCO_CONSTITUCIONAL = ?," +
                    " INCID_SUBMOD_4_1_FERIAS = ?," +
                    " INCID_SUBMOD_4_1_TERCO = ?," +
                    " PARCELA = ?," +
                    " DIAS_VENDIDOS = ?," +
                    " DATA_REFERENCIA = ?," +
                    " AUTORIZADO = ?," +
                    " RESTITUIDO = ?," +
                    " OBSERVACAO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodTbRestituicaoFerias);
            preparedStatement.setInt(2, pCodTipoRestituicao);
            preparedStatement.setDate(3, pInicioPeriodoAquisitivo);
            preparedStatement.setDate(4, pFimPeriodoAquisitivo);
            preparedStatement.setDate(5, pInicioFerias);
            preparedStatement.setDate(6, pFimFerias);
            preparedStatement.setFloat(7, pTotalFerias);
            preparedStatement.setFloat(8, pTotalTercoConstitucional);
            preparedStatement.setFloat(9, pTotalIncidenciaFerias);
            preparedStatement.setFloat(10, pTotalIncidenciaTerco);
            preparedStatement.setInt(11, pParcela);
            preparedStatement.setInt(12, pDiasVendidos);
            preparedStatement.setDate(13, pDataReferencia);
            preparedStatement.setString(14, pAutorizado);
            preparedStatement.setString(15, pRestituido);
            preparedStatement.setString(16, pObservacao);
            preparedStatement.setString(17, pLoginAtualizacao);
            preparedStatement.setInt(18, pCodHistoricoRestituicaoFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível atualizar os dados na tabela de histórico de restituição de férias. Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateSaldoResidualFerias(int pCodSaldoResidualFerias,
                                         int pCodTbRestituicaoFerias,
                                         float pValorFerias,
                                         float pValorTerco,
                                         float pIncidenciaFerias,
                                         float pIncidenciaTerco,
                                         String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_SALDO_RESIDUAL_FERIAS" +
                    " SET COD_RESTITUICAO_FERIAS = ?," +
                    " VALOR_FERIAS = ?," +
                    " VALOR_TERCO = ?," +
                    " INCID_SUBMOD_4_1_FERIAS = ?," +
                    " INCID_SUBMOD_4_1_TERCO = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = ?" +
                    " WHERE COD = CURRENT_TIMESTAMP";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodTbRestituicaoFerias);
            preparedStatement.setFloat(2, pValorFerias);
            preparedStatement.setFloat(3, pValorTerco);
            preparedStatement.setFloat(4, pIncidenciaFerias);
            preparedStatement.setFloat(5, pIncidenciaTerco);
            preparedStatement.setString(6, pLoginAtualizacao);
            preparedStatement.setInt(7, pCodSaldoResidualFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar atualizar os dados do cálculo de férias no banco de dados! Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateSaldoResidualDecimoTerceiro(int pCodSaldoResidualDecimoTerceiro,
                                                 int pCodRestituicaoDecimoTerceiro,
                                                 float pValorDecimoTerceiro,
                                                 float pValorIncidencia,
                                                 String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_SALDO_RESIDUAL_DEC_TER" +
                    " SET COD_RESTITUICAO_DEC_TERCEIRO = ?," +
                    " VALOR = ?," +
                    " INCIDENCIA_SUBMODULO_4_1 = ?," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = ?" +
                    " WHERE COD = CURRENT_TIMESTAMP";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodRestituicaoDecimoTerceiro);
            preparedStatement.setFloat(2, pValorDecimoTerceiro);
            preparedStatement.setFloat(3, pValorIncidencia);
            preparedStatement.setString(4, pLoginAtualizacao);
            preparedStatement.setInt(5, pCodSaldoResidualDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

            throw new RuntimeException("Erro ao tentar atualizar os dados na tabela de saldo residual de décimo terceiro! Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public int UpdateSaldoResidualRescisao(int pCodSaldoResidualRescisao,
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

    public int UpdateTotalMensalAReter(int pCodTotalMensalAReter,
                                       int pCodTerceirizadoContrato,
                                       int pCodFuncaoTerceirizadoContrato,
                                       float pFerias,
                                       float pTercoConstitucional,
                                       float pDecimoTerceiro,
                                       float pIncidenciaSubmodulo41,
                                       float pMultaFgts,
                                       float pTotal,
                                       Date pDataReferencia,
                                       String pAutorizado,
                                       String pRetido,
                                       String pObeservacao,
                                       String pLoginAtualizacao) {

        PreparedStatement preparedStatement;
        int vRetorno = -1;

        try {

            String sql = "UPDATE TB_TOTAL_MENSAL_A_RETER" +
                    " SET COD_TERCEIRIZADO_CONTRATO = ?, " +
                    " COD_FUNCAO_TERCEIRIZADO = ?, " +
                    " FERIAS = ?, " +
                    " TERCO_CONSTITUCIONAL = ?," +
                    " DECIMO_TERCEIRO = ?, " +
                    " INCIDENCIA_SUBMODULO_4_1 = ?, " +
                    " MULTA_FGTS = ?," +
                    " TOTAL = ?, " +
                    " DATA_REFERENCIA = ?, " +
                    " AUTORIZADO = ?, " +
                    " RETIDO = ?, " +
                    " OBSERVACAO = ?, " +
                    " LOGIN_ATUALIZACAO = ?, " +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD = ?";


            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setInt(2, pCodFuncaoTerceirizadoContrato);
            preparedStatement.setFloat(3, pFerias);
            preparedStatement.setFloat(4, pTercoConstitucional);
            preparedStatement.setFloat(5, pDecimoTerceiro);
            preparedStatement.setFloat(6, pIncidenciaSubmodulo41);
            preparedStatement.setFloat(7, pMultaFgts);
            preparedStatement.setFloat(8, pTotal);
            preparedStatement.setDate(9, pDataReferencia);
            preparedStatement.setString(10, pAutorizado);
            preparedStatement.setString(11, pRetido);
            preparedStatement.setString(12, pObeservacao);
            preparedStatement.setString(13, pLoginAtualizacao);
            preparedStatement.setInt(14, pCodTotalMensalAReter);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao tentar atualizar os resultados do cálculo de Total Mensal a Reter no banco de dados! Retornou código: " + vRetorno);

        }

        vRetorno = 0;

        return vRetorno;

    }

    public void UpdateDataFimHistoricoGestaoContrato(int pCodHistoricoGestaoVigente, Date pDataInicio, String pUsername) throws RuntimeException {
        String sql = "UPDATE TB_HISTORICO_GESTAO_CONTRATO SET DATA_FIM=? , LOGIN_ATUALIZACAO=?, DATA_ATUALIZACAO=GETDATE() WHERE COD=?";
        LocalDate dateMinusOne = pDataInicio.toLocalDate().minusDays(1);
        Date dataInicioMenosUmDia = Date.valueOf(dateMinusOne);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, dataInicioMenosUmDia);
            preparedStatement.setString(2, pUsername);
            preparedStatement.setInt(3, pCodHistoricoGestaoVigente);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar atualizar o registro de código " + pCodHistoricoGestaoVigente + " em Historico Gestao Contrato. Causa: " +
                    e.getMessage());
        }
    }

    public void UpdateDataFimPercentualContrato(int pCodPercentualVigente, Date pDataInicio, String pUsername) throws RuntimeException {
        String sql = "UPDATE TB_PERCENTUAL_CONTRATO SET DATA_FIM=?, LOGIN_ATUALIZACAO=?, DATA_ATUALIZACAO=GETDATE() WHERE COD=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, subraiDias(pDataInicio, 1));
            preparedStatement.setString(2, pUsername);
            preparedStatement.setInt(3, pCodPercentualVigente);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar atualizar a data fim de um percentual do contrato ! [Codigo do registro:" +
                    pCodPercentualVigente + "]. Causa: " + e.getMessage());
        }
    }

    public void UpdateFimRemuneracaoFuncao(int pCodFuncaoContrato, Date pDataInicio, String pUsername) throws RuntimeException {
        String sql = "UPDATE TB_REMUNERACAO_FUN_CON SET DATA_FIM=?, LOGIN_ATUALIZACAO=? WHERE COD_FUNCAO_CONTRATO=? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, subraiDias(pDataInicio, 1));
            preparedStatement.setString(2, pUsername);
            preparedStatement.setInt(3, pCodFuncaoContrato);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar atualizar a data fim da remuneração vigente no contrato ! " +
                    e.getMessage());
        }
    }

    private Date subraiDias(Date date, long qtdDias) {
        LocalDate localDate = date.toLocalDate().minusDays(qtdDias);
        return Date.valueOf(localDate);
    }

}
