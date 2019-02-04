package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.dao.ConnectSQLServer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class TesteRestituicaoDecimoTerceiro {

    public static void main(String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();

        // RestituicaoDecimoTerceiro restituicaoDecimoTerceiro = new RestituicaoDecimoTerceiro(connectSQLServer.dbConnect());

        // restituicaoDecimoTerceiro.CalculaRestituicaoDecimoTerceiro(729, "RESGATE",0, Date.valueOf("2016-08-05"),Date.valueOf("2016-12-31"),0);

    }

}
