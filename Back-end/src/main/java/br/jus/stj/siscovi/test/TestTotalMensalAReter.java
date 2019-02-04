package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.TotalMensalAReter;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestTotalMensalAReter {

    public static void main (String[] args) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        TotalMensalAReter totalMensalAReter = new TotalMensalAReter(connectSQLServer.dbConnect());

        int vCodContrato = consulta.RetornaCodContratoAleatorio();
        int vNumeroMeses = 12;
        Date vDataInicioCotrato = consulta.RetornaDataInicioContrato(vCodContrato);

        for (int i = 0; i < vNumeroMeses; i++) {

            totalMensalAReter.CalculaTotalMensal(vCodContrato, vDataInicioCotrato.toLocalDate().getMonthValue(), vDataInicioCotrato.toLocalDate().getYear(), "SYSTEM");

            vDataInicioCotrato = Date.valueOf(vDataInicioCotrato.toLocalDate().plusMonths(1));

        }

     }

}
