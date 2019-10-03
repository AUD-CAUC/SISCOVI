package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.SaldoFuncaoContaVinculadaModel;

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

                    float valorFeriasRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,1);
                    float valorTercoRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,2);
                    float valorDecimoTerceiroRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,3);
                    float valorIncidenciaRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,7);
                    float valorMultaFGTSRetido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,5);
                    float valorTotalRetido = valorFeriasRetido + valorTercoRetido + valorDecimoTerceiroRetido + valorIncidenciaRetido + valorMultaFGTSRetido;

                    float valorFeriasRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,1);
                    float valorIncidenciaFeriasRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,101);
                    float valorTotalFeriasRestituido = valorFeriasRestituido + valorIncidenciaFeriasRestituido;

                    float valorTercoRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,2);
                    float valorIncidenciaTercoRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,102);
                    float valorTotalTercoRestituido = valorTercoRestituido + valorIncidenciaTercoRestituido;

                    float valorDecimoTerceiroRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,3);
                    float valorIncidenciaDecimoTerceiroRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,103);
                    float valorTotalDecimoTerceiroRestituido = valorDecimoTerceiroRestituido + valorIncidenciaDecimoTerceiroRestituido;

                    float valorMultaFGTSRestituido = saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,5);
                    float valorTotalRestituido = valorTotalFeriasRestituido + valorTotalDecimoTerceiroRestituido + valorTotalTercoRestituido + valorMultaFGTSRestituido;
                    float valorSaldo = valorTotalRetido - valorTotalRestituido;

                    SaldoFuncaoContaVinculadaModel saldoFuncaoContaVinculadaModel =

                            new SaldoFuncaoContaVinculadaModel(resultSet.getString(4),
                                    valorFeriasRetido,
                                    valorTercoRetido,
                                    valorDecimoTerceiroRetido,
                                    valorIncidenciaRetido,
                                    valorMultaFGTSRetido,
                                    valorMultaFGTSRestituido,
                                    valorTotalRetido,
                                    valorFeriasRestituido,
                                    valorTercoRestituido,
                                    valorDecimoTerceiroRestituido,
                                    valorTotalRestituido,
                                    valorIncidenciaFeriasRestituido,
                                    valorIncidenciaTercoRestituido,
                                    valorTotalFeriasRestituido,
                                    valorIncidenciaDecimoTerceiroRestituido,
                                    valorTotalDecimoTerceiroRestituido,
                                    valorSaldo);

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
