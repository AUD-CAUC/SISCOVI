package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.dao.ConnectSQLServer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class TesteRestituicaoFerias {
    public static void main(String[] args){

        String result = null;
        float dias = 0;
        Date inicio = Date.valueOf("2017-08-05");
        Date fim = Date.valueOf("2018-08-05");
        int meses = 0;
        boolean resultado;

        float resultadoFloat = 0;

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoFerias restituicaoFerias = new RestituicaoFerias(connectSQLServer.dbConnect());
        Retencao retencao = new Retencao(connectSQLServer.dbConnect());
        Periodos periodo = new Periodos(connectSQLServer.dbConnect());
        Ferias ferias = new Ferias(connectSQLServer.dbConnect());
        Remuneracao remuneracao = new Remuneracao(connectSQLServer.dbConnect());
        Date vDataInicioContrato = Date.valueOf("2016-08-05");



        //dias = ferias.DiasPeriodoAquisitivo(Date.valueOf("2016-01-01"),Date.valueOf("2016-07-25"));
        //resultado = retencao.FuncaoRetencaoIntegral(53, 10, 2016);

        //System.out.printf("O valor da ação é %.2f %n", dias);

        //resultadoFloat = remuneracao.RetornaRemuneracaoPeriodo(145, 10, 2016, 1, 2);

        System.out.print(Date.valueOf(vDataInicioContrato.toLocalDate().minusMonths(1).withDayOfMonth(vDataInicioContrato.toLocalDate().lengthOfMonth()).plusDays(1)));

        //restituicaoFerias.CalculaRestituicaoFerias(729, "RESGATE",0, Date.valueOf("2017-09-01"),Date.valueOf("2017-09-30"),Date.valueOf("2016-08-05"),Date.valueOf("2017-08-04"),0,'N');
/*
        try {

            result = retencao.TipoDeRestituicao(22);

            System.out.print(result);

        } catch(SQLException sqle) {

            throw new NullPointerException ("Falha no teste de recuperação do tipo de restituição.");

        }

        dias = ChronoUnit.DAYS.between(inicio.toLocalDate(), fim.toLocalDate());

        System.out.print("\n");

        System.out.println(dias);

        //TotalMensalDAO totalMensalDAO  = new TotalMensalDAO(connectSQLServer.dbConnect());
        //totalMensalDAO.recuperaAnosDeCalculosAnteriores(1);
        //new TotalMensalController().getValoresCalculados(1,8);
*/

        //meses = (int)ChronoUnit.DAYS.between(inicio.toLocalDate(), fim.toLocalDate());

        //System.out.println(meses);



    }





}
