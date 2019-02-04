package br.jus.stj.siscovi.calculos;

import java.sql.*;

public class Retroatividade {

    private Connection connection;

    Retroatividade(Connection connection) {

        this.connection = connection;

    }

    public boolean CobrancaRetroatividadePercentualEstatico(int pCodContrato, int pMes, int pAno, int pOperacao) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        /**Função que retorna se em um determinado mês
         dever ser cobrada alguma retroatividade.

         --pOperacao = 1 - Retroatividade de remuneração.
         --pOperacao = 2 - Retroatividade de percentual.
        */
        int vRetroatividadeRemuneracao = 0;
        int vRetroatividadePercentual = 0;
        int vRetroatividadePercentualEstatico = 0;

        /**--Caso de busca de retroatividade na remuneração. */
        if (pOperacao == 1) {
            try {
                preparedStatement = connection.prepareStatement("SELECT COUNT(RR.COD) FROM TB_RETROATIVIDADE_REMUNERACAO JOIN TB_REMUNERACAO_FUN_CON RCCO ON RCCO.COD=RR.COD_REM_FUNCAO_CONTRATO " +
                        "JOIN TB_FUNCAO_CONTRATO FC ON FC.COD-RCCO.COD_FUNCAO_CONTRATO WHERE FC.COD_CONTRATO=? AND MONTH(DATA_COBRANCA)=? AND YEAR(DATA_COBRANCA)=?");
                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pMes);
                preparedStatement.setInt(3, pAno);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vRetroatividadeRemuneracao = resultSet.getInt(1);
                    if(vRetroatividadeRemuneracao > 0) {
                        return true;
                    }
                }
            }catch(SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        /**--Caso de busca de retroatividade nos percentuais.*/
        if (pOperacao == 2) {
            try{
                preparedStatement = connection.prepareStatement("SELECT COUNT(RPC.COD) FROM TB_RETROATIVIDADE_PERCENTUAL RPC " +
                        "JOIN TB_PERCENTUAL_CONTRATO PC ON PC.COD = RPC.COD_PERCENTUAL_CONTRATO WHERE PC.COD_CONTRATO=? " +
                        "AND MONTH(RPC.DATA_COBRANCA)=? AND YEAR(RPC.DATA_COBRANCA)=?");
                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pMes);
                preparedStatement.setInt(3, pAno);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vRetroatividadePercentual = resultSet.getInt(1);
                }
                preparedStatement = connection.prepareStatement("SELECT COUNT(RPE.COD) FROM tb_retro_percentual_estatico RPE WHERE RPE.COD_CONTRATO=? " +
                        "AND MONTH(RPE.DATA_COBRANCA)=? AND YEAR(RPE.DATA_COBRANCA)=?");
                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pMes);
                preparedStatement.setInt(3, pAno);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    vRetroatividadePercentualEstatico = resultSet.getInt(1);
                }
                if(vRetroatividadePercentual > 0 || vRetroatividadePercentualEstatico > 0) {
                    return true;
                }
            }catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return false;
    }

    public boolean CobrancaRetroatividade(int pCodContrato, int pMes, int pAno, int pOperacao) {

        /**
         --Função que retorna se em um determinado mês
         --dever ser cobrada alguma retroatividade.

         --pOperacao = 1 - Retroatividade de remuneração.
         --pOperacao = 2 - Retroatividade de percentual.
         */

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int vRetroatividadeRemuneracao = 0;
        int vRetroatividadePercentual = 0;
        int vRetroatividadePercentualEstatico = 0;

        /**--Caso de busca de retroatividade na remuneração. */
        if(pOperacao == 1) {
            try {
                preparedStatement = connection.prepareStatement("SELECT COUNT(RR.COD) FROM TB_RETROATIVIDADE_REMUNERACAO RR JOIN tb_remuneracao_fun_con RCCO ON RCCO.COD=RR.COD_REM_FUNCAO_CONTRATO " +
                        "JOIN tb_funcao_contrato FC ON FC.COD=RCCO.COD_FUNCAO_CONTRATO WHERE FC.COD_CONTRATO=? AND MONTH(DATA_COBRANCA)=? AND YEAR(DATA_COBRANCA)=?");
                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pMes);
                preparedStatement.setInt(3, pAno);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    vRetroatividadeRemuneracao = resultSet.getInt(1);
                    if(vRetroatividadeRemuneracao > 0) {
                        return true;
                    }
                }
            }catch(SQLException sqle) {
                sqle.printStackTrace();
            }

            /**--Caso de busca de retroatividade nos percentuais*/
            if(pOperacao == 2){
                try {
                    preparedStatement = connection.prepareStatement("SELECT COUNT(RPC.COD) FROM tb_retroatividade_percentual RPC JOIN tb_percentual_contrato PC ON PC.COD=RPC.COD_PERCENTUAL_CONTRATO " +
                            "WHERE PC.COD_CONTRATO=? AND MONTH(RPC.DATA_COBRANCA)=? AND YEAR(RPC.DATA_COBRANCA)=?");
                    preparedStatement.setInt(1, pCodContrato);
                    preparedStatement.setInt(2, pMes);
                    preparedStatement.setInt(3, pAno);
                    resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        vRetroatividadePercentual = resultSet.getInt(1);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    preparedStatement = connection.prepareStatement("SELECT COUNT (RPE.COD) FROM tb_retro_percentual_estatico RPE WHERE RPE.COD_CONTRATO=? AND " +
                            "MONTH(RPE.DATA_COBRANCA)=? AND YEAR(RPE.DATA_COBRANCA)=?");
                    preparedStatement.setInt(1, pCodContrato);
                    preparedStatement.setInt(2, pMes);
                    preparedStatement.setInt(3, pAno);
                    resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()) {
                        vRetroatividadePercentualEstatico = resultSet.getInt(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(vRetroatividadePercentual > 0 || vRetroatividadePercentualEstatico > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Função que retorna se em um determinado mês existe situação de retroatividade.
     * @param pCodContrato
     * @param pCodFuncaoContrato
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @return boolean
     */

    public boolean ExisteRetroatividade (int pCodContrato, int pCodFuncaoContrato, int pMes, int pAno, int pOperacao ) {

        //Checked.

        /**pOperacao = 1 - Retroatividade para remuneração.
           pOperacao = 2 - Retroatividade para percentual do contrato ou estático.*/

        ResultSet rs;
        int vRetroatividade = 0;
        int vRetroatividade2 = 0;

        /**Definição da data referência como primeiro dia do m~es de acordo com os argumentos passados.*/

        Date dataRef = Date.valueOf("" + pAno +"-"+ pMes + "-"+"01");

        /**Caso de busca de retroatividade na remuneração.*/

        if (pOperacao == 1) {

            /**Verifica se o mês se encontra dentro de um período de retroatividade.*/

            try {

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(RR.cod)" +
                                                                                       " FROM TB_RETROATIVIDADE_REMUNERACAO RR " +
                                                                                          "JOIN tb_remuneracao_fun_con RFC ON RFC.cod = RR.COD_REM_FUNCAO_CONTRATO" +
                                                                                       " WHERE RFC.COD_FUNCAO_CONTRATO=?" +
                                                                                         " AND ? >= DATEADD(DAY,1,EOMONTH(DATEADD(MONTH,-1, RR.INICIO)))" +
                                                                                         " AND ? <= RR.FIM");

                preparedStatement.setInt(1, pCodFuncaoContrato);
                preparedStatement.setDate(2, dataRef);
                preparedStatement.setDate(3, dataRef);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    if (rs.getInt(1) == 0) {

                        return false;

                    } else {

                        return true;

                    }

                }

            } catch (SQLException sqle) {

                throw new NullPointerException("Erro ao determinar se a remuneração está em uma situação de retroatividade.");

            }

            return false;

        }

        /** --Caso de busca de retroatividade em percentuais. */

        if (pOperacao == 2) {

            try {

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(rp.cod)" +
                                                                                       " FROM TB_RETROATIVIDADE_PERCENTUAL rp " +
                                                                                         "JOIN TB_PERCENTUAL_CONTRATO pc ON pc.cod = rp.cod_percentual_contrato" +
                                                                                       " WHERE pc.cod_contrato = ?" +
                                                                                         " AND ? >= DATEADD(DAY,1,EOMONTH(DATEADD(MONTH,-1, RP.INICIO)))" +
                                                                                         " AND ? <= RP.FIM");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setDate(2, dataRef);
                preparedStatement.setDate(3, dataRef);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    vRetroatividade = rs.getInt(1);

                }

                preparedStatement = connection.prepareStatement("SELECT COUNT(RPE.COD)" +
                                                                     " FROM tb_retro_percentual_estatico RPE" +
                                                                     " WHERE RPE.COD_CONTRATO=?" +
                                                                       " AND ? >= DATEADD(DAY, 1, EOMONTH(DATEADD(MONTH, -1, RPE.INICIO)))" +
                                                                       " AND ? <= RPE.FIM");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setDate(2, dataRef);
                preparedStatement.setDate(3, dataRef);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    vRetroatividade2 = rs.getInt(1);

                }

            } catch (SQLException sqle) {

                throw new NullPointerException("Erro ao determinar se os percentuais estão em uma situação de retroatividade.");

            }

            if (vRetroatividade > 0 || vRetroatividade2 > 0) {

                return true;

            }

        }

        return false;

    }

}
