package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.SaldoIndividualContaVinculadaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaldoIndividualContaVinculadaDAO {

    private final Connection connection;

    public SaldoIndividualContaVinculadaDAO (Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoIndividualContaVinculadaModel> getSaldoIndividualContaVinculada (int pCodContrato) {

        ArrayList<SaldoIndividualContaVinculadaModel> lista = new ArrayList<>();

        String sql = "SELECT  t.nome," +
                "  t.cpf," +
                "  tc.cod" +
                " FROM tb_terceirizado t" +
                "  JOIN tb_terceirizado_contrato tc ON tc.cod_terceirizado = t.cod" +
                " WHERE tc.cod_contrato = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                resultSet.next();

                Saldo saldoContaVinculada = new Saldo(connection);

                do {

                    SaldoIndividualContaVinculadaModel saldoContaVinculadaModel =

                            new SaldoIndividualContaVinculadaModel(resultSet.getString(1),
                                    resultSet.getString(2),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3), 1,1),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,2),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,3),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,7),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,5),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,1),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,2),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,101),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,102),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,100),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,3),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,103),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,100),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,100),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,100) + saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3), 3,100),
                                    saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,100) - (saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3), 2,100) + saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,100)));

                    lista.add(saldoContaVinculadaModel);

                } while (resultSet.next());

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo da conta vinculada.");

        }

        return lista;

    }

}