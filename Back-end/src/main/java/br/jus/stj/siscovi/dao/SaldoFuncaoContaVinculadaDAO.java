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

                do {

                    Saldo saldoContaVinculada = new Saldo(connection);

                    SaldoFuncaoContaVinculadaModel saldoFuncaoContaVinculadaModel =

                            new SaldoFuncaoContaVinculadaModel(resultSet.getString(4),
                                    //resultSet.getString(1),
                                    //resultSet.getString(2),
                                    //resultSet.getString(3),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,1),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,2),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,3),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,7),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,5),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,100),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,1),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,2),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,3),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,100) + saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,100),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,101),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,102),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,100),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,103),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,100),
                                    saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 1,100) - (saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 2,100) + saldoContaVinculada.getSaldoFuncaoContaVinculada(pCodContrato, resultSet.getInt(6), 3,100)));

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
