package br.jus.stj.siscovi.calculos;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class Ferias {

    private Connection connection;

    public Ferias(Connection connection) {

        this.connection = connection;

    }

    /**
     * Função que retorna o número de dias que um terceirizado possui em um determinado
     * período aquisitivo.
     * @param pDataInicio
     * @param pDataFim
     * @return float
     */

    public float DiasPeriodoAquisitivo (Date pDataInicio, Date pDataFim) {

        //Checked.

        float vDiasAUsufruir = 0;
        float vContagemDeDias = 0;
        int vControle = 0;
        Date vDataInicio = null;
        Date vDataFim = null;

        /*Calcula o número de dias baseado no período aquisitivo.*/

        do {

            /*Inicializa a data de início no primeiro laço.*/

            if (vDataInicio == null) {

                vDataInicio = pDataInicio;

            }

            /*Define o fim do mês como dia 30 exceto para fevereiro.*/

            if (vDataInicio.toLocalDate().getMonthValue() != 2) {

                vDataFim = Date.valueOf(vDataInicio.toLocalDate().getYear() + "-" + vDataInicio.toLocalDate().getMonthValue() + "-30");

            } else {

                vDataFim = Date.valueOf(vDataInicio.toLocalDate().withDayOfMonth(vDataInicio.toLocalDate().lengthOfMonth()));

            }

            /**Ajusta a data fim para o final do período aquisitivo no mês correspondente.*/

            if ((vDataFim.toLocalDate().getMonthValue() == pDataFim.toLocalDate().getMonthValue()) &&
                (vDataFim.toLocalDate().getYear() == pDataFim.toLocalDate().getYear()) &&
                (pDataFim.toLocalDate().getDayOfMonth() != 31)) {

                vDataFim = pDataFim;

            }

            vContagemDeDias = vContagemDeDias + (int)(ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate()) + 1);

            /**Para o mês de fevereiro se equaliza o número de dias contados.*/

            if (vDataFim.toLocalDate().getMonthValue() == 2) {

                /**Se o mês for de 28 dias então soma-se 2 a contagem.*/

                if (vDataFim.toLocalDate().getDayOfMonth() == 28) {

                    vContagemDeDias = vContagemDeDias + 2;

                } else {

                    /**Se o mês não for de 28 dias ele é de 29.*/

                    if (vDataFim.toLocalDate().getDayOfMonth() == 29) {

                        vContagemDeDias = vContagemDeDias + 1;

                    }

                }

            }

            vDataInicio = Date.valueOf((vDataInicio.toLocalDate().withDayOfMonth(vDataInicio.toLocalDate().lengthOfMonth())).plusDays(1));

            if ((vDataFim.toLocalDate().getMonthValue() == pDataFim.toLocalDate().getMonthValue()) &&
                    (vDataFim.toLocalDate().getYear() == pDataFim.toLocalDate().getYear())) {

                vControle = 1;

            }

        } while (vControle == 0);

        if (vContagemDeDias > 360) {

            throw new NullPointerException("O período aquisitivo informado contabiliza mais de 360 dias.");

        }

        /**A cada 12 dias de trabalho o funcionário adquire 1 dia de férias,
         considerando um período de 360 dias, óbviamente.*/

        vDiasAUsufruir = vContagemDeDias/12;

        return vDiasAUsufruir;

    }

    /**
     * Função que retorna o número de parcelas de férias
     * concedidas a um funcionário em um determinado
     * contrato em um período aquisitivo específico.
     * período aquisitivo.
     * @param pCodTerceirizadoContrato
     * @param pDataInicio
     * @param pDataFim
     * @return int
     */

    public int ParcelasConcedidas (int pCodTerceirizadoContrato, Date pDataInicio, Date pDataFim) {

        //Checked.

        int vParcelasConcedidas = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(cod)" +
                                                                 " FROM tb_restituicao_ferias" +
                                                                 " WHERE cod_terceirizado_contrato = ?" +
                                                                   " AND data_inicio_periodo_aquisitivo = ?" +
                                                                   " AND data_fim_periodo_aquisitivo = ?");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setDate(2, pDataInicio);
            preparedStatement.setDate(3, pDataFim);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vParcelasConcedidas = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();
            throw new NullPointerException("Erro ao verificar parcelas de férias concedidas no período.");

        }

        return vParcelasConcedidas;

    }

    /**
     * Função que retorna o início ou fim do período aquisitivo de férias.
     * @param pCodTerceirizadoContrato
     * @param pOperacao
     * @return Date
     */

    public Date DataPeriodoAquisitivo (int pCodTerceirizadoContrato, int pOperacao) {

        /**pOperação:
         1 - Início do período aquisitivo.
         2 - Fim do período aquisitivo.*/

        Date vDataDisponibilizacao = null;
        Date vDataInicio = null;
        Date vDataFim = null;
        int vDiasUsufruidos = 0;
        int vSaldoFerias = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        /**Recupera a data de disponibilização do terceirizado.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT data_disponibilizacao" +
                    " FROM tb_terceirizado_contrato" +
                    " WHERE cod = ?;");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDataDisponibilizacao = resultSet.getDate(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar a data de disponibilização do terceirizado.");

        }

        /**Recupera a última data de período aquisitivo e os dias concedidos.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT data_inicio_periodo_aquisitivo," +
                    " data_fim_periodo_aquisitivo," +
                    " SUM(DATEDIFF(day, data_inicio_usufruto, data_fim_usufruto) + dias_vendidos + 1)" +
                    " FROM tb_restituicao_ferias" +
                    " WHERE cod_terceirizado_contrato = ?" +
                    " AND data_inicio_periodo_aquisitivo = (SELECT MAX(data_inicio_periodo_aquisitivo)" +
                    " FROM tb_restituicao_ferias" +
                    " WHERE cod_terceirizado_contrato = ?)" +
                    " GROUP BY data_inicio_periodo_aquisitivo, data_fim_periodo_aquisitivo");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDataInicio = resultSet.getDate(1);
                vDataFim = resultSet.getDate(2);
                vDiasUsufruidos = resultSet.getInt(3);

            } else {

                vDataInicio = null;
                vDataFim = null;
                vDiasUsufruidos = 0;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o último período de férias.");

        }

        if (vDataInicio != null) {

            vSaldoFerias = 30 - vDiasUsufruidos;

            if (vSaldoFerias <= 0) {

                vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));
                vDataFim = Date.valueOf(vDataInicio.toLocalDate().plusDays(364));

            }

        } else {

            vDataInicio = vDataDisponibilizacao;
            vDataFim = Date.valueOf(vDataInicio.toLocalDate().plusDays(364));

        }

        /**Retorna o início do período aquisitivo válido (corrente).*/

        if (pOperacao == 1) {

            return vDataInicio;

        }

        /**Retorna o fim do período aquisitivo válido (corrente).*/

        if (pOperacao == 2) {

            return vDataFim;

        }

        return null;

    }

    /**
     * Fução que retorna as datas relativas aos períodos de férias vencidas e integrais para fins de rescisão.
     * @param pCodTerceirizado
     * @param pDataDesligamento
     * @return Date
     */

    public Date RetornaDatasPeriodoFeriasRescisao (int pCodTerceirizado,
                                                   Date pDataDesligamento,
                                                   int pOperacao ) {

        //Operações:
        // 1 - Data inicio das férias integrais.
        // 2 - Data fim das férias integrais.
        // 3 - Data inicio das férias proporcionais.
        // 4 - Data fim das férias proporcionais.


        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Date vRetorno = null;

        Date vDataInicioFeriasIntegrais = DataPeriodoAquisitivo(pCodTerceirizado, 1);
        Date vDataFimFeriasIntegrais = DataPeriodoAquisitivo(pCodTerceirizado, 2);
        Date vDataInicioFeriasProporcionais = Date.valueOf(vDataFimFeriasIntegrais.toLocalDate().plusDays(1));
        Date vDataFimFeriasProporcionais = pDataDesligamento;
        Date vDataAuxiliar = Date.valueOf(vDataInicioFeriasProporcionais.toLocalDate().plusDays(364));

        // Caso o perído inicialmente recuperado supere a data de desligamento se ajustam o fim das férias
        // integrais e o inicio das férias proporionais.

        if (vDataFimFeriasIntegrais.after(pDataDesligamento)) {

            vDataFimFeriasIntegrais = null;
            vDataInicioFeriasProporcionais = vDataInicioFeriasIntegrais;
            vDataInicioFeriasIntegrais = null;

        }

        // Loop que perpassa todos os períodos de férias possíveis em ordem.

        while (vDataAuxiliar.before(pDataDesligamento)) {

            vDataInicioFeriasIntegrais = vDataInicioFeriasProporcionais;
            vDataFimFeriasIntegrais = vDataAuxiliar;
            vDataInicioFeriasProporcionais = Date.valueOf(vDataFimFeriasIntegrais.toLocalDate().plusDays(1));
            vDataAuxiliar = Date.valueOf(vDataInicioFeriasProporcionais.toLocalDate().plusDays(364));

        }

        // Seleção da data requerida.

        if (pOperacao == 1) {

            vRetorno = vDataInicioFeriasIntegrais;
        } else {

            if (pOperacao == 2) {

                vRetorno = vDataFimFeriasIntegrais;

            } else {

                if (pOperacao == 3) {

                    vRetorno = vDataInicioFeriasProporcionais;
                } else {

                    if (pOperacao == 4) {

                        vRetorno = pDataDesligamento;

                    }

                }

            }

        }

        return vRetorno;

    }


    /**
     * Função que retorna se existe registro de férias para um terceirizado em um contrato.
     * @param pCodTerceirizadoContrato
     * @return boolean
     */

    public boolean ExisteFeriasTerceirizado (int pCodTerceirizadoContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vNumeroRestituicoes = 0;

        String query = "SELECT COUNT(cod)\n" +
                       " FROM tb_restituicao_ferias\n" +
                       " WHERE cod_terceirizado_contrato = ?";

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

    /**
     * Função que retorna o número de dias que um funcionários usufruiu em um determinado período aquisitivo.
     * @param pCodTerceirizadoContrato
     * @param pDataInicio
     * @param pDataFim
     * @return float
     */

    public int RetornaDiasFeriasUsufruidosPeriodo (int pCodTerceirizadoContrato, Date pDataInicio, Date pDataFim) {

        int vDiasUsufruidos = -1;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {

            preparedStatement = connection.prepareStatement("SELECT SUM(DATEDIFF(day, DATA_INICIO_USUFRUTO, DATA_FIM_USUFRUTO) +  DIAS_VENDIDOS + 1)\n" +
                    " FROM tb_restituicao_ferias\n" +
                    " WHERE cod_terceirizado_contrato = ?\n" +
                    "   AND data_inicio_periodo_aquisitivo = ?\n" +
                    "   AND data_fim_periodo_aquisitivo = ?;");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setDate(2, pDataInicio);
            preparedStatement.setDate(3, pDataFim);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDiasUsufruidos = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar os dias de férias usufruídos no período.");

        }

        if(vDiasUsufruidos < 0) {

            vDiasUsufruidos = 0;

        }

        return vDiasUsufruidos;

    }

    /**
     * Função que retorna se aparcela de 14 dias foi concedida em um determinado período aquisitivo.
     * @param pCodTerceirizadoContrato
     * @param pDataInicio
     * @param pDataFim
     * @return float
     */

    public boolean RetornaParcela14DiasFeriasPeriodo (int pCodTerceirizadoContrato, Date pDataInicio, Date pDataFim) {

        boolean vParcela14Dias = false;
        int vRetorno = 0;

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(cod)\n" +
                    "    FROM tb_restituicao_ferias\n" +
                    "    WHERE cod_terceirizado_contrato = ?\n" +
                    "    AND data_inicio_periodo_aquisitivo = ?\n" +
                    "    AND data_fim_periodo_aquisitivo = ?\n" +
                    "    AND (DATEDIFF(day, DATA_INICIO_USUFRUTO, DATA_FIM_USUFRUTO) + 1) >= 14;");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setDate(2, pDataInicio);
            preparedStatement.setDate(3, pDataFim);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vRetorno = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível identificar se já houve registro da parcela de 14 dias.");

        }

        if(vRetorno > 0) {

            vParcela14Dias = true;

        }

        return vParcela14Dias;

    }

}