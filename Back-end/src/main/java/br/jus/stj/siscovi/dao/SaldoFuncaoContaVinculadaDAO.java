package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.SaldoFuncaoContaVinculadaModel;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

public class SaldoFuncaoContaVinculadaDAO {

    private final Connection connection;

    public SaldoFuncaoContaVinculadaDAO(Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoFuncaoContaVinculadaModel> getSaldoContaVinculadaFuncao (int pCodContrato, int pCodUsuario) {

        ArrayList<SaldoFuncaoContaVinculadaModel> lista = new ArrayList<>();
        int vCodGestor = new ContratoDAO(connection).codigoGestorContrato(pCodUsuario, pCodContrato);

        String sql = "SELECT  u.nome," +
                "  c.nome_empresa," +
                "  c.numero_contrato," +
                "  f.nome ," +
                "  c.cod ," +
                "  fc.cod" +
                " FROM tb_funcao_contrato fc" +
                "  JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                "  JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                "  JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                "  JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                " WHERE c.cod = ?" +
                "      AND hgc.cod_usuario = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, vCodGestor);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                resultSet.next();

                Saldo saldoContaVinculada = new Saldo(connection);
                
                do {

                    float feriasRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,1);
                    float tercoRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,2);
                    float decimoTerceiroRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,3);
                    float incidenciaRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,7);
                    float multaFgtsRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,5);
                    float totalRetido = new BigDecimal(Float.toString(feriasRetido)).add(new BigDecimal(Float.toString(tercoRetido))).add(new BigDecimal(Float.toString(decimoTerceiroRetido)))
                            .add(new BigDecimal(Float.toString(incidenciaRetido))).add(new BigDecimal(Float.toString(multaFgtsRetido))).floatValue();

                    float feriasRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,1);
                    float incidenciaFeriasRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,101);
                    float totalFeriasRestituido = new BigDecimal(Float.toString(feriasRestituido)).add(new BigDecimal(Float.toString(incidenciaFeriasRestituido))).floatValue();

                    float tercoRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,2);
                    float incidenciaTercoRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,102);
                    float totalTercoRestituido = new BigDecimal(Float.toString(tercoRestituido)).add(new BigDecimal(Float.toString(incidenciaTercoRestituido))).floatValue();

                    float decimoTerceiroRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,3);
                    float incidenciaDecimoTerceiroRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,103);
                    float totalDecimoTerceiroRestituido = new BigDecimal(Float.toString(decimoTerceiroRestituido)).add(new BigDecimal(Float.toString(incidenciaDecimoTerceiroRestituido))).floatValue();

                    float multaFgtsRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,5);
                    float totalRestituido = new BigDecimal(Float.toString(totalFeriasRestituido)).add(new BigDecimal(Float.toString(totalDecimoTerceiroRestituido)))
                            .add(new BigDecimal(Float.toString(totalTercoRestituido))).add(new BigDecimal(Float.toString(multaFgtsRestituido))).floatValue();

                    float saldoIncidencia = new BigDecimal(Float.toString(incidenciaRetido)).subtract(new BigDecimal(Float.toString(incidenciaFeriasRestituido))).subtract(new BigDecimal(Float.toString(incidenciaTercoRestituido)))
                            .subtract(new BigDecimal(Float.toString(incidenciaDecimoTerceiroRestituido))).floatValue();

                    float saldo = new BigDecimal(Float.toString(totalRetido)).subtract(new BigDecimal(Float.toString(totalRestituido))).floatValue();

                    SaldoFuncaoContaVinculadaModel saldoFuncaoContaVinculadaModel =

                            new SaldoFuncaoContaVinculadaModel(resultSet.getString(4),
                                    feriasRetido,
                                    tercoRetido,
                                    decimoTerceiroRetido,
                                    incidenciaRetido,
                                    multaFgtsRetido,
                                    multaFgtsRestituido,
                                    totalRetido,
                                    feriasRestituido,
                                    tercoRestituido,
                                    decimoTerceiroRestituido,
                                    totalRestituido,
                                    incidenciaFeriasRestituido,
                                    incidenciaTercoRestituido,
                                    totalFeriasRestituido,
                                    incidenciaDecimoTerceiroRestituido,
                                    totalDecimoTerceiroRestituido,
                                    saldoIncidencia,
                                    saldo);

                    lista.add(saldoFuncaoContaVinculadaModel);

                } while (resultSet.next());

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo da conta vinculada.");

        }

        return lista;

    }

}
