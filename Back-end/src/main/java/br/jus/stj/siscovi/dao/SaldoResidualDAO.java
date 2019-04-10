package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaldoResidualDAO {

    private final Connection connection;

    public SaldoResidualDAO (Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoResidualRestituidoFerias> getSaldoResidualFeriasRestituido (int pCodContrato) {

        ArrayList<SaldoResidualRestituidoFerias> lista = new ArrayList<>();

        String sql = "SELECT ft.cod_terceirizado_contrato," +
                "t.nome, " +
                "t.cpf, " +
                "srf.valor_ferias, " +
                "srf.valor_terco, " +
                "srf.incid_submod_4_1_ferias, " +
                "srf.incid_submod_4_1_terco " +
                // "CASE WHEN srf.restituido = 'S' THEN 'SIM' ELSE 'NÃO' END " +
                "FROM tb_restituicao_ferias rt " +
                "JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato " +
                "JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod " +
                "JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado " +
                "JOIN tb_contrato c ON c.cod = tc.cod_contrato " +
                "JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao " +
                "JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato " +
                "JOIN tb_funcao f ON f.cod = fc.cod_funcao " +
                "JOIN tb_saldo_residual_ferias srf ON srf.cod_restituicao_ferias = rt.cod " +
                "WHERE c.cod = ? " +
                "AND ft.data_inicio = (SELECT MAX(data_inicio) " +
                "FROM tb_funcao_terceirizado " +
                "WHERE cod_terceirizado_contrato = tc.cod) AND srf.PEDIDO IS NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    do {

                        SaldoResidualRestituidoFerias saldo =

                                new SaldoResidualRestituidoFerias(resultSet.getInt(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3),
                                        resultSet.getFloat(4),
                                        resultSet.getFloat(5),
                                        resultSet.getFloat(6),
                                        resultSet.getFloat(7),
                                        resultSet.getFloat(4) + resultSet.getFloat(5) + resultSet.getFloat(6) + resultSet.getFloat(7));

                        lista.add(saldo);

                    } while (resultSet.next());

                }

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo residual de férias da conta vinculada.");

        }

        return lista;

    }

    public ArrayList<SaldoResidualRestituidoDecimoTerceiro> getSaldoResidualDecimoTerceiroRestituido (int pCodContrato) {

        ArrayList<SaldoResidualRestituidoDecimoTerceiro> lista = new ArrayList<>();

        String sql = "SELECT ft.cod_terceirizado_contrato," +
                "t.nome, " +
                "t.cpf, " +
                "srdt.valor, " +
                "srdt.incidencia_submodulo_4_1, " +
                "CASE WHEN srdt.restituido = 'S' THEN 'SIM' ELSE 'NÃO' END " +
                "FROM tb_restituicao_decimo_terceiro rt " +
                "JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato " +
                "JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod " +
                "JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado " +
                "JOIN tb_contrato c ON c.cod = tc.cod_contrato " +
                "JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao " +
                "JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato " +
                "JOIN tb_funcao f ON f.cod = fc.cod_funcao " +
                "JOIN tb_saldo_residual_dec_ter srdt ON srdt.cod_restituicao_dec_terceiro = rt.cod " +
                "WHERE c.cod = ? " +
                "AND ft.data_inicio = (SELECT MAX(data_inicio) " +
                "FROM tb_funcao_terceirizado " +
                "WHERE cod_terceirizado_contrato = tc.cod)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    do {

                        SaldoResidualRestituidoDecimoTerceiro saldo =

                                new SaldoResidualRestituidoDecimoTerceiro(resultSet.getInt(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3),
                                        resultSet.getFloat(4),
                                        resultSet.getFloat(5),
                                        resultSet.getFloat(4) + resultSet.getFloat(5),
                                        resultSet.getString(6));

                        lista.add(saldo);

                    } while (resultSet.next());

                }

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo residual de décimo terceiro da conta vinculada.");

        }

        return lista;

    }

    public ArrayList<SaldoResidualRescisao> getSaldoResidualRescisaoRestituido (int pCodContrato) {

        ArrayList<SaldoResidualRescisao> lista = new ArrayList<>();

        String sql = "SELECT ft.cod_terceirizado_contrato," +
                "t.nome, " +
                "t.cpf, " +
                "srr.incid_submod_4_1_dec_terceiro, " +
                "srr.incid_multa_fgts_dec_terceiro, " +
                "srr.incid_submod_4_1_ferias, " +
                "srr.incid_submod_4_1_terco, " +
                "srr.incid_multa_fgts_ferias, " +
                "srr.incid_multa_fgts_terco, " +
                "srr.multa_fgts_salario, " +
                "CASE WHEN srr.restituido = 'S' THEN 'SIM' ELSE 'NÃO' END " +
                "FROM tb_restituicao_rescisao rr " +
                "JOIN tb_terceirizado_contrato tc ON tc.cod = rr.cod_terceirizado_contrato " +
                "JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod " +
                "JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado " +
                "JOIN tb_contrato c ON c.cod = tc.cod_contrato " +
                "JOIN tb_tipo_restituicao tr ON tr.cod = rr.cod_tipo_restituicao " +
                "JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod " +
                "JOIN tb_usuario u ON u.cod = hgc.cod_usuario " +
                "JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato " +
                "JOIN tb_funcao f ON f.cod = fc.cod_funcao " +
                "JOIN tb_saldo_residual_rescisao srr ON srr.cod_restituicao_rescisao = rr.cod " +
                "WHERE c.cod = ? " +
                "AND ft.data_inicio = (SELECT MAX(data_inicio) " +
                "FROM tb_funcao_terceirizado " +
                "WHERE cod_terceirizado_contrato = tc.cod)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    do {

                        SaldoResidualRescisao saldo =

                                new SaldoResidualRescisao(resultSet.getInt(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3),
                                        resultSet.getFloat(4),
                                        resultSet.getFloat(5),
                                        resultSet.getFloat(6),
                                        resultSet.getFloat(7),
                                        resultSet.getFloat(8),
                                        resultSet.getFloat(9),
                                        resultSet.getFloat(10),
                                        resultSet.getFloat(4) + resultSet.getFloat(5) + resultSet.getFloat(6) + resultSet.getFloat(7) +resultSet.getFloat(8)+ resultSet.getFloat(9) + resultSet.getFloat(10),
                                        resultSet.getString(11));

                        lista.add(saldo);

                    } while (resultSet.next());

                }

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo residual de rescisão da conta vinculada.");

        }

        return lista;

    }

    public List<SaldoResidualRestituidoFerias> getCalculosPendentesFerias(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<SaldoResidualRestituidoFerias> lista = new ArrayList<>();
            String sql = "SELECT ft.cod_terceirizado_contrato," +
                    "t.nome, " +
                    "t.cpf, " +
                    "srf.valor_ferias, " +
                    "srf.valor_terco, " +
                    "srf.incid_submod_4_1_ferias, " +
                    "srf.incid_submod_4_1_terco " +
//                    "CASE WHEN srf.restituido = 'S' THEN 'SIM' ELSE 'NÃO' END " +
                    "FROM tb_restituicao_ferias rt " +
                    "JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato " +
                    "JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod " +
                    "JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado " +
                    "JOIN tb_contrato c ON c.cod = tc.cod_contrato " +
                    "JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao " +
                    "JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato " +
                    "JOIN tb_funcao f ON f.cod = fc.cod_funcao " +
                    "JOIN tb_saldo_residual_ferias srf ON srf.cod_restituicao_ferias = rt.cod " +
                    "WHERE c.cod = ? " +
                    "AND ft.data_inicio = (SELECT MAX(data_inicio) " +
                    "FROM tb_funcao_terceirizado " +
                    "WHERE cod_terceirizado_contrato = tc.cod)" +
                    "AND ((srf.AUTORIZADO IS NULL) OR (srf.RESTITUIDO = 'N' AND srf.AUTORIZADO = 'S')) AND srf.PEDIDO IS NOT NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, codigoContrato);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()) {

                        do {

                            SaldoResidualRestituidoFerias saldo =

                                    new SaldoResidualRestituidoFerias(resultSet.getInt(1),
                                            resultSet.getString(2),
                                            resultSet.getString(3),
                                            resultSet.getFloat(4),
                                            resultSet.getFloat(5),
                                            resultSet.getFloat(6),
                                            resultSet.getFloat(7),
                                            resultSet.getFloat(4) + resultSet.getFloat(5) + resultSet.getFloat(6) + resultSet.getFloat(7));

                            lista.add(saldo);

                        } while (resultSet.next());

                    }

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Falha na aquisição do saldo residual de férias da conta vinculada.");

            }

            return lista;
        }else {
            throw new RuntimeException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    public List<SaldoResidualRestituidoDecimoTerceiro> getCalculosPendentesDecTer(int codigoContrato, int codigoUsuario) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        if(codigoGestor == codGestor) {
            List<SaldoResidualRestituidoDecimoTerceiro> lista = new ArrayList<>();
            String sql = "SELECT ft.cod_terceirizado_contrato," +
                    "t.nome, " +
                    "t.cpf, " +
                    "srdt.valor, " +
                    "srdt.incidencia_submodulo_4_1, " +
                    "CASE WHEN srdt.restituido = 'S' THEN 'SIM' ELSE 'NÃO' END " +
                    "FROM tb_restituicao_decimo_terceiro rt " +
                    "JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato " +
                    "JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod " +
                    "JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado " +
                    "JOIN tb_contrato c ON c.cod = tc.cod_contrato " +
                    "JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao " +
                    "JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato " +
                    "JOIN tb_funcao f ON f.cod = fc.cod_funcao " +
                    "JOIN tb_saldo_residual_dec_ter srdt ON srdt.cod_restituicao_dec_terceiro = rt.cod " +
                    "WHERE c.cod = ? " +
                    "AND ft.data_inicio = (SELECT MAX(data_inicio) " +
                    "FROM tb_funcao_terceirizado " +
                    "WHERE cod_terceirizado_contrato = tc.cod)" +
                    "AND ((srdt.AUTORIZADO IS NULL) OR (srdt.RESTITUIDO = 'N' AND srdt.AUTORIZADO = 'S')) AND srdt.PEDIDO IS NOT NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, codigoContrato);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()) {

                        do {

                            SaldoResidualRestituidoDecimoTerceiro saldo =

                                    new SaldoResidualRestituidoDecimoTerceiro(resultSet.getInt(1),
                                            resultSet.getString(2),
                                            resultSet.getString(3),
                                            resultSet.getFloat(4),
                                            resultSet.getFloat(5),
                                            resultSet.getFloat(4) + resultSet.getFloat(5),
                                            resultSet.getString(6));

                            lista.add(saldo);

                        } while (resultSet.next());

                    }

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Falha na aquisição do saldo residual de décimo terceiro da conta vinculada.");

            }

            return lista;
        }else {
            throw new RuntimeException("A operação foi negada ! Este usuário não tem permissão para realizar esta operação.");
        }
    }

    public boolean confirmarFeriasResiduais(AvaliacaoResiduais avaliacaoResiduais) {

        ArrayList<Integer> lista = new ArrayList<>();
        String sql1 = "SELECT cod FROM tb_restituicao_ferias WHERE COD_TIPO_RESTITUICAO = 3 AND AUTORIZADO = 'S' AND RESTITUIDO = 'S' AND COD_TERCEIRIZADO_CONTRATO = ?";

        try(PreparedStatement preparedStatement1 = connection.prepareStatement(sql1)) {

            preparedStatement1.setInt(1, avaliacaoResiduais.getCodTerceirizadoContrato());

            try (ResultSet resultSet = preparedStatement1.executeQuery()) {

                if (resultSet.next()) {
                    do {
                        lista.add(resultSet.getInt(1));
                    } while (resultSet.next());
                }
            }
        } catch (SQLException sqle) {
            return false;
        }

        for (Integer cod_rest_ferias: lista) {
            String sql = "UPDATE tb_saldo_residual_ferias" +
                    " SET " +
                    " PEDIDO = 'S'," +
                    " LOGIN_ATUALIZACAO = ?," +
                    " DATA_ATUALIZACAO = CURRENT_TIMESTAMP" +
                    " WHERE COD_RESTITUICAO_FERIAS = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, avaliacaoResiduais.getUser().getUsername().toUpperCase());
                preparedStatement.setInt(2, cod_rest_ferias);
                preparedStatement.executeUpdate();

            }catch (SQLException sqle) {
                return false;
            }
        }
        

        return true;
    }
}
