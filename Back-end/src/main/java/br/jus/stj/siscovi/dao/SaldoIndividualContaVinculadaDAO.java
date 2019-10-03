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
                    
                    float feriasRetido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3), 1,1);
                    float tercoRetido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,2);
                    float decimoTerceiroRetido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,3);
                    float incidenciaRetido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,7);
                    float multaFgtsRetido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  1,5);
                    float totalRetido = feriasRetido + tercoRetido + decimoTerceiroRetido + incidenciaRetido + multaFgtsRetido;

                    float feriasRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,1);
                    float incidenciaFeriasRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,101);
                    float totalFeriasRestituido = feriasRestituido + incidenciaFeriasRestituido;

                    float tercoRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,2);
                    float incidenciaTercoRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,102);
                    float totalTercoRestituido = tercoRestituido + incidenciaTercoRestituido;

                    float decimoTerceiroRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,3);
                    float incidenciaDecimoTerceiroRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,103);
                    float totalDecimoTerceiroRestituido = decimoTerceiroRestituido + incidenciaDecimoTerceiroRestituido;
                    float multaFgtsRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3, 5);

                    float totalRestituido = totalFeriasRestituido + totalDecimoTerceiroRestituido + totalTercoRestituido + multaFgtsRestituido;

                    float saldo = totalRetido - totalRestituido;

                    SaldoIndividualContaVinculadaModel saldoContaVinculadaModel =

                            new SaldoIndividualContaVinculadaModel(resultSet.getString(1),
                                    resultSet.getString(2),
                                    feriasRetido,
                                    tercoRetido,
                                    decimoTerceiroRetido,
                                    incidenciaRetido,
                                    multaFgtsRetido,
                                    multaFgtsRestituido,
                                    feriasRestituido,
                                    tercoRestituido,
                                    incidenciaFeriasRestituido,
                                    incidenciaTercoRestituido,
                                    totalFeriasRestituido,
                                    decimoTerceiroRestituido,
                                    incidenciaDecimoTerceiroRestituido,
                                    totalDecimoTerceiroRestituido,
                                    totalRetido,
                                    totalRestituido,
                                    saldo);

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