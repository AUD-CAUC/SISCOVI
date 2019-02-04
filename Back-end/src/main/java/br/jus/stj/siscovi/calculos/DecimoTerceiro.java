package br.jus.stj.siscovi.calculos;

import java.sql.*;

public class DecimoTerceiro {

    private Connection connection;

    public DecimoTerceiro (Connection connection) {

        this.connection = connection;

    }

    /**
     * Função que retorna a data de inicio da contagem do décimo terceiro
     * de um terceirizado em um determinado contrato.
     * @param pCodTerceirizadoContrato
     */

    public Date RetornaDataInicioContagem (int pCodTerceirizadoContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Date vMaxDataCalculo = null;
        Date vDataRetorno = null;

        /* Seleciona a máxima data de início de contagem existente ou atribui valor nulo a variável. */

        try {

            preparedStatement = connection.prepareStatement("SELECT MAX(DATA_INICIO_CONTAGEM)" +
                    " FROM tb_restituicao_decimo_terceiro" +
                    " WHERE COD_TERCEIRIZADO_CONTRATO = ?" +
                    " AND PARCELA IN (0,2);");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vMaxDataCalculo = resultSet.getDate(1);

                if (vMaxDataCalculo != null) {

                    vDataRetorno = Date.valueOf((vMaxDataCalculo.toLocalDate().plusYears(1).withDayOfYear(1)));

                }

            } else {

                vMaxDataCalculo = null;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Falha na consulta ao Banco de Dados.");

        }

        if (vMaxDataCalculo == null) {

            try {

                preparedStatement = connection.prepareStatement("SELECT DATA_DISPONIBILIZACAO\n" +
                        " FROM tb_terceirizado_contrato\n" +
                        " WHERE cod = ?;");

                preparedStatement.setInt(1, pCodTerceirizadoContrato);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vDataRetorno = resultSet.getDate(1);

                } else {

                    throw new NullPointerException("Data de disponibilização do terceirizado não encontrada.");

                }

            } catch (SQLException e) {


            }

        }

        return vDataRetorno;

    }

}