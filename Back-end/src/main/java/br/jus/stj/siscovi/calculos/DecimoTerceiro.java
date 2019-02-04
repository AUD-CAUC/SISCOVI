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

}
