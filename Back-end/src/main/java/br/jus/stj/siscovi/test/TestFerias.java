package br.jus.stj.siscovi.test;

import java.sql.Date;
import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.dao.ConnectSQLServer;

public class TestFerias {

    public static void main(String[] args) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();

        Date data = null;
        Ferias ferias = new Ferias(connectSQLServer.dbConnect());
        int vCodTerceirizado = 2;
        Date deslligamento = Date.valueOf("2018-07-22");
        Date inicioPeriodoAquisitivo;
        Date fimPeriodoAquisitivo;

        data = ferias.RetornaDatasPeriodoFeriasRescisao(vCodTerceirizado, deslligamento, 1);

        System.out.print(data);
        System.out.print("\n");

        data = ferias.RetornaDatasPeriodoFeriasRescisao(vCodTerceirizado, deslligamento, 2);

        System.out.print(data);
        System.out.print("\n");

        data = ferias.RetornaDatasPeriodoFeriasRescisao(vCodTerceirizado, deslligamento, 3);

        System.out.print(data);
        System.out.print("\n");

        data = ferias.RetornaDatasPeriodoFeriasRescisao(vCodTerceirizado, deslligamento, 4);

        System.out.print(data);
        System.out.print("\n");

        inicioPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(7, 1);
        fimPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(7, 2);

        System.out.print(inicioPeriodoAquisitivo);
        System.out.print("\n");
        System.out.print(fimPeriodoAquisitivo);




    }

}
