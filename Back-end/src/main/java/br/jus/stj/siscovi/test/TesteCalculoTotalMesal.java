package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.controllers.TotalMensalController;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.TotalMensalDAO;
import br.jus.stj.siscovi.model.TotalMensal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteCalculoTotalMesal {

    public static void main(String[] args){

    ConnectSQLServer connectSQLServer = new ConnectSQLServer();
    TotalMensalAReter totalMensalAReter = new TotalMensalAReter(connectSQLServer.dbConnect());
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    int vCodTipoRestituicao = 0;

    //totalMensalAReter.CalculaTotalMensal(41, 8, 2016);
    //TotalMensalDAO totalMensalDAO  = new TotalMensalDAO(connectSQLServer.dbConnect());
    //totalMensalDAO.recuperaAnosDeCalculosAnteriores(41);
    //new TotalMensalController().getValoresCalculados(41,8);

        try {

            preparedStatement = connectSQLServer.dbConnect().prepareStatement("SELECT COD" +
                    " FROM TB_TIPO_RESTITUICAO" +
                    " WHERE UPPER(nome) = UPPER(?)");

            preparedStatement.setString(1, "HOLTEL");
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTipoRestituicao = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        System.out.println(vCodTipoRestituicao);


    }

}
