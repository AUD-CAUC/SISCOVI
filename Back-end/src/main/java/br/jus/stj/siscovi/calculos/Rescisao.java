package br.jus.stj.siscovi.calculos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Rescisao {
    private Connection connection;

    public Rescisao(Connection connection) {
        this.connection = connection;
    }

    public boolean RetornaStatusAnalise (int pCodTerceirizadoContrato) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vNumeroRestituicoes = 0;

        String query = "SELECT COUNT(cod)\n" +
                "FROM tb_restituicao_rescisao\n" +
                "WHERE cod_terceirizado_contrato = ?\n" +
                "AND ((AUTORIZADO IS NULL AND RESTITUIDO IS NULL) OR (AUTORIZADO = 'S' AND RESTITUIDO IS NULL) OR (AUTORIZADO = 'S' AND RESTITUIDO = 'N'));";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vNumeroRestituicoes = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar restituições de férias anteriores.");

        }

        if (vNumeroRestituicoes == 0) {

            return false;

        }

        return true;
    }
}
