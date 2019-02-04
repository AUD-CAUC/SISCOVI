package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.Saldo;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;

public class TestSaldo {

    public static void main (String[] args) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        InsertTSQL insert = new InsertTSQL(connectSQLServer.dbConnect());
        ConsultaTSQL consulta = new ConsultaTSQL((connectSQLServer.dbConnect()));
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());
        Saldo saldo = new Saldo(connectSQLServer.dbConnect());

        float vFerias = saldo.getSaldoContaVinculada(4, 2016, 2, 1);
        float vDecimoTerceiro = saldo.getSaldoContaVinculada(1, 2016, 3, 103);

        System.out.print(vFerias);
        System.out.print('\n');
        System.out.print(vDecimoTerceiro);

    }

}
