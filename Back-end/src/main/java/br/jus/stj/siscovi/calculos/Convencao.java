package br.jus.stj.siscovi.calculos;

import java.sql.*;

public class Convencao {

    private Connection connection;

    Convencao (Connection connection) {

        this.connection = connection;

    }

    //Essa função está depreciada.

    public int RetornaConvencaoAnterior (int codConvencao) throws SQLException {

        int codConvencaoAnterior = 0;
        int codCargoContrato = 0;
        Date dataRef = null;
        ResultSet rs;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COD_CARGO_CONTRATO," +
                                                                                     " DATA_INICIO_CONVENCAO" +
                                                                               " FROM TB_CONVENCAO_COLETIVA" +
                                                                               " WHERE COD = ?");

        preparedStatement.setInt(1,codConvencao);
        rs = preparedStatement.executeQuery();

        if (rs.next()) {

            codCargoContrato = rs.getInt(1);
            dataRef = rs.getDate(2);

        }

        preparedStatement = connection.prepareStatement("SELECT COD" +
                                                             " FROM TB_CONVENCAO_COLETIVA" +
                                                             " WHERE DATA_ADITAMENTO IS NOT NULL" +
                                                               " AND COD_CARGO_CONTRATO = ?" +
                                                               " AND DATA_INICIO_CONVENCAO = (SELECT MAX(DATA_INICIO_CONVENCAO)" +
                                                                                              " FROM TB_CONVENCAO_COLETIVA" +
                                                                                              " WHERE DATA_INICIO_CONVENCAO < ?" +
                                                                                                " AND COD_CARGO_CONTRATO = ?" +
                                                                                                " AND DATA_ADITAMENTO IS NOT NULL)");

        preparedStatement.setInt(1, codCargoContrato);
        preparedStatement.setDate(2, dataRef);
        preparedStatement.setInt(3, codCargoContrato);
        rs = preparedStatement.executeQuery();

        if (rs.next()) {

            codConvencaoAnterior = rs.getInt(1);

            return codConvencaoAnterior;

        }

        return -1;

    }

    /**
     * Função que retorna se em um dado mês existe um caso de cálculo parcial
     * por existirem duas ou mais convenções vigentes no mesmo mês.
     * @param pCodFuncaoContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */

    public boolean ExisteDuplaConvencao (int pCodFuncaoContrato, int pMes, int pAno, int pRetroatividade) {

        /**pRetroatividade = 1 - Considera a retroatividade.
           pRetroatividade = 2 - Desconsidera a retroatividade.*/

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCount = 0;
        int vCodContrato = 0;
        boolean vRetroatividade = false;

        /**Define o código do contrato.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT cod_contrato" +
                                                                 " FROM tb_funcao_contrato" +
                                                                 " WHERE cod=?");

            preparedStatement.setInt(1, pCodFuncaoContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodContrato = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        /**Definição do modo de funcionamento da função.*/

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(vCodContrato, pCodFuncaoContrato, pMes, pAno, 1);

        }

        /**Conta o número de convenções vigentes no mês.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD)" +
                                                                 " FROM tb_remuneracao_fun_con" +
                                                                 " WHERE DATA_ADITAMENTO IS NOT NULL" +
                                                                   " AND COD_FUNCAO_CONTRATO = ?" +
                                                                   " AND (((MONTH(data_inicio) = ? AND YEAR(data_inicio) = ?) " +
                                                                          " AND" +
                                                                         " (MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?) " +
                                                                          " AND" +
                                                                         " (CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE))) "+ //Define a validade da convenção.
                                                                        " OR" +
                                                                        " ((MONTH(data_fim) = ? AND YEAR(data_fim) = ?)))");

            preparedStatement.setInt(1, pCodFuncaoContrato);
            preparedStatement.setInt(2, pMes);
            preparedStatement.setInt(3, pAno);
            preparedStatement.setInt(4, pMes);
            preparedStatement.setInt(5, pAno);
            preparedStatement.setInt(6, pMes);
            preparedStatement.setInt(7, pAno);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCount = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        if (vCount != 0) {

            if ((vCount > 2) && (vRetroatividade == false)) {

                return true;

            } else {

                return false;

            }

        }

        return false;

    }

}
