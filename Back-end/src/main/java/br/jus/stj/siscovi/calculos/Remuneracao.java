package br.jus.stj.siscovi.calculos;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class Remuneracao {

    private Connection connection;

    Remuneracao(Connection connection) {

        this.connection = connection;

    }

    /**
     * Função que recupera o valor da remuneração vigente para o cargo de um
     * contrato em um determinado perído com vigência de mais de uma convenção.
     * @param pCodFuncaoContrato
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @param pRetroatividade
     * @return vRemuneracao
     */

    public float RetornaRemuneracaoPeriodo (int pCodFuncaoContrato, int pMes, int pAno, int pOperacao, int pRetroatividade) {

        //Checked.

        float vRemuneracao = 0;
        Date vDataReferencia = Date.valueOf(""+pAno+"-"+pMes+"-"+"01"); // Definição da data referência.
        int vCodContrato = 0;
        int vCodRemuneracaoCargoContrato = 0;
        boolean vRetroatividade = false;
        Date vDataAditamento = null;

        /**
         Operação 1: Remuneração do mês em que há somente uma remuneração ou remuneração atual.
         Operação 2: Remuneração encerrada do mês em que há mais de uma remuneração.
         pRetroatividade 1: Considera a retroatividade.
         pRetroatividade 2: Desconsidera os períodos de retroatividade.
         */

        ResultSet rs;
        PreparedStatement preparedStatement;

        /** Definição do cod_contrato. */

        try{

            preparedStatement = connection.prepareStatement("SELECT COD_CONTRATO" +
                                                                 " FROM tb_funcao_contrato" +
                                                                 " WHERE COD = ?");

            preparedStatement.setInt(1, pCodFuncaoContrato);
            rs = preparedStatement.executeQuery();

            if(rs.next()) {

                vCodContrato = rs.getInt(1);

            } else {

                return -1;

            }

        } catch(SQLException sqle) {

            sqle.printStackTrace();

        }

        /** Definição sobre a consideração da retroatividade.*/

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(vCodContrato, pCodFuncaoContrato, pMes, pAno, 1);

        }

        // Definição da remuneração.
        if (pOperacao == 1) {

            try {

                preparedStatement = connection.prepareStatement("SELECT remuneracao,\n" +
                        "       cod,\n" +
                        "       data_aditamento\n" +
                        " FROM tb_remuneracao_fun_con\n" +
                        " WHERE cod_funcao_contrato = ?\n" +
                        "   AND data_aditamento IS NOT NULL\n" +
                        "   AND CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)\n" +
                        "   AND (((((CAST(data_inicio AS DATE) <= CAST(? AS DATE))\n" +
                        "            AND\n" +
                        "           (CAST(data_inicio AS DATE) <= CAST(EOMONTH(?) AS DATE))))\n" +
                        "           AND(\n" +
                        "               ((((CAST(data_fim AS DATE) >= CAST(? AS DATE))\n" +
                        "               AND\n" +
                        "               (CAST(data_fim AS DATE) >= CAST(EOMONTH(?) AS DATE)))\n" +
                        "               OR data_fim IS NULL)))\n" +
                        "           OR (MONTH(data_inicio) = MONTH(?) --Ou início no mês referência.\n" +
                        "              AND YEAR(data_inicio) = YEAR(?))));");

                preparedStatement.setInt(1, pCodFuncaoContrato);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                preparedStatement.setDate(5, vDataReferencia);
                preparedStatement.setDate(6, vDataReferencia);
                preparedStatement.setDate(7, vDataReferencia);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    vRemuneracao = rs.getFloat("REMUNERACAO");
                    vCodRemuneracaoCargoContrato = rs.getInt("COD");
                    vDataAditamento = rs.getDate("DATA_ADITAMENTO");

                }

            } catch(SQLException sqle) {

                throw new NullPointerException("Erro ao tentar Definir remuneracao na função 'Retorna Remuneração Periodo'. Operação: 1");

            }

        }

        if (pOperacao == 2) {

            try {

                preparedStatement = connection.prepareStatement("SELECT REMUNERACAO" +
                                                                     " FROM tb_remuneracao_fun_con" +
                                                                     " WHERE COD_FUNCAO_CONTRATO = ?" +
                                                                       " AND DATA_ADITAMENTO IS NOT NULL" +
                                                                       " AND ((MONTH(DATA_FIM) = MONTH(?))" + // --Ou início no mês referência.
                                                                             " AND" +
                                                                            " (YEAR(DATA_FIM)) = YEAR(?))");

                preparedStatement.setInt(1, pCodFuncaoContrato);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    vRemuneracao = rs.getFloat("REMUNERACAO");

                }

            } catch(SQLException sqle) {

                throw new NullPointerException("Erro ao tentar definir remuneracao na função 'Retorna Remuneração Periodo'. Operação: 2");

            }

        }

        /**Caso haja retroatividade para o mês se retorna a remuneração anterior.*/

        if ((pOperacao == 1) && (vRetroatividade == true)) {

            try {

                Remuneracao remuneracao = new Remuneracao(connection);
                vCodRemuneracaoCargoContrato = remuneracao.RetornaRmuneracaoAnterior(vCodRemuneracaoCargoContrato);

                preparedStatement = connection.prepareStatement("SELECT REMUNERACAO" +
                                                                     " FROM tb_remuneracao_fun_con" +
                                                                     " WHERE COD = ?");

                preparedStatement.setInt(1, vCodRemuneracaoCargoContrato);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    vRemuneracao = rs.getFloat("REMUNERACAO");

                    return vRemuneracao;

                }

            } catch(SQLException sqle) {

                throw new NullPointerException("Erro ao tentar definir remuneração em 'FuncaoRemuneracaoPeriodo'. Operação = 1 e Retroatividade = true");

            }

        }

        /*if((pRetroatividade == 1) && (pOperacao == 1)){
            vRemuneracao = RetornaRemuneracaoPeriodo(pCodFuncaoContrato, pMes, pAno, 2, 1);
            return vRemuneracao;
        }
        if ((pRetroatividade == 2) && (pOperacao == 1)) {
            vRemuneracao = RetornaRemuneracaoPeriodo(pCodFuncaoContrato, pMes, pAno,2,2);
            return vRemuneracao;
        }*/

        return vRemuneracao;

    }

    /**
     * Retorna o código (cod) da remuneração anterior ao cod da remuneração passada.
     * Entenda "passada" como referência.
     * @param pCodRemuneracao
     * @return int
     */

    public int RetornaRmuneracaoAnterior (int pCodRemuneracao) {

        //Checked.

        int vCodRermuneracaoAnterior = 0;
        int vCodFuncaoContrato = 0;
        Date vDataReferencia = null;

        /**Define o cargo e a data referência com base na convenção passada.*/

        try (PreparedStatement preparedStatement = connection.prepareStatement ("SELECT COD_FUNCAO_CONTRATO," +
                                                                                           " DATA_INICIO" +
                                                                                      " FROM TB_REMUNERACAO_FUN_CON" +
                                                                                      " WHERE COD = ?")) {

            preparedStatement.setInt(1, pCodRemuneracao);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    vCodFuncaoContrato = resultSet.getInt("COD_FUNCAO_CONTRATO");
                    vDataReferencia = resultSet.getDate("DATA_INICIO");

                }

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro ao tentar definir dados do contrato em 'FuncaoRemuneracaoPeriodo'.");

        }

        /**Seleciona o cod da conveção anterior com base na maior data de início de conveção daquele cargo, anterior a convenção passada.*/

        try (PreparedStatement preparedStatement = connection.prepareStatement ("SELECT COD" +
                                                                                     " FROM TB_REMUNERACAO_FUN_CON" +
                                                                                     " WHERE DATA_ADITAMENTO IS NOT NULL" +
                                                                                       " AND COD_FUNCAO_CONTRATO = ?" +
                                                                                       " AND DATA_INICIO = (SELECT MAX(DATA_INICIO)" +
                                                                                                            " FROM tb_remuneracao_fun_con" +
                                                                                                            " WHERE DATA_INICIO < ?" +
                                                                                                              " AND COD_FUNCAO_CONTRATO = ?" +
                                                                                                              " AND DATA_ADITAMENTO IS NOT NULL)")) {

            preparedStatement.setInt(1, vCodFuncaoContrato);
            preparedStatement.setDate(2, vDataReferencia);
            preparedStatement.setInt(3, vCodFuncaoContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    vCodRermuneracaoAnterior = resultSet.getInt(1);

                }

            }

        } catch (SQLException e) {

            throw new NullPointerException("Erro ao carregar o código da convenção anterior na função 'RetornaRemuneracaoPeriodo'");

        }

        return vCodRermuneracaoAnterior;

    }

    /**
     * Função que recupera o valor da remuneração vigente para uma função de um
     * contrato em um determinado perído de vigência.
     * @param pCodFuncaoContrato
     * @param pDataInicio
     * @param pDataFim
     * @param pRetroatividade
     * @return vRemuneracao
     */

    public float RetornaRemuneracaoPeriodo (int pCodFuncaoContrato, Date pDataInicio, Date pDataFim, int pRetroatividade) {

        //Checked.

        /**pRetroatividade 1: Considera a retroatividade.
           pRetroatividade 2: Desconsidera os períodos de retroatividade.*/

        float vRemuneracao = 0;
        Date vDataReferencia;
        int vCodContrato = 0;
        int vCodRemuneracaoFunCon = 0;
        boolean vRetroatividade = false;
        Date vDataAditamento;

        /**Definição do cod_contrato.*/

        String sql = "SELECT COD_CONTRATO FROM TB_FUNCAO_CONTRATO WHERE COD = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodFuncaoContrato);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    vCodContrato = resultSet.getInt("COD_CONTRATO");

                }

            }
        } catch (SQLException e) {

            throw new NullPointerException("Não foi possível estabelecer o cod do contrato na função 'RetornaRemuneracaoPeriodo'.");

        }

        /**Definição do percentual.*/

        sql = "SELECT COD," +
                    " REMUNERACAO" +
               " FROM TB_REMUNERACAO_FUN_CON" +
              " WHERE DATA_ADITAMENTO IS NOT NULL" +
                " AND COD_FUNCAO_CONTRATO = ?" +
                " AND DATA_INICIO <= ?" +
                " AND (DATA_FIM >= ? OR DATA_FIM IS NULL)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodFuncaoContrato);
            preparedStatement.setDate(2, pDataInicio);
            preparedStatement.setDate(3, pDataFim);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    vCodRemuneracaoFunCon = resultSet.getInt("COD");
                    vRemuneracao = resultSet.getFloat("REMUNERACAO");

                }
            }

        } catch (SQLException e) {

            throw new NullPointerException("Erro ao tentar carregar a remuneração e código. Função: " + pCodFuncaoContrato + ". No periodo entre : " +
                    pDataInicio.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " e " + pDataFim.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyyy")));

        }

        return vRemuneracao;

    }

}
