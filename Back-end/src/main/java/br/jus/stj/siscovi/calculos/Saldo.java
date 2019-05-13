package br.jus.stj.siscovi.calculos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Saldo {

    private Connection connection;

    public Saldo (Connection connection) {

        this.connection = connection;

    }

    /**
     * Função que retorna um valor relacionado ao saldo da conta vinculada em um ano específico.
     * @param pCodTerceirizadoContrato
     * @param pAno
     * @param pOperacao
     * @param pCodRubrica
     * @return float
     */

    public float getSaldoContaVinculada(int pCodTerceirizadoContrato, int pAno, int pOperacao, int pCodRubrica) {

         /*pOperacao = 1 - RETENÇÃO POR FUNCIONÁRIO
           pOperacao = 2 - RESTITUIÇÃO FÉRIAS POR FUNCIONÁRIO
           pOperacao = 3 - RESTITUICAO 13º POR FUNCIONÁRIO

           Legenda de rúbricas:
           1 - Férias
           2 - Terço constitucional
           3 - Décimo terceiro
           4 - FGTS
           5 - Multa do FGTS
           6 - Penalidade do FGTS
           7 - Incidência do submódulo 4.1 (corresponde a inciência de retenção)

           pCodRubrica especiais:
           100 - TOTAL
           101 - Incidência de férias
           102 - Incidência de terço de férias
           103 - Incidência de décimo terceiro*/

        float vFeriasRetido = 0;
        float vTercoConstitucionalRetido = 0;
        float vDecimoTerceiroRetido = 0;
        float vIncidenciaRetido = 0;
        float vMultaFGTSRetido = 0;
        float vTotalRetido = 0;
        float vFeriasRestituido = 0;
        float vTercoConstitucionalRestituido = 0;
        float vIncidenciaFeriasRestituido = 0;
        float vIncidenciaTercoRestituido = 0;
        float vDecimoTerceiroRestituido = 0;
        float vIncidencia13Restituido = 0;
        float vTotalRestituido = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        //Definição dos valores relacionados a retenção por funcionário.

        if (pOperacao == 1) {

            try {

                preparedStatement = connection.prepareStatement("SELECT SUM(tmr.ferias + CASE WHEN rtm.ferias IS NULL THEN 0 ELSE rtm.ferias END) AS \"Férias retido\",\n" +
                        "           SUM(tmr.terco_constitucional + CASE WHEN rtm.terco_constitucional IS NULL THEN 0 ELSE rtm.terco_constitucional END)  AS \"Abono de férias retido\",\n" +
                        "           SUM(tmr.decimo_terceiro + CASE WHEN rtm.decimo_terceiro IS NULL THEN 0 ELSE rtm.decimo_terceiro END) AS \"Décimo terceiro retido\",\n" +
                        "           SUM(tmr.incidencia_submodulo_4_1 + CASE WHEN rtm.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rtm.incidencia_submodulo_4_1 END) AS \"Incid. do submód. 4.1 retido\",\n" +
                        "           SUM(tmr.multa_fgts + CASE WHEN rtm.multa_fgts IS NULL THEN 0 ELSE rtm.multa_fgts END) AS \"Multa do FGTS retido\"," +
                        "           SUM(tmr.total + CASE WHEN rtm.total IS NULL THEN 0 ELSE rtm.total END) AS \"Total retido\"\n" +
                        "      FROM tb_total_mensal_a_reter tmr\n" +
                        "        JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato\n" +
                        "        LEFT JOIN tb_retroatividade_total_mensal rtm ON rtm.cod_total_mensal_a_reter = tmr.cod\n" +
                        "      WHERE YEAR(tmr.data_referencia) = ?\n" +
                        "        AND tc.cod = ?");

                preparedStatement.setInt(1, pAno);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRetido = resultSet.getFloat(1);
                    vTercoConstitucionalRetido = resultSet.getFloat(2);
                    vDecimoTerceiroRetido = resultSet.getFloat(3);
                    vIncidenciaRetido = resultSet.getFloat(4);
                    vMultaFGTSRetido = resultSet.getFloat(5);
                    vTotalRetido = resultSet.getFloat(6);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor retido. Parâmetros: \n" +
                        "pCodFuncionario = " + pCodTerceirizadoContrato + "\n" +
                        "pCodRubrica = " + pCodRubrica);

            }

        }

        //Definição dos valores relacionados a restituição de férias por funcionário.

        if (pOperacao == 2) {

            try {

                preparedStatement = connection.prepareStatement("SELECT SUM(rf.valor_ferias) AS \"Férias restituído\",\n" +
                        "           SUM(rf.valor_terco_constitucional) AS \"1/3 constitucional restituído\",\n" +
                        "           SUM(rf.incid_submod_4_1_ferias) AS \"Incid. de férias restituído\",\n" +
                        "           SUM(rf.incid_submod_4_1_terco) AS \"Incod. de terço restituído\",\n" +
                        "           SUM(rf.valor_ferias + rf.valor_terco_constitucional + rf.incid_submod_4_1_ferias + rf.incid_submod_4_1_terco) AS \"Total restituído\"\n" +
                        "      FROM tb_restituicao_ferias rf\n" +
                        "        JOIN tb_terceirizado_contrato tc ON tc.cod = rf.cod_terceirizado_contrato\n" +
                        "      WHERE YEAR(rf.data_inicio_periodo_aquisitivo) = ?\n" +
                        "        AND tc.cod = ?;");

                preparedStatement.setInt(1, pAno);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRestituido = resultSet.getFloat(1);
                    vTercoConstitucionalRestituido = resultSet.getFloat(2);
                    vIncidenciaFeriasRestituido = resultSet.getFloat(3);
                    vIncidenciaTercoRestituido = resultSet.getFloat(4);
                    vTotalRestituido = resultSet.getFloat(5);

                }

                preparedStatement = connection.prepareStatement("SELECT SUM(srf.valor_ferias) AS \"Férias restituído\",\n" +
                        "       SUM(srf.valor_terco) AS \"1/3 constitucional restituído\",\n" +
                        "       SUM(srf.incid_submod_4_1_ferias) AS \"Incid. de férias restituído\",\n" +
                        "       SUM(srf.incid_submod_4_1_terco) AS \"Incod. de terço restituído\",\n" +
                        "       SUM(srf.valor_ferias + srf.valor_terco + srf.incid_submod_4_1_ferias + srf.incid_submod_4_1_terco) AS \"Total restituído\"\n" +
                        "  FROM tb_restituicao_ferias rf\n" +
                        "    JOIN tb_terceirizado_contrato tc ON tc.cod = rf.cod_terceirizado_contrato\n" +
                        "    JOIN tb_saldo_residual_ferias srf ON rf.cod = srf.cod_restituicao_ferias\n" +
                        "  WHERE YEAR(rf.data_inicio_periodo_aquisitivo) = ?\n" +
                        "    AND tc.cod = ?\n" +
                        "    AND srf.restituido = 'S';");

                preparedStatement.setInt(1, pAno);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRestituido = resultSet.getFloat(1) + vFeriasRestituido;
                    vTercoConstitucionalRestituido = resultSet.getFloat(2) + vTercoConstitucionalRestituido;
                    vIncidenciaFeriasRestituido = resultSet.getFloat(3) + vIncidenciaFeriasRestituido;
                    vIncidenciaTercoRestituido = resultSet.getFloat(4) + vIncidenciaTercoRestituido;
                    vTotalRestituido = resultSet.getFloat(5) + vTotalRestituido;

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de férias.");

            }

        }

        //Definição dos valores relacionados a restituição de 13° por funcionário.

        if (pOperacao == 3) {

            try {

                preparedStatement = connection.prepareStatement("SELECT SUM(rdt.valor) AS \"Décimo terceiro restituído\",\n" +
                        "           SUM(rdt.incidencia_submodulo_4_1) AS \"Incid. de 13° restituído\",\n" +
                        "           SUM(rdt.valor + rdt.incidencia_submodulo_4_1) AS \"Total restituído\"\n" +
                        "      FROM tb_restituicao_decimo_terceiro rdt\n" +
                        "        JOIN tb_terceirizado_contrato tc ON tc.cod = rdt.cod_terceirizado_contrato\n" +
                        "      WHERE YEAR(rdt.data_inicio_contagem) = ?\n" +
                        "        AND tc.cod = ?;");

                preparedStatement.setInt(1, pAno);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vDecimoTerceiroRestituido = resultSet.getFloat(1);
                    vIncidencia13Restituido = resultSet.getFloat(2);
                    vTotalRestituido = resultSet.getFloat(3);

                }

                preparedStatement = connection.prepareStatement("SELECT SUM(srdt.valor) AS \"Décimo terceiro restituído\",\n" +
                        "           SUM(srdt.incidencia_submodulo_4_1) AS \"Incid. de 13° restituído\",\n" +
                        "           SUM(srdt.valor + srdt.incidencia_submodulo_4_1) AS \"Total restituído\"\n" +
                        "      FROM tb_restituicao_decimo_terceiro rdt\n" +
                        "       JOIN tb_terceirizado_contrato tc ON tc.cod = rdt.cod_terceirizado_contrato\n" +
                        "        JOIN tb_saldo_residual_dec_ter srdt ON srdt.cod_restituicao_dec_terceiro = rdt.cod\n" +
                        "      WHERE YEAR(rdt.data_inicio_contagem) = ?\n" +
                        "       AND tc.cod = ?\n" +
                        "        AND srdt.restituido = 'S';");

                preparedStatement.setInt(1, pAno);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vDecimoTerceiroRestituido = resultSet.getFloat(1) +  vDecimoTerceiroRestituido;
                    vIncidencia13Restituido = resultSet.getFloat(2) + vIncidencia13Restituido;
                    vTotalRestituido = resultSet.getFloat(3) + vTotalRestituido;

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de 13°.");

            }

        }

        /*Retorno do valor de férias retido.*/

        if ((pOperacao == 1) && (pCodRubrica == 1)) {

        return vFeriasRetido;

        }

        /*Retorno do valor de terço constitucional retido.*/

        if (pOperacao == 1 && pCodRubrica == 2) {

            return vTercoConstitucionalRetido;

        }

        /*Retorno do valor de décimo terceiro retido.*/

        if (pOperacao == 1 && pCodRubrica == 3) {

            return vDecimoTerceiroRetido;

        }

        /*Retorno do valor de incidência retido.*/

        if (pOperacao == 1 && pCodRubrica == 7) {

            return vIncidenciaRetido;

        }

        /*Retorno do valor de multa do FGTS retido.*/

        if (pOperacao == 1 && pCodRubrica == 5) {

            return vMultaFGTSRetido;

        }

        /*Retorno do valor total retido.*/

        if (pOperacao == 1 && pCodRubrica == 100) {

            return vTotalRetido;

        }

        /*Retorno do valor de férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 1) {

            return vFeriasRestituido;

        }

        /*Retorno do valor de terço constitucional restituído.*/

        if (pOperacao == 2 && pCodRubrica == 2) {

            return vTercoConstitucionalRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 101) {

            return vIncidenciaFeriasRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 102) {

            return vIncidenciaTercoRestituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 2 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        /*Retorno do valor de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 3) {

            return vDecimoTerceiroRestituido;

        }

        /*Retorno do valor de incidência de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 103) {

            return vIncidencia13Restituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 3 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        return 0;

    }

    /**
     * Função que retorna um valor relacionado ao saldo total individual da conta vinculada.
     * @param pCodTerceirizadoContrato
     * @param pOperacao
     * @param pCodRubrica
     * @return float
     */

    public float getSaldoIndividualContaVinculada(int pCodTerceirizadoContrato, int pOperacao, int pCodRubrica) {

        //Checked.

        /*pOperacao = 1 - RETENÇÃO POR FUNCIONÁRIO
         pOperacao = 2 - RESTITUIÇÃO FÉRIAS POR FUNCIONÁRIO
         pOperacao = 3 - RESTITUICAO 13º POR FUNCIONÁRIO

         Legenda de rúbricas:
         1 - Férias
         2 - Terço constitucional
         3 - Décimo terceiro
         4 - FGTS
         5 - Multa do FGTS
         6 - Penalidade do FGTS
         7 - Incidência do submódulo 4.1 (corresponde a inciência de retenção)

         pCodRubrica especiais:
         100 - TOTAL
         101 - Incidência de férias
         102 - Incidência de terço de férias
         103 - Incidência de décimo terceiro*/

        float vFeriasRetido = 0;
        float vTercoConstitucionalRetido = 0;
        float vDecimoTerceiroRetido = 0;
        float vIncidenciaRetido = 0;
        float vMultaFGTSRetido = 0;
        float vTotalRetido = 0;
        float vFeriasRestituido = 0;
        float vTercoConstitucionalRestituido = 0;
        float vIncidenciaFeriasRestituido = 0;
        float vIncidenciaTercoRestituido = 0;
        float vDecimoTerceiroRestituido = 0;
        float vIncidencia13Restituido = 0;
        float vTotalRestituido = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        //Definição dos valores relacionados a retenção por funcionário.

        if (pOperacao == 1) {

            try {

                preparedStatement = connection.prepareStatement("SELECT SUM(tmr.ferias + CASE WHEN rtm.ferias IS NULL THEN 0 ELSE rtm.ferias END) AS \"Férias retido\",\n" +
                        "           SUM(tmr.terco_constitucional + CASE WHEN rtm.terco_constitucional IS NULL THEN 0 ELSE rtm.terco_constitucional END)  AS \"Abono de férias retido\",\n" +
                        "           SUM(tmr.decimo_terceiro + CASE WHEN rtm.decimo_terceiro IS NULL THEN 0 ELSE rtm.decimo_terceiro END) AS \"Décimo terceiro retido\",\n" +
                        "           SUM(tmr.incidencia_submodulo_4_1 + CASE WHEN rtm.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rtm.incidencia_submodulo_4_1 END) AS \"Incid. do submód. 4.1 retido\",\n" +
                        "           SUM(tmr.multa_fgts + CASE WHEN rtm.multa_fgts IS NULL THEN 0 ELSE rtm.multa_fgts END) AS \"Multa do FGTS retido\"," +
                        "           SUM(tmr.total + CASE WHEN rtm.total IS NULL THEN 0 ELSE rtm.total END) AS \"Total retido\"\n" +
                        "      FROM tb_total_mensal_a_reter tmr\n" +
                        "        JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato\n" +
                        "        LEFT JOIN tb_retroatividade_total_mensal rtm ON rtm.cod_total_mensal_a_reter = tmr.cod\n" +
                        "      WHERE tc.cod = ? AND tmr.RETIDO = 'S'");

                preparedStatement.setInt(1, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRetido = resultSet.getFloat(1);
                    vTercoConstitucionalRetido = resultSet.getFloat(2);
                    vDecimoTerceiroRetido = resultSet.getFloat(3);
                    vIncidenciaRetido = resultSet.getFloat(4);
                    vMultaFGTSRetido = resultSet.getFloat(5);
                    vTotalRetido = resultSet.getFloat(6);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor retido. Parâmetros: \n" +
                        "pCodFuncionario = " + pCodTerceirizadoContrato + "\n" +
                        "pCodRubrica = " + pCodRubrica);

            }

        }

        //Definição dos valores relacionados a restituição de férias por funcionário.

        if (pOperacao == 2) {

            try {

                preparedStatement = connection.prepareStatement("SELECT ROUND(SUM(CASE WHEN rf.valor_ferias IS NULL THEN 0 ELSE rf.valor_ferias END + CASE WHEN srf.valor_ferias IS NULL THEN 0 ELSE srf.valor_ferias END + CASE WHEN r.valor_ferias IS NULL THEN 0 ELSE r.valor_ferias END + CASE WHEN r.valor_ferias_prop IS NULL THEN 0 ELSE r.valor_ferias_prop END + CASE WHEN srr.valor_ferias IS NULL THEN 0 ELSE srr.valor_ferias END + CASE WHEN srr.valor_ferias_prop IS NULL THEN 0 ELSE srr.valor_ferias_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.valor_terco_constitucional IS NULL THEN 0 ELSE rf.valor_terco_constitucional END + CASE WHEN srf.valor_terco IS NULL THEN 0 ELSE srf.valor_terco END + CASE WHEN r.valor_terco IS NULL THEN 0 ELSE r.valor_terco END + CASE WHEN srr.valor_terco IS NULL THEN 0 ELSE srr.valor_terco END + CASE WHEN r.valor_terco_prop IS NULL THEN 0 ELSE r.valor_terco_prop END + CASE WHEN srr.valor_terco_prop IS NULL THEN 0 ELSE srr.valor_terco_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE rf.incid_submod_4_1_ferias END + CASE WHEN srf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srf.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias END + CASE WHEN srr.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias_prop END + CASE WHEN srr.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.incid_submod_4_1_terco IS NULL THEN 0 ELSE rf.incid_submod_4_1_terco END + CASE WHEN srf.incid_submod_4_1_terco IS NULL THEN 0 ELSE srf.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco IS NULL THEN 0 ELSE r.incid_submod_4_1_terco END + CASE WHEN srr.incid_submod_4_1_terco IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_terco_prop END + CASE WHEN srr.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.valor_ferias IS NULL THEN 0 ELSE rf.valor_ferias END + CASE WHEN srf.valor_ferias IS NULL THEN 0 ELSE srf.valor_ferias END + CASE WHEN r.valor_ferias IS NULL THEN 0 ELSE r.valor_ferias END + CASE WHEN r.valor_ferias_prop IS NULL THEN 0 ELSE r.valor_ferias_prop END + CASE WHEN srr.valor_ferias IS NULL THEN 0 ELSE srr.valor_ferias END + CASE WHEN srr.valor_ferias_prop IS NULL THEN 0 ELSE srr.valor_ferias_prop END +\n" +
                        "                 CASE WHEN rf.valor_terco_constitucional IS NULL THEN 0 ELSE rf.valor_terco_constitucional END + CASE WHEN srf.valor_terco IS NULL THEN 0 ELSE srf.valor_terco END + CASE WHEN r.valor_terco IS NULL THEN 0 ELSE r.valor_terco END + CASE WHEN srr.valor_terco IS NULL THEN 0 ELSE srr.valor_terco END + CASE WHEN r.valor_terco_prop IS NULL THEN 0 ELSE r.valor_terco_prop END + CASE WHEN srr.valor_terco_prop IS NULL THEN 0 ELSE srr.valor_terco_prop END +\n" +
                        "                 CASE WHEN rf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE rf.incid_submod_4_1_ferias END + CASE WHEN srf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srf.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias END + CASE WHEN srr.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias_prop END + CASE WHEN srr.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias_prop END +\n" +
                        "                 CASE WHEN rf.incid_submod_4_1_terco IS NULL THEN 0 ELSE rf.incid_submod_4_1_terco END + CASE WHEN srf.incid_submod_4_1_terco IS NULL THEN 0 ELSE srf.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco IS NULL THEN 0 ELSE r.incid_submod_4_1_terco END + CASE WHEN srr.incid_submod_4_1_terco IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_terco_prop END + CASE WHEN srr.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco_prop END), 2)\n" +
                        "FROM tb_terceirizado_contrato tc\n" +
                        "       LEFT JOIN tb_restituicao_ferias rf ON tc.cod = rf.cod_terceirizado_contrato\n" +
                        "       LEFT JOIN tb_saldo_residual_ferias srf ON srf.cod_restituicao_ferias = rf.cod\n" +
                        "       LEFT JOIN tb_restituicao_rescisao r ON r.cod_terceirizado_contrato = tc.cod\n" +
                        "       LEFT JOIN tb_saldo_residual_rescisao srr ON srr.COD_RESTITUICAO_RESCISAO = r.cod\n" +
                        "WHERE tc.cod = ?\n" +
                        "  AND (srf.RESTITUIDO = 'S'\n" +
                        "         OR r.restituido = 'S'\n" +
                        "         OR rf.RESTITUIDO = 'S'\n" +
                        "         OR srr.RESTITUIDO = 'S');");

                preparedStatement.setInt(1, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRestituido = resultSet.getFloat(1);
                    vTercoConstitucionalRestituido = resultSet.getFloat(2);
                    vIncidenciaFeriasRestituido = resultSet.getFloat(3);
                    vIncidenciaTercoRestituido = resultSet.getFloat(4);
                    vTotalRestituido = resultSet.getFloat(5);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de férias.");

            }

        }

        //Definição dos valores relacionados a restituição de 13° por funcionário.

        if (pOperacao == 3) {

            try {

                preparedStatement = connection.prepareStatement("SELECT ROUND(SUM(CASE WHEN rdt.valor IS NULL THEN 0 ELSE rdt.valor END + CASE WHEN srdt.valor IS NULL THEN 0 ELSE srdt.valor END + CASE WHEN r.valor_decimo_terceiro IS NULL THEN 0 ELSE r.valor_decimo_terceiro END + CASE WHEN srr.valor_decimo_terceiro IS NULL THEN 0 ELSE srr.valor_decimo_terceiro END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rdt.incidencia_submodulo_4_1 END + CASE WHEN srdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE srdt.incidencia_submodulo_4_1 END + CASE WHEN r.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE r.incid_submod_4_1_dec_terceiro END + CASE WHEN srr.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE srr.incid_submod_4_1_dec_terceiro END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rdt.valor IS NULL THEN 0 ELSE rdt.valor END + CASE WHEN srdt.valor IS NULL THEN 0 ELSE srdt.valor END + CASE WHEN r.valor_decimo_terceiro IS NULL THEN 0 ELSE r.valor_decimo_terceiro END + CASE WHEN srr.valor_decimo_terceiro IS NULL THEN 0 ELSE srr.valor_decimo_terceiro END +\n" +
                        "                 CASE WHEN rdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rdt.incidencia_submodulo_4_1 END + CASE WHEN srdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE srdt.incidencia_submodulo_4_1 END + CASE WHEN r.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE r.incid_submod_4_1_dec_terceiro END + CASE WHEN srr.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE srr.incid_submod_4_1_dec_terceiro END), 2)\n" +
                        "  FROM tb_terceirizado_contrato tc\n" +
                        "    LEFT JOIN tb_restituicao_decimo_terceiro rdt ON tc.cod = rdt.cod_terceirizado_contrato\n" +
                        "    LEFT JOIN tb_saldo_residual_dec_ter srdt ON srdt.cod_restituicao_dec_terceiro = rdt.cod\n" +
                        "    LEFT JOIN tb_restituicao_rescisao r ON r.cod_terceirizado_contrato = tc.cod\n" +
                        "    LEFT JOIN tb_saldo_residual_rescisao srr ON srr.COD_RESTITUICAO_RESCISAO = r.cod\n" +
                        "  WHERE tc.cod = ?\n" +
                        "    AND (srdt.RESTITUIDO = 'S'\n" +
                        "         OR r.restituido = 'S'\n" +
                        "         OR rdt.RESTITUIDO = 'S'\n" +
                        "         OR srr.RESTITUIDO = 'S');");

                preparedStatement.setInt(1, pCodTerceirizadoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vDecimoTerceiroRestituido = resultSet.getFloat(1);
                    vIncidencia13Restituido = resultSet.getFloat(2);
                    vTotalRestituido = resultSet.getFloat(3);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de 13°.");

            }

        }

        /*Retorno do valor de férias retido.*/

        if ((pOperacao == 1) && (pCodRubrica == 1)) {

            return vFeriasRetido;

        }

        /*Retorno do valor de terço constitucional retido.*/

        if (pOperacao == 1 && pCodRubrica == 2) {

            return vTercoConstitucionalRetido;

        }

        /*Retorno do valor de décimo terceiro retido.*/

        if (pOperacao == 1 && pCodRubrica == 3) {

            return vDecimoTerceiroRetido;

        }

        /*Retorno do valor de incidência retido.*/

        if (pOperacao == 1 && pCodRubrica == 7) {

            return vIncidenciaRetido;

        }

        /*Retorno do valor de multa do FGTS retido.*/

        if (pOperacao == 1 && pCodRubrica == 5) {

            return vMultaFGTSRetido;

        }

        /*Retorno do valor total retido.*/

        if (pOperacao == 1 && pCodRubrica == 100) {

            return vTotalRetido;

        }

        /*Retorno do valor de férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 1) {

            return vFeriasRestituido;

        }

        /*Retorno do valor de terço constitucional restituído.*/

        if (pOperacao == 2 && pCodRubrica == 2) {

            return vTercoConstitucionalRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 101) {

            return vIncidenciaFeriasRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 102) {

            return vIncidenciaTercoRestituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 2 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        /*Retorno do valor de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 3) {

            return vDecimoTerceiroRestituido;

        }

        /*Retorno do valor de incidência de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 103) {

            return vIncidencia13Restituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 3 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        return 0;

    }

    public float getSaldoFuncaoContaVinculada(int pCodContrato, int pCodFuncaoContrato, int pOperacao, int pCodRubrica) {

        //Checked.

        /*pOperacao = 1 - RETENÇÃO POR FUNÇÃO
         pOperacao = 2 - RESTITUIÇÃO FÉRIAS POR FUNÇÃO
         pOperacao = 3 - RESTITUICAO 13º POR FUNÇÃO

         Legenda de rúbricas:
         1 - Férias
         2 - Terço constitucional
         3 - Décimo terceiro
         4 - FGTS
         5 - Multa do FGTS
         6 - Penalidade do FGTS
         7 - Incidência do submódulo 4.1 (corresponde a inciência de retenção)

         pCodRubrica especiais:
         100 - TOTAL
         101 - Incidência de férias
         102 - Incidência de terço de férias
         103 - Incidência de décimo terceiro*/

        float vFeriasRetido = 0;
        float vTercoConstitucionalRetido = 0;
        float vDecimoTerceiroRetido = 0;
        float vIncidenciaRetido = 0;
        float vMultaFGTSRetido = 0;
        float vTotalRetido = 0;
        float vFeriasRestituido = 0;
        float vTercoConstitucionalRestituido = 0;
        float vIncidenciaFeriasRestituido = 0;
        float vIncidenciaTercoRestituido = 0;
        float vDecimoTerceiroRestituido = 0;
        float vIncidencia13Restituido = 0;
        float vTotalRestituido = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        //Definição dos valores relacionados a retenção total.

        if (pOperacao == 1) {

            try {

                preparedStatement = connection.prepareStatement("SELECT SUM(tmr.ferias + CASE WHEN rtm.ferias IS NULL THEN 0 ELSE rtm.ferias END)," +
                                                                          " SUM(tmr.terco_constitucional + CASE WHEN rtm.terco_constitucional IS NULL THEN 0 ELSE rtm.terco_constitucional END)," +
                                                                          " SUM(tmr.decimo_terceiro + CASE WHEN rtm.decimo_terceiro IS NULL THEN 0 ELSE rtm.decimo_terceiro END)," +
                                                                          " SUM(tmr.incidencia_submodulo_4_1 + CASE WHEN rtm.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rtm.incidencia_submodulo_4_1 END)," +
                                                                          " SUM(tmr.multa_fgts + CASE WHEN rtm.multa_fgts IS NULL THEN 0 ELSE rtm.multa_fgts END)," +
                                                                          " SUM(tmr.total + CASE WHEN rtm.total IS NULL THEN 0 ELSE rtm.total END)" +
                                                                     " FROM tb_total_mensal_a_reter tmr\n" +
                                                                       " JOIN tb_terceirizado_contrato tc ON tc.cod = tmr.cod_terceirizado_contrato\n" +
                                                                       " LEFT JOIN tb_retroatividade_total_mensal rtm ON rtm.cod_total_mensal_a_reter = tmr.cod\n" +
                                                                       " JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod\n" +
                                                                       " JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato\n" +
                                                                     " WHERE fc.cod_contrato = ?\n" +
                                                                       " AND fc.cod = ?");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodFuncaoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRetido = resultSet.getFloat(1);
                    vTercoConstitucionalRetido = resultSet.getFloat(2);
                    vDecimoTerceiroRetido = resultSet.getFloat(3);
                    vIncidenciaRetido = resultSet.getFloat(4);
                    vMultaFGTSRetido = resultSet.getFloat(5);
                    vTotalRetido = resultSet.getFloat(6);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor retido.");

            }

        }

        //Definição dos valores relacionados a restituição de férias total.

        if (pOperacao == 2) {

            try {

                preparedStatement = connection.prepareStatement("SELECT ROUND(SUM(CASE WHEN rf.valor_ferias IS NULL THEN 0 ELSE rf.valor_ferias END + CASE WHEN srf.valor_ferias IS NULL THEN 0 ELSE srf.valor_ferias END + CASE WHEN r.valor_ferias IS NULL THEN 0 ELSE r.valor_ferias END + CASE WHEN r.valor_ferias_prop IS NULL THEN 0 ELSE r.valor_ferias_prop END + CASE WHEN srr.valor_ferias IS NULL THEN 0 ELSE srr.valor_ferias END + CASE WHEN srr.valor_ferias_prop IS NULL THEN 0 ELSE srr.valor_ferias_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.valor_terco_constitucional IS NULL THEN 0 ELSE rf.valor_terco_constitucional END + CASE WHEN srf.valor_terco IS NULL THEN 0 ELSE srf.valor_terco END + CASE WHEN r.valor_terco IS NULL THEN 0 ELSE r.valor_terco END + CASE WHEN srr.valor_terco IS NULL THEN 0 ELSE srr.valor_terco END + CASE WHEN r.valor_terco_prop IS NULL THEN 0 ELSE r.valor_terco_prop END + CASE WHEN srr.valor_terco_prop IS NULL THEN 0 ELSE srr.valor_terco_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE rf.incid_submod_4_1_ferias END + CASE WHEN srf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srf.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias END + CASE WHEN srr.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias_prop END + CASE WHEN srr.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.incid_submod_4_1_terco IS NULL THEN 0 ELSE rf.incid_submod_4_1_terco END + CASE WHEN srf.incid_submod_4_1_terco IS NULL THEN 0 ELSE srf.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco IS NULL THEN 0 ELSE r.incid_submod_4_1_terco END + CASE WHEN srr.incid_submod_4_1_terco IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_terco_prop END + CASE WHEN srr.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco_prop END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rf.valor_ferias IS NULL THEN 0 ELSE rf.valor_ferias END + CASE WHEN srf.valor_ferias IS NULL THEN 0 ELSE srf.valor_ferias END + CASE WHEN r.valor_ferias IS NULL THEN 0 ELSE r.valor_ferias END + CASE WHEN r.valor_ferias_prop IS NULL THEN 0 ELSE r.valor_ferias_prop END + CASE WHEN srr.valor_ferias IS NULL THEN 0 ELSE srr.valor_ferias END + CASE WHEN srr.valor_ferias_prop IS NULL THEN 0 ELSE srr.valor_ferias_prop END +\n" +
                        "                 CASE WHEN rf.valor_terco_constitucional IS NULL THEN 0 ELSE rf.valor_terco_constitucional END + CASE WHEN srf.valor_terco IS NULL THEN 0 ELSE srf.valor_terco END + CASE WHEN r.valor_terco IS NULL THEN 0 ELSE r.valor_terco END + CASE WHEN srr.valor_terco IS NULL THEN 0 ELSE srr.valor_terco END + CASE WHEN r.valor_terco_prop IS NULL THEN 0 ELSE r.valor_terco_prop END + CASE WHEN srr.valor_terco_prop IS NULL THEN 0 ELSE srr.valor_terco_prop END +\n" +
                        "                 CASE WHEN rf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE rf.incid_submod_4_1_ferias END + CASE WHEN srf.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srf.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias END + CASE WHEN srr.incid_submod_4_1_ferias IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias END + CASE WHEN r.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_ferias_prop END + CASE WHEN srr.incid_submod_4_1_ferias_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_ferias_prop END +\n" +
                        "                 CASE WHEN rf.incid_submod_4_1_terco IS NULL THEN 0 ELSE rf.incid_submod_4_1_terco END + CASE WHEN srf.incid_submod_4_1_terco IS NULL THEN 0 ELSE srf.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco IS NULL THEN 0 ELSE r.incid_submod_4_1_terco END + CASE WHEN srr.incid_submod_4_1_terco IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco END + CASE WHEN r.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE r.incid_submod_4_1_terco_prop END + CASE WHEN srr.incid_submod_4_1_terco_prop IS NULL THEN 0 ELSE srr.incid_submod_4_1_terco_prop END), 2)\n" +
                        "    FROM tb_terceirizado_contrato tc\n" +
                        "       JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod\n" +
                        "       JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato\n" +
                        "       LEFT JOIN tb_restituicao_ferias rf ON tc.cod = rf.cod_terceirizado_contrato\n" +
                        "       LEFT JOIN tb_saldo_residual_ferias srf ON srf.cod_restituicao_ferias = rf.cod\n" +
                        "       LEFT JOIN tb_restituicao_rescisao r ON r.cod_terceirizado_contrato = tc.cod\n" +
                        "       LEFT JOIN tb_saldo_residual_rescisao srr ON srr.COD_RESTITUICAO_RESCISAO = r.cod\n" +
                        "WHERE fc.cod_contrato = ?\n" +
                        "  AND fc.cod = ?\n" +
                        "  AND (srf.RESTITUIDO = 'S'\n" +
                        "       OR r.restituido = 'S'\n" +
                        "       OR rf.RESTITUIDO = 'S'\n" +
                        "       OR srr.RESTITUIDO = 'S')");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodFuncaoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vFeriasRestituido = resultSet.getFloat(1);
                    vTercoConstitucionalRestituido = resultSet.getFloat(2);
                    vIncidenciaFeriasRestituido = resultSet.getFloat(3);
                    vIncidenciaTercoRestituido = resultSet.getFloat(4);
                    vTotalRestituido = resultSet.getFloat(5);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de férias.");

            }

        }

        //Definição dos valores relacionados a restituição de décimo terceiro total.

        if (pOperacao == 3) {

            try {

                preparedStatement = connection.prepareStatement("SELECT ROUND(SUM(CASE WHEN rdt.valor IS NULL THEN 0 ELSE rdt.valor END + CASE WHEN srdt.valor IS NULL THEN 0 ELSE srdt.valor END + CASE WHEN r.valor_decimo_terceiro IS NULL THEN 0 ELSE r.valor_decimo_terceiro END + CASE WHEN srr.valor_decimo_terceiro IS NULL THEN 0 ELSE srr.valor_decimo_terceiro END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rdt.incidencia_submodulo_4_1 END + CASE WHEN srdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE srdt.incidencia_submodulo_4_1 END + CASE WHEN r.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE r.incid_submod_4_1_dec_terceiro END + CASE WHEN srr.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE srr.incid_submod_4_1_dec_terceiro END), 2),\n" +
                        "       ROUND(SUM(CASE WHEN rdt.valor IS NULL THEN 0 ELSE rdt.valor END + CASE WHEN srdt.valor IS NULL THEN 0 ELSE srdt.valor END + CASE WHEN r.valor_decimo_terceiro IS NULL THEN 0 ELSE r.valor_decimo_terceiro END + CASE WHEN srr.valor_decimo_terceiro IS NULL THEN 0 ELSE srr.valor_decimo_terceiro END +\n" +
                        "                 CASE WHEN rdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE rdt.incidencia_submodulo_4_1 END + CASE WHEN srdt.incidencia_submodulo_4_1 IS NULL THEN 0 ELSE srdt.incidencia_submodulo_4_1 END + CASE WHEN r.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE r.incid_submod_4_1_dec_terceiro END + CASE WHEN srr.incid_submod_4_1_dec_terceiro IS NULL THEN 0 ELSE srr.incid_submod_4_1_dec_terceiro END), 2)\n" +
                        "FROM tb_terceirizado_contrato tc\n" +
                        "       JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod\n" +
                        "       JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato\n" +
                        "       LEFT JOIN tb_restituicao_decimo_terceiro rdt ON tc.cod = rdt.cod_terceirizado_contrato\n" +
                        "       LEFT JOIN tb_saldo_residual_dec_ter srdt ON srdt.cod_restituicao_dec_terceiro = rdt.cod\n" +
                        "       LEFT JOIN tb_restituicao_rescisao r ON r.cod_terceirizado_contrato = tc.cod\n" +
                        "       LEFT JOIN tb_saldo_residual_rescisao srr ON srr.COD_RESTITUICAO_RESCISAO = r.cod\n" +
                        "WHERE fc.cod_contrato = ?\n" +
                        "  AND fc.cod = ?\n" +
                        "  AND (srdt.RESTITUIDO = 'S'\n" +
                        "         OR r.restituido = 'S'\n" +
                        "         OR rdt.RESTITUIDO = 'S'\n" +
                        "         OR srr.RESTITUIDO = 'S')\n");

                preparedStatement.setInt(1, pCodContrato);
                preparedStatement.setInt(2, pCodFuncaoContrato);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    vDecimoTerceiroRestituido = resultSet.getFloat(1);
                    vIncidencia13Restituido = resultSet.getFloat(2);
                    vTotalRestituido = resultSet.getFloat(3);

                }

            } catch (SQLException sqle) {

                sqle.printStackTrace();

                throw new NullPointerException("Não foi possível recuperar o valor restituído de 13°.");

            }

        }

        /*Retorno do valor de férias retido.*/

        if ((pOperacao == 1) && (pCodRubrica == 1)) {

            return vFeriasRetido;

        }

        /*Retorno do valor de terço constitucional retido.*/

        if (pOperacao == 1 && pCodRubrica == 2) {

            return vTercoConstitucionalRetido;

        }

        /*Retorno do valor de décimo terceiro retido.*/

        if (pOperacao == 1 && pCodRubrica == 3) {

            return vDecimoTerceiroRetido;

        }

        /*Retorno do valor de incidência retido.*/

        if (pOperacao == 1 && pCodRubrica == 7) {

            return vIncidenciaRetido;

        }

        /*Retorno do valor de multa do FGTS retido.*/

        if (pOperacao == 1 && pCodRubrica == 5) {

            return vMultaFGTSRetido;

        }

        /*Retorno do valor total retido.*/

        if (pOperacao == 1 && pCodRubrica == 100) {

            return vTotalRetido;

        }

        /*Retorno do valor de férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 1) {

            return vFeriasRestituido;

        }

        /*Retorno do valor de terço constitucional restituído.*/

        if (pOperacao == 2 && pCodRubrica == 2) {

            return vTercoConstitucionalRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 101) {

            return vIncidenciaFeriasRestituido;

        }

        /*Retorno do valor de incidência sobre férias restituído.*/

        if (pOperacao == 2 && pCodRubrica == 102) {

            return vIncidenciaTercoRestituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 2 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        /*Retorno do valor de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 3) {

            return vDecimoTerceiroRestituido;

        }

        /*Retorno do valor de incidência de décimo terceiro restituído.*/

        if (pOperacao == 3 && pCodRubrica == 103) {

            return vIncidencia13Restituido;

        }

        /*Retorno do valor total restituído de férias.*/

        if (pOperacao == 3 && pCodRubrica == 100) {

            return vTotalRestituido;

        }

        return 0;

    }

}