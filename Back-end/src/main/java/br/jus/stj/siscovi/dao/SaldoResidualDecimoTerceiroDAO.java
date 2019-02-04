package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.SaldoResidualRestituidoDecimoTerceiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaldoResidualDecimoTerceiroDAO {

    private final Connection connection;

    public SaldoResidualDecimoTerceiroDAO (Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoResidualRestituidoDecimoTerceiro> getSaldoResidualDecimoTerceiroRestituido (int pCodContrato) {

        ArrayList<SaldoResidualRestituidoDecimoTerceiro> lista = new ArrayList<>();

        String sql = "SELECT t.nome, " +
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

                                new SaldoResidualRestituidoDecimoTerceiro(resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getFloat(3),
                                        resultSet.getFloat(4),
                                        resultSet.getFloat(3) + resultSet.getFloat(4),
                                        resultSet.getString(5));

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

}
