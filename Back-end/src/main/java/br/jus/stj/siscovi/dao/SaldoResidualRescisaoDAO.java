package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.SaldoResidualRescisao;
import br.jus.stj.siscovi.model.SaldoResidualRestituidoFerias;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaldoResidualRescisaoDAO {

    private final Connection connection;

    public SaldoResidualRescisaoDAO (Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoResidualRescisao> getSaldoResidualRescisaoRestituido (int pCodContrato) {

        ArrayList<SaldoResidualRescisao> lista = new ArrayList<>();

        String sql = "SELECT t.nome, " +
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

                                new SaldoResidualRescisao(resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getFloat(3),
                                        resultSet.getFloat(4),
                                        resultSet.getFloat(5),
                                        resultSet.getFloat(6),
                                        resultSet.getFloat(7),
                                        resultSet.getFloat(8),
                                        resultSet.getFloat(9),
                                        resultSet.getFloat(3) + resultSet.getFloat(4) + resultSet.getFloat(5) + resultSet.getFloat(6) +resultSet.getFloat(7)+ resultSet.getFloat(8) + resultSet.getFloat(9),
                                        resultSet.getString(10));

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


}
