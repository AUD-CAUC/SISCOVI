package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.DecimoTerceiro;
import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.AvaliacaoDecimoTerceiro;
import br.jus.stj.siscovi.model.DecimoTerceiroPendenteModel;
import br.jus.stj.siscovi.model.TerceirizadoDecimoTerceiro;
import br.jus.stj.siscovi.model.ValorRestituicaoDecimoTerceiroModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DecimoTerceiroDAO {
    private Connection connection;

    public DecimoTerceiroDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * @param codigoContrato
     * @return
     */
    public ArrayList<TerceirizadoDecimoTerceiro> getListaTerceirizadoParaCalculoDeDecimoTerceiro(int codigoContrato, int pAnoContagem) {
        ArrayList<TerceirizadoDecimoTerceiro> terceirizados = new ArrayList<>();
        String sql = "SELECT TC.COD, " +
                " T.NOME" +
                " FROM tb_terceirizado_contrato TC " +
                " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                " WHERE COD_CONTRATO = ? AND T.ATIVO = 'S'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            DecimoTerceiro decimoTerceiro = new DecimoTerceiro(connection);
            Saldo saldoDecimoTerceiro = new Saldo(connection);
            float vSaldoDecimoTericeiro = 0; //Este saldo é correspondente ao ano da data de início da contagem.
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Date inicioContagem = decimoTerceiro.RetornaDataInicioContagem(resultSet.getInt("COD"), pAnoContagem);
                    vSaldoDecimoTericeiro = saldoDecimoTerceiro.getSaldoContaVinculada(resultSet.getInt("COD"), inicioContagem.toLocalDate().getYear(), 1, 3);
                    TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD"),
                            resultSet.getString("NOME"),
                            inicioContagem,
                            vSaldoDecimoTericeiro,
                            0);
                    terceirizados.add(terceirizadoDecimoTerceiro);
                }
            }
        } catch (SQLException e) {
            throw new NullPointerException("Nenhum funcionário ativo encontrado para este contrato.");
        }
        return terceirizados;
    }

    public List<DecimoTerceiroPendenteModel> getCalculosPendentes(int codigoContrato, int codigoUsuario) {
        List<DecimoTerceiroPendenteModel> lista = new ArrayList<>();
        int codigo = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        String status = "";
        if(codigo == codGestor) {
            String sql = "SELECT RDT.COD_TERCEIRIZADO_CONTRATO, " +
                    " T.NOME as TERCEIRIZADO," +
                    " TR.NOME as TIPO," +
                    " TF.NOME AS \"FUNÇÃO\"," +
                    " RDT.PARCELA," +
                    " RDT.DATA_INICIO_CONTAGEM," +
                    " RDT.VALOR," +
                    " RDT.INCIDENCIA_SUBMODULO_4_1 AS INCIDENCIA," +
                    " RDT.DATA_REFERENCIA," +
                    " RDT.AUTORIZADO," +
                    " RDT.COD AS CODIGO," +
                    " RDT.OBSERVACAO" +
                    " FROM tb_terceirizado_contrato TC" +
                    " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                    " JOIN tb_restituicao_decimo_terceiro RDT ON RDT.COD_TERCEIRIZADO_CONTRATO =TC.COD_TERCEIRIZADO" +
                    " JOIN tb_tipo_restituicao TR ON TR.COD=RDT.COD_TIPO_RESTITUICAO " +
                    " JOIN tb_funcao_terceirizado FT ON FT.COD_TERCEIRIZADO_CONTRATO= TC.cod" +
                    " JOIN tb_funcao_contrato FC ON FC.COD=FT.COD_FUNCAO_CONTRATO" +
                    " JOIN tb_funcao TF  ON TF.COD=FC.COD_FUNCAO" +
                    " WHERE TC.COD_CONTRATO = ? AND T.ATIVO = 'S' AND (AUTORIZADO IS NULL)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"),
                                resultSet.getString("TERCEIRIZADO"), resultSet.getDate("DATA_INICIO_CONTAGEM"),0, 0 );
                        terceirizadoDecimoTerceiro.setTipoRestituicao(resultSet.getString("TIPO"));
                        ValorRestituicaoDecimoTerceiroModel vrdtm = new ValorRestituicaoDecimoTerceiroModel(resultSet.getFloat("VALOR"), resultSet.getFloat("INCIDENCIA"));
                        terceirizadoDecimoTerceiro.setValoresDecimoTerceiro(vrdtm);
                        terceirizadoDecimoTerceiro.setParcelas(resultSet.getInt("PARCELA"));
                        terceirizadoDecimoTerceiro.setNomeCargo(resultSet.getString("FUNÇÃO"));
                        status = avaliaStatus(1, resultSet.getString("AUTORIZADO"), null);
                        DecimoTerceiroPendenteModel decimoTerceiroPendenteModel = new DecimoTerceiroPendenteModel(resultSet.getInt("CODIGO"), terceirizadoDecimoTerceiro, status,
                                resultSet.getString("OBSERVACAO"));
                        lista.add(decimoTerceiroPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new  RuntimeException("");
            }
        }
        return lista;
    }

    private String avaliaStatus(int operacao, String autorizado, String restituido) {
        switch (operacao) {
            case 1:
                if(autorizado == null)
                    return "EM ANÁLISE";
            case 2:
                if(autorizado.toUpperCase().equals("N"))
                    return "NEGADO";
            case 3:
                if(autorizado.toUpperCase().equals("S") && restituido == null)
                    return "EM ANÁLISE DE EXECUÇÃO";
            case 4:
                if(autorizado.toUpperCase().equals("S") && restituido.toUpperCase().equals("N"));
                    return "REJEITADO";
            case 5:
                if(autorizado.toUpperCase().equals("S") && restituido.toUpperCase().equals("S"))
                    return "RESTITUIDO";

        }
        return null;
    }

    public boolean salvarAlteracoesCalculo(AvaliacaoDecimoTerceiro avaliacaoDecimoTerceiro) {
        int codigo = new UsuarioDAO(connection).verifyPermission(avaliacaoDecimoTerceiro.getUser().getId(), avaliacaoDecimoTerceiro.getCodigoContrato());
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(avaliacaoDecimoTerceiro.getUser().getId(), avaliacaoDecimoTerceiro.getCodigoContrato());
        if(codGestor == codigo) {
            String sql = "UPDATE TB_RESTITUICAO_DECIMO_TERCEIRO SET AUTORIZADO=?, OBSERVACAO=? WHERE COD_TERCEIRIZADO_CONTRATO=? AND COD=?";
            List<DecimoTerceiroPendenteModel> lista = avaliacaoDecimoTerceiro.getDecimosTerceirosPendentes();
            return atualizaCalculos(sql, lista);
        }
        return false;
    }

    public List<DecimoTerceiroPendenteModel> getCalculosPendentesNegados(int codigoContrato, int codigoUsuario) {
        List<DecimoTerceiroPendenteModel> lista = new ArrayList<>();
        int codigo = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        String status = "";
        if(codigo == codGestor) {
            String sql = "SELECT RDT.COD_TERCEIRIZADO_CONTRATO, " +
                    " T.NOME as TERCEIRIZADO," +
                    " TR.NOME as TIPO," +
                    " TF.NOME AS \"FUNÇÃO\"," +
                    " RDT.PARCELA," +
                    " RDT.DATA_INICIO_CONTAGEM," +
                    " RDT.VALOR," +
                    " RDT.INCIDENCIA_SUBMODULO_4_1 AS INCIDENCIA," +
                    " RDT.DATA_REFERENCIA," +
                    " RDT.AUTORIZADO," +
                    " RDT.COD AS CODIGO," +
                    " RDT.OBSERVACAO" +
                    " FROM tb_terceirizado_contrato TC" +
                    " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                    " JOIN tb_restituicao_decimo_terceiro RDT ON RDT.COD_TERCEIRIZADO_CONTRATO =TC.COD_TERCEIRIZADO" +
                    " JOIN tb_tipo_restituicao TR ON TR.COD=RDT.COD_TIPO_RESTITUICAO " +
                    " JOIN tb_funcao_terceirizado FT ON FT.COD_TERCEIRIZADO_CONTRATO= TC.cod" +
                    " JOIN tb_funcao_contrato FC ON FC.COD=FT.COD_FUNCAO_CONTRATO" +
                    " JOIN tb_funcao TF  ON TF.COD=FC.COD_FUNCAO" +
                    " WHERE TC.COD_CONTRATO = ? AND T.ATIVO = 'S' AND (AUTORIZADO='n' OR AUTORIZADO='N')";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"),
                                resultSet.getString("TERCEIRIZADO"), resultSet.getDate("DATA_INICIO_CONTAGEM"),0, 0 );
                        terceirizadoDecimoTerceiro.setTipoRestituicao(resultSet.getString("TIPO"));
                        ValorRestituicaoDecimoTerceiroModel vrdtm = new ValorRestituicaoDecimoTerceiroModel(resultSet.getFloat("VALOR"), resultSet.getFloat("INCIDENCIA"));
                        terceirizadoDecimoTerceiro.setValoresDecimoTerceiro(vrdtm);
                        terceirizadoDecimoTerceiro.setParcelas(resultSet.getInt("PARCELA"));
                        terceirizadoDecimoTerceiro.setNomeCargo(resultSet.getString("FUNÇÃO"));
                        status = avaliaStatus(2, resultSet.getString("AUTORIZADO"), null);
                        DecimoTerceiroPendenteModel decimoTerceiroPendenteModel = new DecimoTerceiroPendenteModel(resultSet.getInt("CODIGO"), terceirizadoDecimoTerceiro, status,
                                resultSet.getString("OBSERVACAO"));
                        lista.add(decimoTerceiroPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new  RuntimeException("");
            }
        }
        return lista;
    }

    public List<DecimoTerceiroPendenteModel> getCalculosPendentesExecucao(int codigoContrato, int codigoUsuario) {
        List<DecimoTerceiroPendenteModel> lista = new ArrayList<>();
        int codigo = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        String status = "";
        if(codigo == codGestor) {
            String sql = "SELECT RDT.COD_TERCEIRIZADO_CONTRATO, " +
                    " T.NOME as TERCEIRIZADO," +
                    " TR.NOME as TIPO," +
                    " TF.NOME AS \"FUNÇÃO\"," +
                    " RDT.PARCELA," +
                    " RDT.DATA_INICIO_CONTAGEM," +
                    " RDT.VALOR," +
                    " RDT.INCIDENCIA_SUBMODULO_4_1 AS INCIDENCIA," +
                    " RDT.DATA_REFERENCIA," +
                    " RDT.AUTORIZADO," +
                    " RDT.RESTITUIDO," +
                    " RDT.COD AS CODIGO," +
                    " RDT.OBSERVACAO" +
                    " FROM tb_terceirizado_contrato TC" +
                    " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                    " JOIN tb_restituicao_decimo_terceiro RDT ON RDT.COD_TERCEIRIZADO_CONTRATO =TC.COD_TERCEIRIZADO" +
                    " JOIN tb_tipo_restituicao TR ON TR.COD=RDT.COD_TIPO_RESTITUICAO " +
                    " JOIN tb_funcao_terceirizado FT ON FT.COD_TERCEIRIZADO_CONTRATO= TC.cod" +
                    " JOIN tb_funcao_contrato FC ON FC.COD=FT.COD_FUNCAO_CONTRATO" +
                    " JOIN tb_funcao TF  ON TF.COD=FC.COD_FUNCAO" +
                    " WHERE TC.COD_CONTRATO = ? AND T.ATIVO = 'S' AND (AUTORIZADO='s' OR AUTORIZADO='S') AND (RESTITUIDO IS NULL)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"),
                                resultSet.getString("TERCEIRIZADO"), resultSet.getDate("DATA_INICIO_CONTAGEM"),0, 0 );
                        terceirizadoDecimoTerceiro.setTipoRestituicao(resultSet.getString("TIPO"));
                        ValorRestituicaoDecimoTerceiroModel vrdtm = new ValorRestituicaoDecimoTerceiroModel(resultSet.getFloat("VALOR"), resultSet.getFloat("INCIDENCIA"));
                        terceirizadoDecimoTerceiro.setValoresDecimoTerceiro(vrdtm);
                        terceirizadoDecimoTerceiro.setParcelas(resultSet.getInt("PARCELA"));
                        terceirizadoDecimoTerceiro.setNomeCargo(resultSet.getString("FUNÇÃO"));
                        status = avaliaStatus(3, resultSet.getString("AUTORIZADO"), resultSet.getString("RESTITUIDO"));
                        DecimoTerceiroPendenteModel decimoTerceiroPendenteModel = new DecimoTerceiroPendenteModel(resultSet.getInt("CODIGO"), terceirizadoDecimoTerceiro, status,
                                resultSet.getString("OBSERVACAO"));
                        lista.add(decimoTerceiroPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new  RuntimeException("" + sqle.getMessage());
            }
        }
        return lista;
    }

    public List<DecimoTerceiroPendenteModel> getCalculosNaoPendentesNegados(int codigoContrato, int codigoUsuario) {
        List<DecimoTerceiroPendenteModel> lista = new ArrayList<>();
        int codigo = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        String status = "";
        if(codigo == codGestor) {
            String sql = "SELECT RDT.COD_TERCEIRIZADO_CONTRATO, " +
                    " T.NOME as TERCEIRIZADO," +
                    " TR.NOME as TIPO," +
                    " TF.NOME AS \"FUNÇÃO\"," +
                    " RDT.PARCELA," +
                    " RDT.DATA_INICIO_CONTAGEM," +
                    " RDT.VALOR," +
                    " RDT.INCIDENCIA_SUBMODULO_4_1 AS INCIDENCIA," +
                    " RDT.DATA_REFERENCIA," +
                    " RDT.AUTORIZADO," +
                    " RDT.RESTITUIDO," +
                    " RDT.COD AS CODIGO," +
                    " RDT.OBSERVACAO" +
                    " FROM tb_terceirizado_contrato TC" +
                    " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                    " JOIN tb_restituicao_decimo_terceiro RDT ON RDT.COD_TERCEIRIZADO_CONTRATO =TC.COD_TERCEIRIZADO" +
                    " JOIN tb_tipo_restituicao TR ON TR.COD=RDT.COD_TIPO_RESTITUICAO " +
                    " JOIN tb_funcao_terceirizado FT ON FT.COD_TERCEIRIZADO_CONTRATO= TC.cod" +
                    " JOIN tb_funcao_contrato FC ON FC.COD=FT.COD_FUNCAO_CONTRATO" +
                    " JOIN tb_funcao TF  ON TF.COD=FC.COD_FUNCAO" +
                    " WHERE TC.COD_CONTRATO = ? AND T.ATIVO = 'S' AND (AUTORIZADO='s' OR AUTORIZADO='S') AND (RESTITUIDO='n' OR RESTITUIDO='N')";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"),
                                resultSet.getString("TERCEIRIZADO"), resultSet.getDate("DATA_INICIO_CONTAGEM"),0, 0 );
                        terceirizadoDecimoTerceiro.setTipoRestituicao(resultSet.getString("TIPO"));
                        ValorRestituicaoDecimoTerceiroModel vrdtm = new ValorRestituicaoDecimoTerceiroModel(resultSet.getFloat("VALOR"), resultSet.getFloat("INCIDENCIA"));
                        terceirizadoDecimoTerceiro.setValoresDecimoTerceiro(vrdtm);
                        terceirizadoDecimoTerceiro.setParcelas(resultSet.getInt("PARCELA"));
                        terceirizadoDecimoTerceiro.setNomeCargo(resultSet.getString("FUNÇÃO"));
                        status = avaliaStatus(4, resultSet.getString("AUTORIZADO"), resultSet.getString("RESTITUIDO"));
                        DecimoTerceiroPendenteModel decimoTerceiroPendenteModel = new DecimoTerceiroPendenteModel(resultSet.getInt("CODIGO"), terceirizadoDecimoTerceiro, status,
                                resultSet.getString("OBSERVACAO"));
                        lista.add(decimoTerceiroPendenteModel);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new  RuntimeException("" + sqle.getMessage());
            }
        }
        return lista;
    }

    public boolean executarCalculos(AvaliacaoDecimoTerceiro avaliacaoDecimoTerceiro) {
        int codigo = new UsuarioDAO(connection).verifyPermission(avaliacaoDecimoTerceiro.getUser().getId(), avaliacaoDecimoTerceiro.getCodigoContrato());
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(avaliacaoDecimoTerceiro.getUser().getId(), avaliacaoDecimoTerceiro.getCodigoContrato());
        if(codGestor == codigo) {
            String sql = "UPDATE TB_RESTITUICAO_DECIMO_TERCEIRO SET RESTITUIDO=?, OBSERVACAO=? WHERE COD_TERCEIRIZADO_CONTRATO=? AND COD=?";
            List<DecimoTerceiroPendenteModel> lista = avaliacaoDecimoTerceiro.getDecimosTerceirosPendentes();
            return atualizaCalculos(sql, lista);
        }
        return false;
    }

    private boolean atualizaCalculos(String sql, List<DecimoTerceiroPendenteModel> lista) {
        for (DecimoTerceiroPendenteModel decimoTerceiroPendenteModel : lista){
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, decimoTerceiroPendenteModel.getStatus());
                preparedStatement.setString(2, decimoTerceiroPendenteModel.getObservacoes());
                preparedStatement.setInt(3, decimoTerceiroPendenteModel.getTerceirizadoDecTer().getCodigoTerceirizadoContrato());
                preparedStatement.setInt(4, decimoTerceiroPendenteModel.getCod());
                preparedStatement.executeUpdate();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new RuntimeException("" + sqle.getMessage());
            }
        }
        return true;
    }

    public List<TerceirizadoDecimoTerceiro> getRestituicoes(int codigoContrato, int codigoUsuario) {
        int codigo = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        List<TerceirizadoDecimoTerceiro> lista = new ArrayList<>();
        if(codigo == codGestor) {
            String sql = "SELECT RDT.COD_TERCEIRIZADO_CONTRATO," +
                    " T.NOME as TERCEIRIZADO," +
                    " TR.NOME as TIPO," +
                    " TF.NOME AS FUNÇÃO," +
                    " RDT.PARCELA," +
                    " RDT.DATA_INICIO_CONTAGEM," +
                    " RDT.VALOR," +
                    " RDT.INCIDENCIA_SUBMODULO_4_1 AS INCIDENCIA," +
                    " RDT.DATA_REFERENCIA," +
                    " RDT.AUTORIZADO," +
                    " RDT.RESTITUIDO," +
                    " RDT.COD AS CODIGO," +
                    " RDT.OBSERVACAO" +
                    " FROM tb_terceirizado_contrato TC" +
                    " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO" +
                    " JOIN tb_restituicao_decimo_terceiro RDT ON RDT.COD_TERCEIRIZADO_CONTRATO =TC.COD" +
                    " JOIN tb_tipo_restituicao TR ON TR.COD=RDT.COD_TIPO_RESTITUICAO" +
                    " JOIN tb_funcao_terceirizado FT ON FT.COD_TERCEIRIZADO_CONTRATO= TC.cod" +
                    " JOIN tb_funcao_contrato FC ON FC.COD=FT.COD_FUNCAO_CONTRATO" +
                    " JOIN tb_funcao TF  ON TF.COD=FC.COD_FUNCAO" +
                    " WHERE TC.COD_CONTRATO = ? AND T.ATIVO = 'S' AND (AUTORIZADO='s' OR AUTORIZADO='S') AND (RESTITUIDO='s' OR RESTITUIDO='S')";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TerceirizadoDecimoTerceiro terceirizadoDecimoTerceiro = new TerceirizadoDecimoTerceiro(resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"),
                                resultSet.getString("TERCEIRIZADO"), resultSet.getDate("DATA_INICIO_CONTAGEM"),0, 0 );
                        terceirizadoDecimoTerceiro.setTipoRestituicao(resultSet.getString("TIPO"));
                        ValorRestituicaoDecimoTerceiroModel vrdtm = new ValorRestituicaoDecimoTerceiroModel(resultSet.getFloat("VALOR"), resultSet.getFloat("INCIDENCIA"));
                        terceirizadoDecimoTerceiro.setValoresDecimoTerceiro(vrdtm);
                        terceirizadoDecimoTerceiro.setParcelas(resultSet.getInt("PARCELA"));
                        terceirizadoDecimoTerceiro.setNomeCargo(resultSet.getString("FUNÇÃO"));
                        lista.add(terceirizadoDecimoTerceiro);
                    }
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new  RuntimeException("" + sqle.getMessage());
            }
        }
        return lista;
    }
}