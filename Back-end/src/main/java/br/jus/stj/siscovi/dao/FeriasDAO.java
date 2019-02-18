package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.model.AvaliacaoFerias;
import br.jus.stj.siscovi.model.CalcularFeriasModel;
import br.jus.stj.siscovi.model.CalculoPendenteModel;
import br.jus.stj.siscovi.model.TerceirizadoFerias;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class FeriasDAO {
    private final Connection connection;

    public FeriasDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     *
     * @param codigoContrato
     * @return
     */
    public ArrayList<TerceirizadoFerias> getListaTerceirizadoParaCalculoDeFerias(int codigoContrato) {
        ArrayList<TerceirizadoFerias> terceirizados = new ArrayList<>();
        String sql = "SELECT TC.COD, " +
                " T.NOME " +
                " FROM tb_terceirizado_contrato TC " +
                " JOIN " +
                " tb_terceirizado T ON T.COD=TC.COD_TERCEIRIZADO " +
                " WHERE COD_CONTRATO=? AND T.ATIVO='S'";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            Ferias ferias = new Ferias(connection);
            try(ResultSet resultSet = preparedStatement.executeQuery()){

                while(resultSet.next()) {

                    Date inicioPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(resultSet.getInt("COD"), 1);
                    Date fimPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(resultSet.getInt("COD"), 2);
                    int diasUsufruidos = ferias.RetornaDiasFeriasUsufruidosPeriodo(resultSet.getInt("COD"), inicioPeriodoAquisitivo, fimPeriodoAquisitivo);
                    boolean parcela14Dias = ferias.RetornaParcela14DiasFeriasPeriodo(resultSet.getInt("COD"), inicioPeriodoAquisitivo, fimPeriodoAquisitivo);
                    String parcelaAnterior = ferias.RetornaMaiorParcelaConcedidaFeriasPeriodo(resultSet.getInt("COD"), inicioPeriodoAquisitivo, fimPeriodoAquisitivo);

                    TerceirizadoFerias terceirizadoFerias = new TerceirizadoFerias(resultSet.getInt("COD"),
                            resultSet.getString("NOME"),
                            inicioPeriodoAquisitivo,
                            fimPeriodoAquisitivo,
                            diasUsufruidos,
                            parcela14Dias,
                            ferias.ExisteFeriasTerceirizado(resultSet.getInt("COD")),
                            parcelaAnterior);
                    terceirizados.add(terceirizadoFerias);
                }
            }
        } catch (SQLException e) {
            throw new NullPointerException("Nenhum funcionário ativo encontrado para este contrato.");
        }
        return terceirizados;
    }

    /**
     *  Returns a list of previous vacation calculus for all the employees that had It's vacation rights calculated and has not been apreciated by the responsible party
     * @param codigoContrato - Id of the contract user wants to get pending vacation calculus
     * @return
     */
    public List<CalculoPendenteModel> getCalculosPendentes(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<CalculoPendenteModel> lista = new ArrayList<>();
            String sql = "SELECT rt.COD_TERCEIRIZADO_CONTRATO AS \"COD\"," +
                    " u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " c.numero_contrato AS \"Contrato N°\"," +
                    " tr.nome AS \"Tipo de restituição\"," +
                    " t.nome AS \"Terceirizado\"," +
                    " f.nome AS \"Cargo\"," +
                    " rt.data_inicio_periodo_aquisitivo AS \"Início do período aquisitivo\"," +
                    " rt.data_fim_periodo_aquisitivo AS \"Fim do período aquisitivo\"," +
                    " rt.data_inicio_usufruto AS \"Início do usufruto\"," +
                    " rt.data_fim_usufruto AS \"Fim do usufruto\"," +
                    " rt.dias_vendidos AS \"Dias vendidos\"," +
                    " rt.parcela AS \"PARCELA\"," +
                    " rt.valor_ferias AS \"Valor de férias\"," +
                    " rt.valor_terco_constitucional AS \"Valor de 1/3\"," +
                    " rt.incid_submod_4_1_ferias AS \"Incidência sobre férias\"," +
                    " rt.incid_submod_4_1_terco AS \"Incidência sobre 1/3\"," +
                    " rt.valor_ferias + rt.valor_terco_constitucional + rt.incid_submod_4_1_ferias + rt.incid_submod_4_1_terco AS \"Total\"," +
                    " rt.AUTORIZADO," +
                    " rt.cod AS CODIGO" +
                    " FROM tb_restituicao_ferias rt" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado" +
                    " JOIN tb_contrato c ON c.cod = tc.cod_contrato" +
                    " JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " WHERE tc.COD_CONTRATO = ? AND (AUTORIZADO IS NULL)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String status = "";
                        if (resultSet.getString("AUTORIZADO") == null) {
                            status = "Em Análise";
                        }
                        CalcularFeriasModel calcularFeriasModel = new CalcularFeriasModel(resultSet.getInt("COD"),
                                resultSet.getString("Tipo de restituição"),
                                resultSet.getInt("Dias vendidos"),
                                resultSet.getDate("Início do usufruto"),
                                resultSet.getDate("Fim do usufruto"),
                                resultSet.getDate("Início do período aquisitivo"),
                                resultSet.getDate("Fim do período aquisitivo"),
                                0,
                                resultSet.getInt("PARCELA"),
                                resultSet.getFloat("TOTAL"),
                                resultSet.getFloat("Valor de férias"),
                                resultSet.getFloat("Valor de 1/3"),
                                resultSet.getFloat("Incidência sobre férias"),
                                resultSet.getFloat("Incidência sobre 1/3"));
                        CalculoPendenteModel calculoPendenteModel = new CalculoPendenteModel(resultSet.getInt("CODIGO"), calcularFeriasModel,
                                resultSet.getString("Terceirizado"),
                                resultSet.getString("Cargo"),
                                status,
                                resultSet.getFloat("Total"));
                        lista.add(calculoPendenteModel);
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            return lista;
        }else {
            throw new RuntimeException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    public boolean salvaAvaliacaoCalculosFerias(AvaliacaoFerias avaliacaoFerias) {
        String sql = "UPDATE tb_restituicao_ferias" +
                " SET " +
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
                " OBSERVACAO = ?," +
                " LOGIN_ATUALIZACAO = ?," +
                " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                " WHERE COD_TERCEIRIZADO_CONTRATO = ? AND COD = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (CalculoPendenteModel calculoPendenteModel : avaliacaoFerias.getCalculosAvaliados()) {
                int i = 1;
                //preparedStatement.setString(1, calculoPendenteModel.getCalcularFeriasModel().getTipoRestituicao());
                preparedStatement.setDate(i++, calculoPendenteModel.getCalcularFeriasModel().getInicioPeriodoAquisitivo());
                preparedStatement.setDate(i++, calculoPendenteModel.getCalcularFeriasModel().getFimPeriodoAquisitivo());
                preparedStatement.setDate(i++, calculoPendenteModel.getCalcularFeriasModel().getInicioFerias());
                preparedStatement.setDate(i++, calculoPendenteModel.getCalcularFeriasModel().getFimFerias());
                preparedStatement.setInt(i++, calculoPendenteModel.getCalcularFeriasModel().getDiasVendidos());
                preparedStatement.setFloat(i++, calculoPendenteModel.getCalcularFeriasModel().getpTotalFerias());
                preparedStatement.setFloat(i++, calculoPendenteModel.getCalcularFeriasModel().getpTotalTercoConstitucional());
                preparedStatement.setFloat(i++, calculoPendenteModel.getCalcularFeriasModel().getpTotalIncidenciaFerias());
                preparedStatement.setFloat(i++, calculoPendenteModel.getCalcularFeriasModel().getpTotalIncidenciaTerco());
                preparedStatement.setInt(i++, calculoPendenteModel.getCalcularFeriasModel().getParcelas());
                preparedStatement.setString(i++,calculoPendenteModel.getStatus());
                preparedStatement.setString(i++, calculoPendenteModel.getObservacoes());
                preparedStatement.setString(i++, avaliacaoFerias.getUser().getUsername().toUpperCase());
                preparedStatement.setInt(i++, calculoPendenteModel.getCalcularFeriasModel().getCodTerceirizadoContrato());
                preparedStatement.setInt(i++, calculoPendenteModel.getCod());
                preparedStatement.executeUpdate();
            }
        }catch (SQLException sqle) {

        }
        return true;
    }

    public boolean salvarExecucaoFerias(AvaliacaoFerias avaliacaoFerias) {
        String sql = "UPDATE tb_restituicao_ferias" +
                " SET " +
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
                " RESTITUIDO = ?," +
                " OBSERVACAO = ?," +
                " LOGIN_ATUALIZACAO = ?," +
                " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                " WHERE COD_TERCEIRIZADO_CONTRATO = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (CalculoPendenteModel calculoPendenteModel : avaliacaoFerias.getCalculosAvaliados()) {
                //preparedStatement.setString(1, calculoPendenteModel.getCalcularFeriasModel().getTipoRestituicao());
                preparedStatement.setDate(1, calculoPendenteModel.getCalcularFeriasModel().getInicioPeriodoAquisitivo());
                preparedStatement.setDate(2, calculoPendenteModel.getCalcularFeriasModel().getFimPeriodoAquisitivo());
                preparedStatement.setDate(3, calculoPendenteModel.getCalcularFeriasModel().getInicioFerias());
                preparedStatement.setDate(4, calculoPendenteModel.getCalcularFeriasModel().getFimFerias());
                preparedStatement.setInt(5, calculoPendenteModel.getCalcularFeriasModel().getDiasVendidos());
                preparedStatement.setFloat(6, calculoPendenteModel.getCalcularFeriasModel().getpTotalFerias());
                preparedStatement.setFloat(7, calculoPendenteModel.getCalcularFeriasModel().getpTotalTercoConstitucional());
                preparedStatement.setFloat(8, calculoPendenteModel.getCalcularFeriasModel().getpTotalIncidenciaFerias());
                preparedStatement.setFloat(9, calculoPendenteModel.getCalcularFeriasModel().getpTotalIncidenciaTerco());
                preparedStatement.setInt(10, calculoPendenteModel.getCalcularFeriasModel().getParcelas());
                preparedStatement.setString(11,calculoPendenteModel.getStatus());
                preparedStatement.setString(12, calculoPendenteModel.getObservacoes());
                preparedStatement.setString(13, avaliacaoFerias.getUser().getUsername().toUpperCase());
                preparedStatement.setInt(14, calculoPendenteModel.getCalcularFeriasModel().getCodTerceirizadoContrato());
                preparedStatement.executeUpdate();
            }
        }catch (SQLException sqle) {

        }
        return true;
    }

    public List<CalculoPendenteModel> getCalculosNegados(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<CalculoPendenteModel> lista = new ArrayList<>();
            String sql = "SELECT rt.COD_TERCEIRIZADO_CONTRATO AS \"COD\"," +
                    " u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " c.numero_contrato AS \"Contrato N°\"," +
                    " tr.nome AS \"Tipo de restituição\"," +
                    " t.nome AS \"Terceirizado\"," +
                    " f.nome AS \"Cargo\"," +
                    " rt.data_inicio_periodo_aquisitivo AS \"Início do período aquisitivo\"," +
                    " rt.data_fim_periodo_aquisitivo AS \"Fim do período aquisitivo\"," +
                    " rt.data_inicio_usufruto AS \"Início do usufruto\"," +
                    " rt.data_fim_usufruto AS \"Fim do usufruto\"," +
                    " rt.dias_vendidos AS \"Dias vendidos\"," +
                    " rt.parcela AS \"PARCELA\"," +
                    " rt.valor_ferias AS \"Valor de férias\"," +
                    " rt.valor_terco_constitucional AS \"Valor de 1/3\"," +
                    " rt.incid_submod_4_1_ferias AS \"Incidência sobre férias\"," +
                    " rt.incid_submod_4_1_terco AS \"Incidência sobre 1/3\"," +
                    " rt.valor_ferias + rt.valor_terco_constitucional + rt.incid_submod_4_1_ferias + rt.incid_submod_4_1_terco AS \"Total\"," +
                    " rt.AUTORIZADO," +
                    " rt.COD AS CODIGO" +
                    " FROM tb_restituicao_ferias rt" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado" +
                    " JOIN tb_contrato c ON c.cod = tc.cod_contrato" +
                    " JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " WHERE tc.COD_CONTRATO = ? AND (AUTORIZADO='N' OR AUTORIZADO='n')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String status = "";
                        if (resultSet.getString("AUTORIZADO") == null) {
                            status = "Em Análise";
                        }
                        if (resultSet.getString("AUTORIZADO").toUpperCase().equals("N")) {
                            status = "NEGADO";
                        }
                        CalcularFeriasModel calcularFeriasModel = new CalcularFeriasModel(resultSet.getInt("COD"),
                                resultSet.getString("Tipo de restituição"),
                                resultSet.getInt("Dias vendidos"),
                                resultSet.getDate("Início do usufruto"),
                                resultSet.getDate("Fim do usufruto"),
                                resultSet.getDate("Início do período aquisitivo"),
                                resultSet.getDate("Fim do período aquisitivo"),
                                0,
                                resultSet.getInt("PARCELA"),
                                resultSet.getFloat("TOTAL"),
                                resultSet.getFloat("Valor de férias"),
                                resultSet.getFloat("Valor de 1/3"),
                                resultSet.getFloat("Incidência sobre férias"),
                                resultSet.getFloat("Incidência sobre 1/3"));
                        CalculoPendenteModel calculoPendenteModel = new CalculoPendenteModel(resultSet.getInt("CODIGO"), calcularFeriasModel,
                                resultSet.getString("Terceirizado"),
                                resultSet.getString("Cargo"),
                                status,
                                resultSet.getFloat("Total"));
                        lista.add(calculoPendenteModel);
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            return lista;
        }else {
            throw new RuntimeException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    /**
     * Função que retorna a lista de cálculos de férias que foram avaliados pelo devido usuário da Secretaria de Administração e que foram aprovados, logo
     * obrigatoriamente a tabela de Restituição de Férias deve possuir registro na coluna 'AUTORIZADO' com valor afirmativo, no caso foi adotado o caractere 'S',
     * e que não foram executados por Usuário da Secretaria de Finanças, logo a coluna 'RESTITUÍDO' deve estar nula.
     *
     * Usuários do perfil de Gestão do Contrato na condição de Gestor ou de substituto do mesmo podem ver a lista mas não podem realizar nenhuma alteração
     *
     * Usuários de perfil da SOF podem alterar o status do cálculo
     *
     * Os demais Usuários não podem receber essa lista
     *
     * @param codigoContrato - Chave primária do contrato que se deseja obter a lista de
     * @param codigoUsuario - Chave primária do usuário que está fazendo a requisição. Sua permissão deve ser devidamente checada
     * @return List<CalculoPendenteModel> lista
     */
    public List<CalculoPendenteModel> getCalculosPendentesExecucao(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<CalculoPendenteModel> lista = new ArrayList<>();
            String sql = "SELECT rt.COD_TERCEIRIZADO_CONTRATO AS \"COD\"," +
                    " u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " c.numero_contrato AS \"Contrato N°\"," +
                    " tr.nome AS \"Tipo de restituição\"," +
                    " t.nome AS \"Terceirizado\"," +
                    " f.nome AS \"Cargo\"," +
                    " rt.data_inicio_periodo_aquisitivo AS \"Início do período aquisitivo\"," +
                    " rt.data_fim_periodo_aquisitivo AS \"Fim do período aquisitivo\"," +
                    " rt.data_inicio_usufruto AS \"Início do usufruto\"," +
                    " rt.data_fim_usufruto AS \"Fim do usufruto\"," +
                    " rt.dias_vendidos AS \"Dias vendidos\"," +
                    " rt.parcela AS \"PARCELA\"," +
                    " rt.valor_ferias AS \"Valor de férias\"," +
                    " rt.valor_terco_constitucional AS \"Valor de 1/3\"," +
                    " rt.incid_submod_4_1_ferias AS \"Incidência sobre férias\"," +
                    " rt.incid_submod_4_1_terco AS \"Incidência sobre 1/3\"," +
                    " rt.valor_ferias + rt.valor_terco_constitucional + rt.incid_submod_4_1_ferias + rt.incid_submod_4_1_terco AS \"Total\"," +
                    " rt.AUTORIZADO," +
                    " rt.RESTITUIDO," +
                    " rt.COD AS CODIGO" +
                    " FROM tb_restituicao_ferias rt" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado" +
                    " JOIN tb_contrato c ON c.cod = tc.cod_contrato" +
                    " JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " WHERE tc.COD_CONTRATO = ? AND ((AUTORIZADO ='S' OR AUTORIZADO ='s') AND (RESTITUIDO IS NULL))";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        String status = "";
                        String autorizado;
                        String restituido;
                        if(resultSet.getString("AUTORIZADO").toUpperCase() != null) {
                            autorizado = resultSet.getString("AUTORIZADO").toUpperCase();
                        }else {
                            autorizado = null;
                        }
                        if(resultSet.getString("RESTITUIDO") != null) {
                            restituido = resultSet.getString("RESTITUIDO").toUpperCase();
                        }else {
                            restituido = null;
                        }
                        if(autorizado.equals("S") && restituido == null) {
                            status = "Em Análise";
                        }
                        CalcularFeriasModel calcularFeriasModel = new CalcularFeriasModel(resultSet.getInt("COD"),
                                resultSet.getString("Tipo de restituição"),
                                resultSet.getInt("Dias vendidos"),
                                resultSet.getDate("Início do usufruto"),
                                resultSet.getDate("Fim do usufruto"),
                                resultSet.getDate("Início do período aquisitivo"),
                                resultSet.getDate("Fim do período aquisitivo"),
                                0,
                                resultSet.getInt("PARCELA"),
                                resultSet.getFloat("TOTAL"),
                                resultSet.getFloat("Valor de férias"),
                                resultSet.getFloat("Valor de 1/3"),
                                resultSet.getFloat("Incidência sobre férias"),
                                resultSet.getFloat("Incidência sobre 1/3"));
                        CalculoPendenteModel calculoPendenteModel = new CalculoPendenteModel(resultSet.getInt("CODIGO"), calcularFeriasModel,
                                resultSet.getString("Terceirizado"),
                                resultSet.getString("Cargo"),
                                status,
                                resultSet.getFloat("Total"));
                        lista.add(calculoPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            return lista;
        }else {
            throw new NullPointerException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    public List<CalculoPendenteModel> getCalculosNaoPendentesNegados(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<CalculoPendenteModel> lista = new ArrayList<>();
            String sql = "SELECT rt.COD_TERCEIRIZADO_CONTRATO AS \"COD\"," +
                    " u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " c.numero_contrato AS \"Contrato N°\"," +
                    " tr.nome AS \"Tipo de restituição\"," +
                    " t.nome AS \"Terceirizado\"," +
                    " f.nome AS \"Cargo\"," +
                    " rt.data_inicio_periodo_aquisitivo AS \"Início do período aquisitivo\"," +
                    " rt.data_fim_periodo_aquisitivo AS \"Fim do período aquisitivo\"," +
                    " rt.data_inicio_usufruto AS \"Início do usufruto\"," +
                    " rt.data_fim_usufruto AS \"Fim do usufruto\"," +
                    " rt.dias_vendidos AS \"Dias vendidos\"," +
                    " rt.parcela AS \"PARCELA\"," +
                    " rt.valor_ferias AS \"Valor de férias\"," +
                    " rt.valor_terco_constitucional AS \"Valor de 1/3\"," +
                    " rt.incid_submod_4_1_ferias AS \"Incidência sobre férias\"," +
                    " rt.incid_submod_4_1_terco AS \"Incidência sobre 1/3\"," +
                    " rt.valor_ferias + rt.valor_terco_constitucional + rt.incid_submod_4_1_ferias + rt.incid_submod_4_1_terco AS \"Total\"," +
                    " rt.AUTORIZADO," +
                    " rt.RESTITUIDO," +
                    " rt.COD AS CODIGO" +
                    " FROM tb_restituicao_ferias rt" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado" +
                    " JOIN tb_contrato c ON c.cod = tc.cod_contrato" +
                    " JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " WHERE tc.COD_CONTRATO = ? AND ((AUTORIZADO ='S' OR AUTORIZADO ='s') AND (RESTITUIDO = 'N' OR RESTITUIDO='n'))";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        String status = "";
                        String autorizado;
                        String restituido;
                        if(resultSet.getString("AUTORIZADO").toUpperCase() != null) {
                            autorizado = resultSet.getString("AUTORIZADO").toUpperCase();
                        }else {
                            autorizado = null;
                        }
                        if(resultSet.getString("RESTITUIDO") != null ) {
                            restituido = resultSet.getString("RESTITUIDO").toUpperCase();
                        }else {
                            restituido = null;
                        }
                        if(autorizado.equals("S") && restituido == null) {
                            status = "Em Análise";
                        }else if(restituido.equals("N")){
                            status = "NEGADO";
                        }
                        CalcularFeriasModel calcularFeriasModel = new CalcularFeriasModel(resultSet.getInt("COD"),
                                resultSet.getString("Tipo de restituição"),
                                resultSet.getInt("Dias vendidos"),
                                resultSet.getDate("Início do usufruto"),
                                resultSet.getDate("Fim do usufruto"),
                                resultSet.getDate("Início do período aquisitivo"),
                                resultSet.getDate("Fim do período aquisitivo"),
                                0,
                                resultSet.getInt("PARCELA"),
                                resultSet.getFloat("TOTAL"),
                                resultSet.getFloat("Valor de férias"),
                                resultSet.getFloat("Valor de 1/3"),
                                resultSet.getFloat("Incidência sobre férias"),
                                resultSet.getFloat("Incidência sobre 1/3"));
                        CalculoPendenteModel calculoPendenteModel = new CalculoPendenteModel(resultSet.getInt("CODIGO"), calcularFeriasModel,
                                resultSet.getString("Terceirizado"),
                                resultSet.getString("Cargo"),
                                status,
                                resultSet.getFloat("Total"));
                        lista.add(calculoPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            return lista;
        }else {
            throw new NullPointerException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    public List<CalculoPendenteModel> getRestituicoesFerias(int codigoContrato, int codigoUsuario) {
        int cod = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        List<CalculoPendenteModel> lista = new ArrayList<>();
        if(cod == codGestor) {
            String sql = "SELECT rt.COD_TERCEIRIZADO_CONTRATO AS \"COD\"," +
                    " u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " c.numero_contrato AS \"Contrato N°\"," +
                    " tr.nome AS \"Tipo de restituição\"," +
                    " t.nome AS \"Terceirizado\"," +
                    " f.nome AS \"Cargo\"," +
                    " rt.data_inicio_periodo_aquisitivo AS \"Início do período aquisitivo\"," +
                    " rt.data_fim_periodo_aquisitivo AS \"Fim do período aquisitivo\"," +
                    " rt.data_inicio_usufruto AS \"Início do usufruto\"," +
                    " rt.data_fim_usufruto AS \"Fim do usufruto\"," +
                    " rt.dias_vendidos AS \"Dias vendidos\"," +
                    " rt.parcela AS \"PARCELA\"," +
                    " rt.valor_ferias AS \"Valor de férias\"," +
                    " rt.valor_terco_constitucional AS \"Valor de 1/3\"," +
                    " rt.incid_submod_4_1_ferias AS \"Incidência sobre férias\"," +
                    " rt.incid_submod_4_1_terco AS \"Incidência sobre 1/3\"," +
                    " rt.valor_ferias + rt.valor_terco_constitucional + rt.incid_submod_4_1_ferias + rt.incid_submod_4_1_terco AS \"Total\"," +
                    " rt.AUTORIZADO," +
                    " rt.RESTITUIDO," +
                    " rt.COD AS CODIGO"+
                    " FROM tb_restituicao_ferias rt" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado" +
                    " JOIN tb_contrato c ON c.cod = tc.cod_contrato" +
                    " JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " WHERE tc.COD_CONTRATO = ? AND ((AUTORIZADO ='S' OR AUTORIZADO ='s') AND (RESTITUIDO = 'S' OR RESTITUIDO='s'))";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        CalcularFeriasModel calcularFeriasModel = new CalcularFeriasModel(resultSet.getInt("COD"),
                                resultSet.getString("Tipo de restituição"),
                                resultSet.getInt("Dias vendidos"),
                                resultSet.getDate("Início do usufruto"),
                                resultSet.getDate("Fim do usufruto"),
                                resultSet.getDate("Início do período aquisitivo"),
                                resultSet.getDate("Fim do período aquisitivo"),
                                0,
                                resultSet.getInt("PARCELA"),
                                resultSet.getFloat("TOTAL"),
                                resultSet.getFloat("Valor de férias"),
                                resultSet.getFloat("Valor de 1/3"),
                                resultSet.getFloat("Incidência sobre férias"),
                                resultSet.getFloat("Incidência sobre 1/3"));
                        CalculoPendenteModel calculoPendenteModel = new CalculoPendenteModel(resultSet.getInt("CODIGO"), calcularFeriasModel,
                                resultSet.getString("Terceirizado"),
                                resultSet.getString("Cargo"),
                                null,
                                resultSet.getFloat("Total"));
                        lista.add(calculoPendenteModel);
                    }
                }
            }catch (SQLException slqe) {
                slqe.printStackTrace();
            }
        }
        return lista;
    }
}
