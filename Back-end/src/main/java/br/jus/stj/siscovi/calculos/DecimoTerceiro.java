package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;

import java.sql.*;

public class DecimoTerceiro {

    private Connection connection;

    public DecimoTerceiro (Connection connection) {

        this.connection = connection;

    }

    /**
     * Função que retorna a data de inicio da contagem do décimo terceiro
     * de um terceirizado em um determinado contrato em determinado ano.
     * @param pCodTerceirizadoContrato
     * @param pAnoContagem
     */

    public Date RetornaDataInicioContagem (int pCodTerceirizadoContrato, int pAnoContagem) {

        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        Date vDataDisponibilizacao = null;
        Date vDataRetorno = null;

        /* Determina se a data de inicio da contagem do 13 é a data de disponibilização ou o primeiro dia do ano de desligamento. */

        vDataDisponibilizacao = consulta.RetornaDataDisponibilizacaoTerceirizado(pCodTerceirizadoContrato);

        if (vDataDisponibilizacao.toLocalDate().getYear() == pAnoContagem) {

            vDataRetorno = vDataDisponibilizacao;

        }

        else {

            vDataRetorno = Date.valueOf(pAnoContagem + "-01-01");

        }

        return vDataRetorno;

    }

    public boolean RetornaStatusAnalise (int pCodTerceirizadoContrato) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vNumeroRestituicoes = 0;

        String query = "SELECT COUNT(cod)\n" +
                "FROM tb_restituicao_decimo_terceiro\n" +
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

    public boolean RetornaRestituido (int pCodTerceirizadoContrato, int pAnoContagem) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        String sql = "SELECT * FROM tb_restituicao_decimo_terceiro " +
                "WHERE AUTORIZADO = 'S' AND RESTITUIDO = 'S' AND year(DATA_INICIO_CONTAGEM) = ? AND COD_TERCEIRIZADO_CONTRATO = ? AND (PARCELA = 0 OR PARCELA = 2)";

        try {

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, pAnoContagem);
            preparedStatement.setInt(2, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return true;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar restituições de férias anteriores.");

        }

        return false;
    }
    public boolean RetornaRestituidoAnoPassado (int pCodTerceirizadoContrato, int pAnoContagem, int anoDisponibilizacao) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        String sql = "SELECT * FROM tb_restituicao_decimo_terceiro " +
                "WHERE AUTORIZADO = 'S' AND RESTITUIDO = 'S' AND year(DATA_INICIO_CONTAGEM) = ? AND COD_TERCEIRIZADO_CONTRATO = ? AND (PARCELA = 0 OR PARCELA = 2)";

        if (pAnoContagem > anoDisponibilizacao) {
            try {

                preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setInt(1, pAnoContagem-1);
                preparedStatement.setInt(2, pCodTerceirizadoContrato);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    return true;

                }

            } catch (SQLException sqle) {

                throw new NullPointerException("Não foi possível recuperar restituições de férias anteriores.");

            }
            return false;
        }

        return true;
    }

    public String RetornaMaiorParcelaConcedidaDecimoTerceiroPeriodo (int pCodTerceirizadoContrato, Date inicioContagem) {

        String vParcela = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {

            preparedStatement = connection.prepareStatement("SELECT MAX(parcela)\n" +
                    "    FROM tb_restituicao_decimo_terceiro\n" +
                    "    WHERE COD_TERCEIRIZADO_CONTRATO = ?\n" +
                    "    AND DATA_INICIO_CONTAGEM = ?\n" +
                    "    AND (autorizado IS null OR autorizado != 'N')\n" +
                    "    AND (restituido IS null OR restituido != 'N');");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setDate(2, inicioContagem);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vParcela = resultSet.getString(1);

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível identificar se já houve registro da parcela de 14 dias.");

        }

        return vParcela;
    }

}
