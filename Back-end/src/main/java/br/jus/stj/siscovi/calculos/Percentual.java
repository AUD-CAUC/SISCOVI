package br.jus.stj.siscovi.calculos;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_REDPeer;
import javax.validation.constraints.Null;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class Percentual {

    private Connection connection;

    Percentual(Connection connection){

        this.connection = connection;

    }

    /**
     * Função que retorna se em um dado mês existe ao menos um caso
     * de mudança de percentual que enseje dupla vigência.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */

    public boolean ExisteMudancaPercentual (int pCodContrato, int pMes, int pAno, int pRetroatividade) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        /**
         pRetroatividade = 1 - Considera a retroatividade.
         pRetroatividade = 2 - Desconsidera a retroatividade.*/

        int vCount = 0;
        int vCount2 = 0;
        boolean vRetroatividade = false;

        /** --Definição do modo de funcionamento da função. */

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);

        }

        /** --Conta o número de percentuais da mesma rubrica vigentes no mês. */

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD_RUBRICA)" +
                                                                 " FROM (SELECT cod_rubrica," +
                                                                              " COUNT(pc.cod) AS \"CONTAGEM\"" +
                                                                         " FROM tb_percentual_contrato pc" +
                                                                         " WHERE pc.cod_contrato = ?" +
                                                                           " AND (((MONTH(pc.data_inicio) = ? AND YEAR(pc.data_inicio) = ?)" +
                                                                                  " AND" +
                                                                                 " (MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?)" +
                                                                                  " AND" +
                                                                                  "(CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)))" + //Define a validade da convenção
                                                                                 " OR" +
                                                                                " (MONTH(pc.data_fim) = ? AND YEAR(pc.data_fim) = ?))" +
                                                                       " GROUP BY cod_rubrica)" + " AS X" +
                                                                 " WHERE X.CONTAGEM > 1");

            preparedStatement.setInt(1, pCodContrato);
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

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(cod_rubrica)" +
                                                                 " FROM (SELECT pe.cod_rubrica," +
                                                                              " COUNT(pe.cod) AS \"CONTAGEM\"" +
                                                                         " FROM tb_percentual_estatico pe" +
                                                                         " WHERE (((MONTH(pe.data_inicio) = ? AND YEAR(pe.data_inicio) = ?)" +
                                                                                  " AND" +
                                                                                 " (MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?)" +
                                                                                  " AND" +
                                                                                 " (CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)))" + //--Define a validade da convenção.
                                                                                " OR" +
                                                                               " (MONTH(pe.data_fim) = ? AND YEAR(pe.data_fim) = ?))" +
                                                                       " GROUP BY pe.cod_rubrica) AS X" +
                                                                 " WHERE X.CONTAGEM > 1");

            int i = 1;
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i++, pAno);
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i++, pAno);
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i, pAno);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCount2 = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        /** --Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO. */

        if (((vCount > 0) && (vRetroatividade == false)) || ((vCount2 > 0) && (!vRetroatividade))) {

            return true;

        }

        return false;

    }

    /**
     * Função que retorna se em um dado mês existe ao menos um caso
     * de mudança de percentual estático que enseje mais de uma vigência.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */

     public boolean ExisteMudancaEstatico (int pCodContrato, int pMes, int pAno, int pRetroatividade) {

         //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Retroatividade retroatividade = new Retroatividade(connection);

        /**
         pRetroatividade = 1 - Considera a retroatividade.
         pRetroatividade = 2 - Desconsidera a retroatividade.
         */

        int vCount = 0;
        boolean vRetroatividade = false;

        /**--Definição do modo de funcionamento da função.*/

        if (pRetroatividade == 1) {

                vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);

        }

        /** --Conta o número de percentuais da mesma rubrica vigentes no mês. */

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD_RUBRICA)" +
                                                                 " FROM (SELECT pe.cod_rubrica," +
                                                                              " COUNT(pe.cod) AS \"CONTAGEM\"\n" +
                                                                         " FROM tb_percentual_estatico pe" +
                                                                         " WHERE (((MONTH(pe.data_inicio) = ? AND YEAR(pe.data_inicio) = ?)" +
                                                                                  " AND" +
                                                                                  "(MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?)" +
                                                                                  " AND" +
                                                                                 " (CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)))" + /*--Define a validade da convenção.*/
                                                                                " OR" +
                                                                               " (MONTH(pe.data_fim) = ? AND YEAR(pe.data_fim) = ?))" +
                                                                        " GROUP BY pe.cod_rubrica)" +
                                                                 " WHERE CONTAGEM > 1");

            preparedStatement.setInt(1, pMes);
            preparedStatement.setInt(2, pAno);
            preparedStatement.setInt(3, pMes);
            preparedStatement.setInt(4, pAno);
            preparedStatement.setInt(5, pMes);
            preparedStatement.setInt(6, pAno);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCount = resultSet.getInt(1);

            }

            if (vCount != 0) {

                /**--Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO.*/

                if ((vCount > 0) && (vRetroatividade == false)) {

                    return true;

                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    /**
     * Função que retorna se em um dado mês existe ao menos um caso
     * de mudança de percentual do contrato que enseje mais de uma vigência.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */

    public boolean ExisteMudancaContrato (int pCodContrato, int pMes, int pAno, int pRetroatividade) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        /**pRetroatividade = 1 - Considera a retroatividade.
            pRetroatividade = 2 - Desconsidera a retroatividade.*/

        int vCount = 0;
        boolean vRetroatividade = false;

        /**Definição do modo de funcionamento da função.*/

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(this.connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);

        }

        /**Conta o número de percentuais da mesma rubrica vigentes no mês.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD_RUBRICA)" +
                                                                 " FROM (SELECT cod_rubrica," +
                                                                              " COUNT(pc.cod) AS \"CONTAGEM\"" +
                                                                         " FROM tb_percentual_contrato pc" +
                                                                         " WHERE pc.cod_contrato = ?" +
                                                                           " AND (((MONTH(data_inicio) = ? AND YEAR(pc.data_inicio) = ?)" +
                                                                                 " AND" +
                                                                                 " (MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?)" +
                                                                                 " AND" +
                                                                                 " (CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)))" + /* Define a validade da convenção.*/
                                                                                " OR" +
                                                                                " (MONTH(pc.data_fim) = ? AND YEAR(pc.data_fim) = ?))" +
                                                                       " GROUP BY cod_rubrica)" +
                                                                 " WHERE CONTAGEM > 1");

            int i = 1;
            preparedStatement.setInt(i++, pCodContrato);
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i++, pAno);
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i++, pAno);
            preparedStatement.setInt(i++, pMes);
            preparedStatement.setInt(i++, pAno);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCount = resultSet.getInt(1);

            }

            if (vCount != 0) {

                /**Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO.*/

                if ((vCount > 0 ) && (vRetroatividade == false)) {

                    return true;

                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    /**--Função que retorna o percentual de um contrato em um período específico.
     * @param pCodContrato
     * @param pCodRubrica
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @param pRetroatividade
     * @return
     */

    public float RetornaPercentualContrato (int pCodContrato, int pCodRubrica, int pMes, int pAno, int pOperacao, int pRetroatividade) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        float vPercentual = 0;
        int vCodPercentual = 0;
        int vRubricaCheck = 0;
        boolean vRetroatividade = false;
        int vRetroatividadePercentual = 0;
        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01");

        /**pOperação = 1: Percentual do mês em que não há vigência de mais de um percentual ou percentual atual (contrato).
           pOperação = 2: Percentual encerrado do mês em que há dupla vigência (contrato).
           pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
           pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.

           Legenda de rúbricas (usar esses códigos):

            1 - Férias
            2 - Terço constitucional
            3 - Décimo terceiro
            4 - FGTS
            5 - Multa do FGTS
            6 - Penalidade do FGTS
            7 - Incidência do submódulo 4.1
          */

         /**Confere se o cod da rubrica passada existe.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT(PC.COD_RUBRICA))" +
                                                                 " FROM TB_PERCENTUAL_CONTRATO PC" +
                                                                 " WHERE PC.COD_RUBRICA = ?" +
                                                                   " AND PC.COD_CONTRATO = ?");

            preparedStatement.setInt(1, pCodRubrica);
            preparedStatement.setInt(2, pCodContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vRubricaCheck = resultSet.getInt(1);

            }

            if (vRubricaCheck == 0) {

                throw new NullPointerException("Rubrica Inexistente!");

            }

        } catch (SQLException sqle) {

            throw new IllegalArgumentException("Erro ao tentar acessar o banco de Dados");

        }

        /**Confere o status de retroatividade para o período.*/

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);

        }

        if (vRetroatividade) {

            /**Estabelece se a retroatividade observada é em relação à rubrica passada.*/

            try {

                preparedStatement = connection.prepareStatement("SELECT COUNT(RP.COD)" +
                                                                     " FROM TB_RETROATIVIDADE_PERCENTUAL RP" +
                                                                       " JOIN TB_PERCENTUAL_CONTRATO PC ON PC.COD = RP.COD_PERCENTUAL_CONTRATO" +
                                                                     " WHERE PC.COD_CONTRATO = ?" +
                                                                       " AND PC.COD_RUBRICA = ?" +
                                                                       " AND CAST(? AS DATE) >= CAST(DATEADD(DAY, 1, EOMONTH(DATEADD(MONTH, -1, RP.INICIO))) AS DATE)" +
                                                                       " AND CAST(? AS DATE) <= CAST(RP.FIM AS DATE)");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodRubrica);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vRetroatividadePercentual = resultSet.getInt(1);

                }

                if (vRetroatividadePercentual == 0) {

                    vRetroatividade = false;

                }

            } catch (SQLException e) {

                throw new NullPointerException("Erro ao tentar determinar existência de retroativade para a rubrica passda.");

            }

        }

        /**Definição do percentual. Busca realizada na tabela de percentuais do contrato.*/

        if (pOperacao == 1) {

            try {

                preparedStatement = connection.prepareStatement("SELECT pc.percentual," +
                                                                           " pc.cod" +
                                                                     " FROM tb_percentual_contrato pc" +
                                                                     " WHERE pc.cod_contrato = ?" +
                                                                       " AND pc.cod_rubrica = ?" +
                                                                       " AND pc.data_aditamento IS NOT NULL\n" +
                                                                       " AND CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)" +
                                                                       " AND ((((CAST(pc.data_inicio AS DATE) <= CAST(? AS DATE))" +
                                                                              " AND" +
                                                                              " (CAST(pc.data_inicio AS DATE) <= EOMONTH(CAST(? AS DATE))))" +
                                                                              " AND" +
                                                                              " (((CAST(pc.data_fim AS DATE) >= CAST(? AS DATE))" +
                                                                               " AND" +
                                                                              " (CAST(pc.data_fim AS DATE) >= EOMONTH(CAST(? AS DATE))))" +
                                                                               " OR pc.data_fim IS NULL)) OR (MONTH(data_inicio) = MONTH(?)" +
                                                                                 " AND YEAR(data_inicio) = YEAR(?)))");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodRubrica);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                preparedStatement.setDate(5, vDataReferencia);
                preparedStatement.setDate(6, vDataReferencia);
                preparedStatement.setDate(7, vDataReferencia);
                preparedStatement.setDate(8, vDataReferencia);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vPercentual = resultSet.getFloat(1);
                    vCodPercentual = resultSet.getInt(2);

                }

            } catch (SQLException e) {

                    throw new NullPointerException("Erro na busca do percentual do contrato!");

            }

        }

        /**Busca realizada na tabela de percentuais do contrato para percentual com data fim no mês de referência.*/

        if (pOperacao == 2) {

            try {

                preparedStatement = connection.prepareStatement("SELECT PC.PERCENTUAL" +
                                                                     " FROM TB_PERCENTUAL_CONTRATO PC" +
                                                                     " WHERE PC.COD_CONTRATO = ?" +
                                                                       " AND PC.COD_RUBRICA = ?" +
                                                                       " AND PC.DATA_ADITAMENTO IS NOT NULL" +
                                                                       " AND (MONTH(DATA_FIM) = MONTH(?)) AND YEAR(DATA_FIM) = YEAR(?)");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodRubrica);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vPercentual = resultSet.getFloat(1);
                    vCodPercentual = resultSet.getInt(2);

                }

            } catch (SQLException e) {

                throw new NullPointerException("Erro ao buscar percentual do contrato com data fim no mês de referência!");

            }

        }

        /**Em caso de consideração da retroatividade se retorna o percentual anterior.*/

        if (pOperacao == 1 && vRetroatividade) {

            vCodPercentual = RetornaPercentualAnterior(vCodPercentual);

            try {

                preparedStatement = connection.prepareStatement("SELECT PERCENTUAL" +
                                                                     " FROM TB_PERCENTUAL_CONTRATO" +
                                                                     " WHERE COD = ?");

                preparedStatement.setInt(1, vCodPercentual);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vPercentual = resultSet.getFloat("PERCENTUAL");

                }

            } catch (SQLException e) {

                e.printStackTrace();

            }

        }

        return vPercentual;

    }

    /**
     * Retorna o código (cod) do percentual anterior ao cod do percentual passado.
     * Entenda "passado" como referência.
     * @param pCodPercentual
     * @return vCodPercentual
     */

    public int RetornaPercentualAnterior (int pCodPercentual) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodPercentualAnterior = 0;
        int vCodContrato = 0;
        int vCodRubrica = 0;
        Date vDataReferencia = null;
        int vCount = 0;

        /**Verificação de existência do percentual no contrato (para diferenciar dos percentuais estáticos).*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD)" +
                                                                 " FROM TB_PERCENTUAL_CONTRATO" +
                                                                 " WHERE COD = ?");

            preparedStatement.setInt(1, pCodPercentual);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCount = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            throw new NullPointerException("Não foi encontrada uma percentual com esse código no contrato!");

        }

        /**Se vCount for maior que zero então o percentual existe no contrato.*/

        if (vCount > 0) {

            /**Define o contrato e a data referência com base no percentual referência.*/

            try {

                preparedStatement = connection.prepareStatement("SELECT COD_CONTRATO," +
                                                                           " DATA_INICIO," +
                                                                           " COD_RUBRICA" +
                                                                     " FROM TB_PERCENTUAL_CONTRATO" +
                                                                     " WHERE COD = ?");

                preparedStatement.setInt(1, pCodPercentual);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vCodContrato = resultSet.getInt("COD_CONTRATO");
                    vDataReferencia = resultSet.getDate("DATA_INICIO");
                    vCodRubrica = resultSet.getInt("COD_RUBRICA");

                }

                /**Seleciona o cod do percentual anterior com base na maior data de início
                de percentual daquela rubrica, anterior ao percentual referência.*/

                preparedStatement = connection.prepareStatement("SELECT COD" +
                                                                     " FROM TB_PERCENTUAL_CONTRATO" +
                                                                     " WHERE DATA_ADITAMENTO IS NOT NULL" +
                                                                       " AND COD_CONTRATO = ?" +
                                                                       " AND COD_RUBRICA = ?" +
                                                                       " AND DATA_INICIO = (SELECT MAX(DATA_INICIO)" +
                                                                                            " FROM TB_PERCENTUAL_CONTRATO" +
                                                                                            " WHERE DATA_INICIO < ?" +
                                                                                              " AND COD_CONTRATO = ?" +
                                                                                              " AND COD_RUBRICA = ?" +
                                                                                              " AND DATA_ADITAMENTO IS NOT NULL)");

                preparedStatement.setInt(1, vCodContrato);
                preparedStatement.setInt(2, vCodRubrica);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setInt(4, vCodContrato);
                preparedStatement.setInt(5, vCodRubrica);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vCodPercentualAnterior = resultSet.getInt("COD");

                }

                return vCodPercentualAnterior;

            } catch (SQLException e) {

                throw new NullPointerException("Não foi contrado nenhum dado!");

            }

        }

        return 0;

    }

    /**--Função que retorna o percentual estático em um período específico.
     * @param pCodContrato
     * @param pCodRubrica
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @param pRetroatividade
     * @return
     */

    public float RetornaPercentualEstatico(int pCodContrato, int pCodRubrica, int pMes, int pAno, int pOperacao, int pRetroatividade) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        float vPercentual = 0;
        int vCodPercentual = 0;
        int vRubricaCheck = 0;
        boolean vRetroatividade = false;
        int vRetroatividadePercentual = 0;

        /**Definição da data referência.*/

        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01");

        /**
          * pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual.
          * pOperação = 2: Percentual encerrado do mês em que há dupla vigência.
          * pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
          * pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.
          *
          * Legenda de rúbricas (usar esses códigos):
          * 1 - Férias
          * 2 - Terço constitucional
          * 3 - Décimo terceiro
          * 4 - FGTS
          * 5 - Multa do FGTS
          * 6 - Penalidade do FGTS
          * 7 - Incidência do submódulo 4.1
          */

        /**Confere se o cod da rubrica passada existe.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT(COD))" +
                                                                 " FROM TB_RUBRICA" +
                                                                 " WHERE COD = ?");

            preparedStatement.setInt(1, pCodRubrica);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                 vRubricaCheck = resultSet.getInt(1);

            }

         } catch (SQLException e) {

             throw new IllegalArgumentException("Erro ao tentar encontrar um Percentual Estático para a rubrica: " + pCodRubrica);

        }

         if (vRubricaCheck == 0) {

             throw  new NullPointerException("Erro na execução da função F_RETORNA_PERCENTUAL_CONTRATO: Código da rubrica é inexistente. CÓDIGO: -20001");

        }

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT(COD))" +
                                                                 " FROM TB_RUBRICA" +
                                                                 " WHERE COD = ?");

            preparedStatement.setInt(1, pCodRubrica);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vRubricaCheck = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            throw new IllegalArgumentException("Erro ao tentar encontrar um Percentual Estático para a rubrica: " + pCodRubrica);

        }

        if (vRubricaCheck == 0) {

            throw  new NullPointerException("Erro na execução da função F_RETORNA_PERCENTUAL_CONTRATO: Código da rubrica é inexistente. CÓDIGO: -20001");

        }

        /**Confere o status de retroatividade para o período.*/

        if (pRetroatividade == 1) {

            Retroatividade retroatividade = new Retroatividade(connection);
            vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);

        }

        /**Se o status de retroatividade para o período for TRUE
        então ele verifica se a rubrica do argumento deu origem
        a retroatividade.*/

        if(vRetroatividade) {

            try {

                preparedStatement = connection.prepareStatement("SELECT COUNT(RPE.COD)" +
                                                                     " FROM TB_RETRO_PERCENTUAL_ESTATICO RPE" +
                                                                       " JOIN TB_PERCENTUAL_ESTATICO PE ON PE.COD = RPE.COD_PERCENTUAL_ESTATICO" +
                                                                      " WHERE PE.COD_RUBRICA = ?" +
                                                                        " AND CAST(? AS DATE) >= DATEADD( DAY, 1, EOMONTH(DATEADD(MONTH, -1,CAST (RPE.INICIO AS DATE))))" +
                                                                        " AND CAST(? AS DATE) <= CAST(RPE.FIM AS DATE)");

                preparedStatement.setInt(1, pCodRubrica);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){

                    vRetroatividadePercentual = resultSet.getInt(1);

                }

                if(vRetroatividadePercentual == 0) {

                    vRetroatividade = false;

                }

            } catch (SQLException e) {

                throw new NullPointerException("Erro ao tentar verificar se existe retroatividade do percentual estático: " + pCodRubrica);

            }

         }

         /**Definição do percentual.
            Busca realizada na tabela de percentuais estáticos.*/

        if (pOperacao == 1) {

            try{

                preparedStatement = connection.prepareStatement("SELECT PE.percentual," +
                                                                           " PE.cod" +
                                                                     " FROM TB_PERCENTUAL_ESTATICO PE" +
                                                                     " WHERE PE.COD_RUBRICA = ?" +
                                                                       " AND PE.DATA_ADITAMENTO IS NOT NULL" +
                                                                       " AND CAST(PE.DATA_ADITAMENTO AS DATE) <= CAST(GETDATE() AS DATE)" +
                                                                       " AND ((((CAST(PE.DATA_INICIO AS DATE) <= CAST(? AS DATE)) " +
                                                                               " AND" +
                                                                              " (CAST(PE.DATA_INICIO AS DATE) <= EOMONTH(CAST(? AS DATE))))" +
                                                                               " AND" +
                                                                              " (((CAST(pe.data_fim AS DATE) >= CAST(? AS DATE))" +
                                                                                 " AND" +
                                                                                " (CAST(PE.DATA_FIM AS DATE) >= EOMONTH(CAST(? AS DATE))))" +
                                                                                 " OR pe.data_fim IS NULL))" +
                                                                           " OR" +
                                                                          " (MONTH(DATA_INICIO) = MONTH(?) AND YEAR(DATA_INICIO) = YEAR(?)))");

                preparedStatement.setInt(1, pCodRubrica);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                preparedStatement.setDate(5, vDataReferencia);
                preparedStatement.setDate(6, vDataReferencia);
                preparedStatement.setDate(7, vDataReferencia);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vPercentual = resultSet.getFloat(1);
                    vCodPercentual = resultSet.getInt(2);

                }

            } catch(SQLException sqle) {

                throw new NullPointerException("Erro ao tentar encontrar um percentual estático ! Operação do erro: 1");

            }

        }

         /**Busca realizada na tabela de percentuais estáticos para percentual com data fim no mês de referência.*/

         if (pOperacao == 2) {

             try {

                 preparedStatement = connection.prepareStatement("SELECT PE.PERCENTUAL FROM TB_PERCENTUAL_ESTATICO PE WHERE PE.COD_RUBRICA=? AND PE.DATA_ADITAMENTO IS NOT NULL" +
                         " AND (MONTH(DATA_FIM)= MONTH(?)) AND (YEAR(DATA_FIM)= YEAR(?))");

                 preparedStatement.setInt(1, pCodRubrica);
                 preparedStatement.setDate(2, vDataReferencia);
                 preparedStatement.setDate(3, vDataReferencia);
                 resultSet = preparedStatement.executeQuery();

                 if (resultSet.next()) {

                     vPercentual = resultSet.getFloat(1);

                 }

             } catch (SQLException e) {

                 throw new NullPointerException("Erro ao tentar encontrar um percentual estático ! Operação: 2");

             }

         }

         /**Em caso de consideração da retroatividade se retorna o percentual anterior.*/

         if ((pOperacao == 1) && (vRetroatividade)) {

             Percentual percentual = new Percentual(connection);
             vCodPercentual = percentual.RetornaPercentualAnterior(vCodPercentual);

             try {

                 preparedStatement = connection.prepareStatement("SELECT PERCENTUAL" +
                                                                      " FROM TB_PERCENTUAL_ESTATICO" +
                                                                      " WHERE COD = ?");

                 preparedStatement.setInt(1, vCodPercentual);
                 resultSet = preparedStatement.executeQuery();

                 if (resultSet.next()) {

                     vPercentual = resultSet.getFloat("PERCENTUAL");

                 }

             } catch(SQLException sqle) {

                 throw new NullPointerException("Erro ao tentar recuperar percentual estático ! Operação: 1. Retroatividade: true");

             }

         }

         return vPercentual;

     }

    /**
     *  Função que retorna se em um dado mês existe ao menos um caso
     *  de mudança de percentual do contrato que enseje dupla vigência.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */

     public boolean MudancaPercentualContrato (int pCodContrato, int pMes, int pAno, int pRetroatividade) {
         PreparedStatement preparedStatement;
         ResultSet  resultSet;
         Retroatividade retroatividade = new Retroatividade(connection);

         /*
          * pRetroatividade = 1 - Considera a retroatividade.
          * pRetroatividade = 2 - Desconsidera a retroatividade.
          */

         int vCount = 0;
         boolean vRetroatividade = false;

         /* Definição do modo de funcionamento da função.*/

         if(pRetroatividade == 1) {
             vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0 , pMes, pAno, 2);
         }

         /* Conta o número de percentuais da mesma rubrica vigentes no mês.*/

         try{
             preparedStatement = connection.prepareStatement("SELECT COUNT(COD_RUBRICA) FROM (SELECT cod_rubrica, COUNT(pc.cod) AS \"CONTAGEM\"" +
                     " FROM tb_percentual_contrato pc WHERE pc.cod_contrato = ? AND (((MONTH(pc.data_inicio) = ? AND YEAR(pc.data_inicio) = ?) " +
                     " AND (MONTH(data_aditamento) = ? AND YEAR(data_aditamento) = ?) AND (CAST(data_aditamento AS DATE) <= CAST(GETDATE() AS DATE)))" +
                     " OR (MONTH(pc.data_fim) = ? AND YEAR(pc.data_fim) = ?)) GROUP BY cod_rubrica) AS X WHERE X.CONTAGEM > 1");
             preparedStatement.setInt(1, pCodContrato);
             preparedStatement.setInt(2, pMes);
             preparedStatement.setInt(3, pAno);
             preparedStatement.setInt(4, pMes);
             preparedStatement.setInt(5, pAno);
             preparedStatement.setInt(6, pMes);
             preparedStatement.setInt(7, pAno);
             resultSet = preparedStatement.executeQuery();
             if(resultSet.next()) {
                 vCount = resultSet.getInt(1);
             }
         }catch (SQLException sqle) {
             throw new NullPointerException("Erro na execução da função F_MUNDANCA_PERCENTUAL_CONTRATO. Código: -20003");
         }
         if(vCount != 0) {

             /* Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO.*/

             if(vCount > 0 && !vRetroatividade) {
                 return true;
             }else {
                 return false;
             }
         }
         return false;
     }

    /**
     * Função que retorna se em um dado mês existe ao menos um caso
     * de mudança de percentual estático que enseje dupla vigência.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     * @param pRetroatividade
     * @return boolean
     */
     public boolean MudancaPercentualEstatico(int pCodContrato, int pMes, int pAno, int pRetroatividade){
         Retroatividade retroatividade = new Retroatividade(connection);

         // pRetroatividade = 1 - Considera a retroatividade.
         // pRetroatividade = 2 - Desconsidera a retroatividade.

         int vCount = 0;
         boolean vRetroatividade = false;

         if(pRetroatividade == 1) {
             vRetroatividade = retroatividade.ExisteRetroatividade(pCodContrato, 0, pMes, pAno, 2);
         }

         // --Conta o número de percentuais da mesma rubrica vigentes no mês.
         try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(COD_RUBRICA) FROM (SELECT PE.COD_RUBRICA, COUNT(PE.COD) AS \"CONTAGEM\" FROM TB_PERCENTUAL_ESTATICO PE " +
                 "WHERE (((MONTH(PE.DATA_INICIO)=? AND YEAR(PE.DATA_INICIO)=?) AND (MONTH(DATA_ADITAMENTO)=? AND YEAR(DATA_ADITAMENTO)=?) AND" +
                 " (CAST(DATA_ADITAMENTO AS DATE) <= CAST(GETDATE() AS DATE))) OR (MONTH(PE.DATA_FIM)=? AND YEAR(PE.DATA_FIM)=?)) GROUP BY PE.COD_RUBRICA) AS X WHERE X.CONTAGEM >1")){
             preparedStatement.setInt(1, pMes);
             preparedStatement.setInt(2, pAno);
             preparedStatement.setInt(3, pMes);
             preparedStatement.setInt(4, pAno);
             preparedStatement.setInt(5, pMes);
             preparedStatement.setInt(6, pAno);
             try(ResultSet resultSet = preparedStatement.executeQuery()){
                 if(resultSet.next()) {
                     vCount = resultSet.getInt(1);
                 }
             }

         } catch (SQLException e) {
             throw new NullPointerException("Erro na execução da função F_MUNDANCA_PERCENTUAL_ESTATICO. Erro: -20003");
         }
         if(vCount != 0) {
             // --Se houver qualquer número de percentuais da mesma rubrica no mês passado retorna VERDADEIRO.
             if(vCount > 0 && !vRetroatividade) {
                 return true;
             }else {
                 return false;
             }
         }
         return false;
     }

    /**
     * Função que retorna o percentual de um contrato em um período específico.
     * @param pCodContrato
     * @param pCodRubrica
     * @param pDataInicio
     * @param pDataFim
     * @param pRetroatividade
     * @return vPercentual
     */

     public float RetornaPercentualContrato (int pCodContrato, int pCodRubrica, Date pDataInicio, Date pDataFim, int pRetroatividade) {

         //Checked.

         float vPercentual = 0;
         // int vCodPercentual = 0;
         int vRubricaCheck = 0;
        // boolean vRetroatividade = false;
         int vRetroatividadePercentual = 0;
         Date vDataReferencia;
         int vAno;
         int vMes;

         /**pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual (contrato).
            pOperação = 2: Percentual encerrado do mês em que há dupla vigência (contrato).
            pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
            pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.

            Legenda de rúbricas (usar esses códigos):

              1 - Férias (percentual contrato)
              2 - Terço constitucional (percentual contrato)
              3 - Décimo terceiro (percentual contrato)
              4 - FGTS (percentual estático)
              5 - Multa do FGTS (percentual estático)
              6 - Penalidade do FGTS (percentual estático)
              7 - Incidência do submódulo 4.1 (percentual contrato)*/

         /**Confere se o cod da rubrica passada existe.*/

         String sql = "SELECT COUNT(DISTINCT(PC.COD_RUBRICA))" +
                       " FROM TB_PERCENTUAL_CONTRATO PC" +
                       " WHERE PC.COD_RUBRICA = ?" +
                         " AND PC.COD_CONTRATO = ?";

         try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

             preparedStatement.setInt(1, pCodRubrica);
             preparedStatement.setInt(2, pCodContrato);

             try (ResultSet resultSet = preparedStatement.executeQuery()) {

                 if (resultSet.next()) {

                     vRubricaCheck = resultSet.getInt(1);

                 }

             }

         } catch (SQLException e) {

             throw new NullPointerException("Erro ao tentar verfificar a existência da rubrica: " + pCodRubrica + ", na função RetornaPercentualContrato.");

         }

         if(vRubricaCheck == 0) {

             throw new NullPointerException("Erro na execução da função RetornaPercentualContrato: Código da rubrica é inexistente");

         }

         /**Definição do percentual.
            Busca realizada na tabela de percentuais do contrato.*/

         sql = "SELECT COD," +
                     " PERCENTUAL" +
                " FROM TB_PERCENTUAL_CONTRATO" +
                " WHERE COD_CONTRATO = ?" +
                  " AND DATA_ADITAMENTO IS NOT NULL" +
                  " AND COD_RUBRICA = ?" +
                  " AND DATA_INICIO <= ?" +
                  " AND (DATA_FIM >= ? OR DATA_FIM IS NULL)";

         try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

             preparedStatement.setInt(1, pCodContrato);
             preparedStatement.setInt(2, pCodRubrica);
             preparedStatement.setDate(3, pDataInicio);
             preparedStatement.setDate(4, pDataFim);

             try (ResultSet resultSet = preparedStatement.executeQuery()) {

                 if (resultSet.next()) {

                     vPercentual = resultSet.getFloat("PERCENTUAL");

                 }

             }

         } catch (SQLException e) {

             throw new NullPointerException("Erro ao tentar buscar o percentual do contrato para o período passado. Data Inicio: " + pDataInicio.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
             + "Data Fim: " + pDataInicio.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

         }

         return vPercentual;

     }

    /**
     * Função que retorna o percentual de um contrato em um período específico.
     * @param pCodContrato
     * @param pCodRubrica
     * @param pDataInicio
     * @param pDataFim
     * @param pRetroatividade
     * @return vPercentual
     */

     public float RetornaPercentualEstatico (int pCodContrato, int pCodRubrica, Date pDataInicio, Date pDataFim, int pRetroatividade) {

         //Checked.

         /**pOperação = 1: Percentual do mês em que não há dupla vigência ou percentual atual (contrato).
            pOperação = 2: Percentual encerrado do mês em que há dupla vigência (contrato).
            pRetroatividade = 1: Leva em consideração a retroatividade (funcionamento normal).
            pRetroatividade = 2: Desconsidera a retroatividade para realizar o cálculo desta.

            Legenda de rúbricas (usar esses códigos):
            1 - Férias (percentual contrato)
            2 - Terço constitucional (percentual contrato)
            3 - Décimo terceiro (percentual contrato)
            4 - FGTS (percentual estático)
            5 - Multa do FGTS (percentual estático)
            6 - Penalidade do FGTS (percentual estático)
            7 - Incidência do submódulo 4.1 (percentual contrato)*/

         float vPercentual = 0;
         int vCodPercentual = 0;
         int vRubricaCheck = 0;
         boolean vRetroatividade = false;
         int vRetroatividadePercentual = 0;
         Date vDataReferencia;
         int vAno;
         int vMes;

         /**Confere se o cod da rubrica passada existe.*/

         String sql = "SELECT COUNT(DISTINCT(PE.COD_RUBRICA))" +
                       " FROM TB_PERCENTUAL_ESTATICO PE" +
                       " WHERE PE.COD_RUBRICA = ?";

         try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

             preparedStatement.setInt(1, pCodRubrica);

             try (ResultSet resultSet = preparedStatement.executeQuery()) {

                 if (resultSet.next()) {

                     vRubricaCheck = resultSet.getInt(1);

                     if (vRubricaCheck == 0) {

                         throw new NullPointerException("Erro na execução da função RetornaPercentualEstatico: Código da rubrica é inexistente.");

                     }

                 }

             }

         } catch (SQLException e) {

             throw new NullPointerException("Erro ao tentar verificar a existência da rubrica: " + pCodRubrica);

         }

         sql = "SELECT COD," +
                     " PERCENTUAL" +
                " FROM TB_PERCENTUAL_ESTATICO PE" +
                " WHERE DATA_ADITAMENTO IS NOT NULL" +
                  " AND COD_RUBRICA = ?" +
                  " AND DATA_INICIO <= ?" +
                  " AND (DATA_FIM >= ? OR DATA_FIM IS NULL)";

         try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

             preparedStatement.setInt(1, pCodRubrica);
             preparedStatement.setDate(2, pDataInicio);
             preparedStatement.setDate(3, pDataFim);

             try (ResultSet resultSet  = preparedStatement.executeQuery()) {

                 if (resultSet.next()) {

                     vPercentual = resultSet.getFloat(2);

                 }

             }

         } catch (SQLException e) {

             throw new NullPointerException("Erro ao tentar carregar o percentual estático : " + pCodRubrica + ". No período especificado. Data inicio: "
                     + pDataInicio.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". Data fim: " + pDataFim.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

         }

         return vPercentual;

     }

}
