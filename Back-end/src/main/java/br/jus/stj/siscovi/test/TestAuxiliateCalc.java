package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.dao.AuxiliateCalcDAO;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.HistoricoDAO;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TestAuxiliateCalc {
    public static void main (String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        AuxiliateCalcDAO calcDAO = new AuxiliateCalcDAO(connectSQLServer.dbConnect());
        String username;

        try {
            // System.out.println(calcDAO.TipoDeRestituicao(1));
            // calcDAO.ExisteRetroatividade(1, 2, 8, 2013, 1);
            // System.out.println(calcDAO.RetornaConvencaoAnterior(1));
            // System.out.println(calcDAO.RetornaConvencaoAnterior(2));
            // System.out.println(calcDAO.RetornaRemuneracaoPeriodo(1,8,2013,1,1));
            Date date1 = Date.valueOf(""+2018+"-"+1+"-"+8); // Data no modelo de objetos da biblioteca do driver do SQL
            Date date2 = Date.valueOf(""+2018+"-"+2+"-"+8); // Data no modelo de objetos da biblioteca do driver do SQL
            System.out.println("A data1 segundo o driver sql é: "+date1); // imprime a Data1
            System.out.println("A data2 segundo o driver sql é: "+date2); // imprime a Data2
            LocalDate date3 = date1.toLocalDate(); // Converte data do modelo de objetos da biblioteca do driver para a nova API de data e tempo do Java 8
            LocalDate date4 = date2.toLocalDate(); // Mesma conversão
            System.out.println("A data1 segundo o Java é: "+date3); // Imprime a data1 convertida
            System.out.println("A data2 segundo o Java é: "+date4); // imprime a data2 convertida
            long days = Period.between(date4,date3).getDays(); // Algumas operações com Datas permitidas pela API do Java 8
            System.out.println("O número de dias entre 8 de janeiro de 2018 e 8 de feverreiro de 2018 é: "+days);
            System.out.println("O dia da data 1 é: "+date3.getDayOfMonth());
            System.out.println("O dia anterior a data 1 é: "+date3.minusDays(1).getDayOfMonth());
            System.out.println("A data 2 vem depois da data1: "+date2.after(date1)); // A comparação pode ser feita com os próprios objetos da Biblioteca do Driver SQL
            System.out.println("A data 1 vem antes da data 2: "+date1.before(date2));
            System.out.println("A data 1 é igual a data 2: "+date1.equals(date2));
            long dias = ChronoUnit.DAYS.between(date3,date4) + 1; // Contando quantidade de dias entre duas datas
            System.out.println("O numero de dias entre 8 de janeiro de 2018 e 8 de fevereiro de 2018 é : "+dias); // Repara que a funcção "Period" não funciona para meses diferentes
            System.out.println(""+date3.format(DateTimeFormatter.ofPattern("cccc, dd 'de' "+ "MMMM" +" 'de' uuuu")));
            System.out.println(""+date4.format(DateTimeFormatter.ofPattern("cccc, dd 'de' "+ "MMMM" +" 'de' uuuu")));
            System.out.println("Primeiro dia do mês da data1: "+date3.withDayOfMonth(1));
            System.out.println("Último dia do mês da data1: "+date3.withDayOfMonth(date3.lengthOfMonth()));
            System.out.print("Convertendo :");
            System.out.println(" "+date3.withDayOfMonth(date3.lengthOfMonth()).format(DateTimeFormatter.ofPattern("cccc, dd 'de' MMMM 'de' uuuu")));
            Date date = Date.valueOf("2013-02-01");
            System.out.println(date);
            System.out.println(date.toLocalDate().format(DateTimeFormatter.ofPattern("cccc, dd 'de' "+ "MMMM" +" 'de' uuuu")));
            connectSQLServer.dbConnect().close();
            System.out.println(date.toLocalDate().getDayOfMonth());
            System.out.println(date1.toLocalDate().getMonthValue());
            System.out.println(ChronoUnit.DAYS.between(Date.valueOf("2017-07-01").toLocalDate(), Date.valueOf("2017-07-02").toLocalDate()));

        } catch (SQLServerException sqlse) {
            sqlse.printStackTrace();
        } catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
