package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.RestituicaoRescisao;
import br.jus.stj.siscovi.dao.ConnectSQLServer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;


public class TesteRestituicaoRescisao {

    public static void main(String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();

        RestituicaoRescisao restituicaoRescisao = new RestituicaoRescisao(connectSQLServer.dbConnect());

        //restituicaoRescisao.CalculaRestituicaoRescisao(742, "RESGATE",Date.valueOf("2016-12-31"), "A PEDIDO");

    }

}
