package br.jus.stj.siscovi.dao;


import br.jus.stj.siscovi.model.TerceirizadoRescisao;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.*;

public class RescisaoDAO {

    private Connection connection;

    public RescisaoDAO (Connection connection) {

        this.connection = connection;

    }

    /**
     *
     * @param codigoContrato
     * @return
     */

    public ArrayList<TerceirizadoRescisao> getListaTerceirizadoParaCalculoDeRescisao (int codigoContrato) {

        ArrayList<TerceirizadoRescisao> terceirizados = new ArrayList<>();

        String sql = "SELECT TC.COD," +
                           " T.NOME" +
                      " FROM tb_terceirizado_contrato TC " +
                        " JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO " +
                      " WHERE COD_CONTRATO = ? AND T.ATIVO = 'S'";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, codigoContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    TerceirizadoRescisao terceirizadoRescisao = new TerceirizadoRescisao(resultSet.getInt("COD"),
                                                                                         resultSet.getString("NOME"));

                    terceirizados.add(terceirizadoRescisao);

                }

            } catch (SQLException sqle) {

                throw new NullPointerException("Erro ao recuperar funcionários do contrato.");

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro ao buscar funcionários ativos no contrato.");

        }

        return terceirizados;

    }

}
