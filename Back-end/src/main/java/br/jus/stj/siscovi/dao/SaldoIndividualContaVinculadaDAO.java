package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.SaldoIndividualContaVinculadaModel;

import java.math.BigDecimal;
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
                    float totalRetido = new BigDecimal(Float.toString(feriasRetido)).add(new BigDecimal(Float.toString(tercoRetido))).add(new BigDecimal(Float.toString(decimoTerceiroRetido)))
                            .add(new BigDecimal(Float.toString(incidenciaRetido))).add(new BigDecimal(Float.toString(multaFgtsRetido))).floatValue();

                    float feriasRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,1);
                    float incidenciaFeriasRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,101);
                    float totalFeriasRestituido = new BigDecimal(Float.toString(feriasRestituido)).add(new BigDecimal(Float.toString(incidenciaFeriasRestituido))).floatValue();

                    float tercoRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,2);
                    float incidenciaTercoRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  2,102);
                    float totalTercoRestituido = new BigDecimal(Float.toString(tercoRestituido)).add(new BigDecimal(Float.toString(incidenciaTercoRestituido))).floatValue();


                    float decimoTerceiroRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,3);
                    float incidenciaDecimoTerceiroRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3,103);
                    float totalDecimoTerceiroRestituido = new BigDecimal(Float.toString(decimoTerceiroRestituido)).add(new BigDecimal(Float.toString(incidenciaDecimoTerceiroRestituido))).floatValue();
                    float multaFgtsRestituido = saldoContaVinculada.getSaldoIndividualContaVinculada(resultSet.getInt(3),  3, 5);

                    float totalRestituido = new BigDecimal(Float.toString(totalFeriasRestituido)).add(new BigDecimal(Float.toString(totalDecimoTerceiroRestituido)))
                            .add(new BigDecimal(Float.toString(totalTercoRestituido))).add(new BigDecimal(Float.toString(multaFgtsRestituido))).floatValue();

                    float saldoIncidencia = new BigDecimal(Float.toString(incidenciaRetido)).add(new BigDecimal(Float.toString(incidenciaFeriasRestituido))).add(new BigDecimal(Float.toString(incidenciaTercoRestituido)))
                            .add(new BigDecimal(Float.toString(incidenciaDecimoTerceiroRestituido))).add(new BigDecimal(Float.toString(multaFgtsRestituido))).floatValue();

                    float saldo = new BigDecimal(Float.toString(totalRetido)).add(new BigDecimal(Float.toString(totalRestituido))).floatValue();

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