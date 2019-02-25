package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.model.CalcularRescisaoModel;
import br.jus.stj.siscovi.model.CalculoPendenteRescisaoModel;
import br.jus.stj.siscovi.model.TerceirizadoRescisao;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.*;
import java.util.List;

public class RescisaoDAO {
    private Connection connection;

    public RescisaoDAO (Connection connection) {
        this.connection = connection;
    }

    /**
     * @param codigoContrato
     * @return
     */
    public ArrayList<TerceirizadoRescisao> getListaTerceirizadoParaCalculoDeRescisao (int codigoContrato) {
        ArrayList<TerceirizadoRescisao> terceirizados = new ArrayList<>();
        Ferias ferias = new Ferias(connection);
        String sql = "SELECT TC.COD," +
                     "       T.NOME," +
                     "       TC.DATA_DESLIGAMENTO" +
                     " FROM tb_terceirizado_contrato TC" +
                     "   JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO" +
                     " WHERE COD_CONTRATO = ?" +
                     "   AND TC.DATA_DESLIGAMENTO IS NOT NULL" +
                     "   AND tc.cod NOT IN (SELECT cod_terceirizado_contrato" +
                     "                        FROM tb_restituicao_rescisao)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TerceirizadoRescisao terceirizadoRescisao = new TerceirizadoRescisao(resultSet.getInt("COD"),
                            resultSet.getString("NOME"),
                            resultSet.getDate(3),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 1),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 2),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 3),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 4),
                            null,
                            null);
                    terceirizados.add(terceirizadoRescisao);
                }
            } catch (SQLException sqle) {
                throw new NullPointerException("Erro ao recuperar funcionários do contrato.");
            }
        } catch (SQLException sqle) {
            throw new NullPointerException("Erro ao buscar funcionários ativos no contrato.");
        }
        return terceirizados;
    }

    public List<CalculoPendenteRescisaoModel> getRestituicoesRescisao(int codigoContrato, int codigoUsuario) {
        int cod = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        List<CalculoPendenteRescisaoModel> lista = new ArrayList<>();
        if(cod == codGestor) {
            String sql = "SELECT rt.COD,\n" +
                        "        rt.COD_TERCEIRIZADO_CONTRATO, \n" +
                        "        u.nome, \n" +
                        "        c.nome_empresa, \n" +
                        "        c.numero_contrato, \n" +
                        "        tr.nome, \n" +
                        "        trr.TIPO_RESCISAO, \n" +
                        "        t.nome, \n" +
                        "        f.nome, \n" +
                        "        rt.data_desligamento, \n" +
                        "        rt.data_inicio_ferias, \n" +
                        "        rt.data_fim_ferias, \n" +
                        "        rt.data_inicio_ferias_prop, \n" +
                        "        rt.data_fim_ferias_prop, \n" +
                        "        rt.data_inicio_contagem_dec_ter,\n" +
                        "        rt.valor_decimo_terceiro, \n" +
                        "        rt.INCID_SUBMOD_4_1_DEC_TERCEIRO, \n" +
                        "        rt.INCID_MULTA_FGTS_DEC_TERCEIRO, \n" +
                        "        rt.VALOR_FERIAS, \n" +
                        "        rt.VALOR_TERCO, \n" +
                        "        rt.INCID_SUBMOD_4_1_FERIAS, \n" +
                        "        rt.INCID_SUBMOD_4_1_TERCO,\n" +
                        "        rt.INCID_MULTA_FGTS_FERIAS,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO,\n" +
                        "        rt.VALOR_FERIAS_PROP,\n" +
                        "        rt.VALOR_TERCO_PROP,\n" +
                        "        rt.INCID_SUBMOD_4_1_FERIAS_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_FERIAS_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO_PROP,\n" +
                        "        rt.MULTA_FGTS_SALARIO,\n" +
                        "        rt.valor_decimo_terceiro + rt.INCID_SUBMOD_4_1_DEC_TERCEIRO + rt.INCID_MULTA_FGTS_DEC_TERCEIRO + rt.VALOR_FERIAS + rt.VALOR_TERCO + rt.INCID_SUBMOD_4_1_FERIAS + rt.INCID_SUBMOD_4_1_TERCO + rt.INCID_MULTA_FGTS_FERIAS + rt.INCID_MULTA_FGTS_TERCO + rt.VALOR_FERIAS_PROP + rt.VALOR_TERCO_PROP + rt.INCID_SUBMOD_4_1_FERIAS_PROP + rt.INCID_MULTA_FGTS_TERCO_PROP + rt.INCID_MULTA_FGTS_FERIAS_PROP + rt.INCID_MULTA_FGTS_TERCO_PROP + rt.MULTA_FGTS_SALARIO, \n" +
                        "        rt.AUTORIZADO, \n" +
                        "        rt.RESTITUIDO \n" +
                        "   FROM tb_restituicao_rescisao rt \n" +
                        "    JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato \n" +
                        "    JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod \n" +
                        "    JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado \n" +
                        "    JOIN tb_contrato c ON c.cod = tc.cod_contrato \n" +
                        "    JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao \n" +
                        "    JOIN tb_tipo_rescisao trr ON trr.cod = rt.cod_tipo_rescisao \n" +
                        "    JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod \n" +
                        "    JOIN tb_usuario u ON u.cod = hgc.cod_usuario \n" +
                        "    JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato \n" +
                        "    JOIN tb_funcao f ON f.cod = fc.cod_funcao \n" +
                        "   WHERE tc.COD_CONTRATO = ? AND ((AUTORIZADO ='S' OR AUTORIZADO ='s') AND (RESTITUIDO = 'S' OR RESTITUIDO='s'))";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {

                        CalcularRescisaoModel calcularRescisaoModel = new CalcularRescisaoModel(resultSet.getInt(2),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getDate(10),
                                resultSet.getDate(11),
                                resultSet.getDate(12),
                                resultSet.getDate(13),
                                resultSet.getDate(14),
                                resultSet.getDate(15),
                                0,
                                0,
                                0,
                                resultSet.getFloat(16),
                                resultSet.getFloat(17),
                                resultSet.getFloat(18),
                                resultSet.getFloat(19),
                                resultSet.getFloat(20),
                                resultSet.getFloat(21),
                                resultSet.getFloat(22),
                                resultSet.getFloat(23),
                                resultSet.getFloat(24),
                                resultSet.getFloat(25),
                                resultSet.getFloat(26),
                                resultSet.getFloat(27),
                                resultSet.getFloat(28),
                                resultSet.getFloat(29),
                                resultSet.getFloat(30),
                                resultSet.getFloat(31));


                        CalculoPendenteRescisaoModel calculoPendenteModel = new CalculoPendenteRescisaoModel(resultSet.getInt(1),
                                calcularRescisaoModel,
                                resultSet.getString(8),
                                resultSet.getString(9),
                                null,
                                resultSet.getFloat(32));
                        lista.add(calculoPendenteModel);
                    }
                }
            }catch (SQLException slqe) {
                slqe.printStackTrace();
            }
        }
        return lista;
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
                    " rt.COD AS CODIGO," +
                    " rt.OBSERVACAO" +
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
                        calculoPendenteModel.setObservacoes(resultSet.getString("OBSERVACAO"));
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
                    " rt.COD AS CODIGO," +
                    " rt.OBSERVACAO" +
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
                        calculoPendenteModel.setObservacoes(resultSet.getString("OBSERVACAO"));
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
}
