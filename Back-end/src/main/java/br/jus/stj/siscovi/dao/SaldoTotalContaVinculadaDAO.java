package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.model.SaldoTotalContaVinculadaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

public class SaldoTotalContaVinculadaDAO {

    private final Connection connection;

    public SaldoTotalContaVinculadaDAO(Connection connection) {

        this.connection = connection;

    }

    public ArrayList<SaldoTotalContaVinculadaModel> getSaldoContaVinculadaContrato (int pCodContrato, int pCodUsuario) {

        ArrayList<SaldoTotalContaVinculadaModel> lista = new ArrayList<>();
        int vCodGestor = new ContratoDAO(connection).codigoGestorContrato(pCodUsuario, pCodContrato);
        int vCodFuncaoContrato = 0;

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

                vCodFuncaoContrato = resultSet.getInt(6);

                do {

                    Saldo saldoContaVinculada = new Saldo(connection);

                    SaldoTotalContaVinculadaModel saldoTotalContaVinculadaModel =

                            new SaldoTotalContaVinculadaModel(resultSet.getString(4),
                                    //resultSet.getString(1),
                                    //resultSet.getString(2),
                                    //resultSet.getString(3),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,1),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,2),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,3),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,7),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,5),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,100),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,1),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,2),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 3,3),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,100) + saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 3,100),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,101),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,102),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,100),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 3,103),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 3,100),
                                    saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 1,100) - (saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 2,100) + saldoContaVinculada.getSaldoTotalContaVinculada(pCodContrato, vCodFuncaoContrato, 3,100)));

                    lista.add(saldoTotalContaVinculadaModel);

                } while (resultSet.next());

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Falha na aquisição do saldo da conta vinculada.");

        }

        return lista;

    }

}