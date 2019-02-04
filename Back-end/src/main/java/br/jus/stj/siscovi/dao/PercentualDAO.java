package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.PercentualModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PercentualDAO {
    private Connection connection;

    public PercentualDAO (Connection connection) {
        this.connection = connection;
    }

    public ArrayList<PercentualModel> getPercentuaisDoContrato(int codigoContrato) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<PercentualModel> percertuais = new ArrayList<PercentualModel>();
        try {
            preparedStatement = connection.prepareStatement("SELECT NOME, PERCENTUAL, DATA_INICIO, DATA_FIM, DATA_ADITAMENTO FROM TB_CONTRATO C JOIN TB_PERCENTUAL_CONTRATO PC " +
                    " ON PC.COD_CONTRATO=C.cod JOIN TB_RUBRICAS R ON R.COD=PC.COD_RUBRICA WHERE C.cod=?");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                PercentualModel percentual = new PercentualModel(resultSet.getString("NOME"),
                                                                    resultSet.getFloat("PERCENTUAL"),
                                                                    resultSet.getDate("DATA_INICIO"),
                                                                    resultSet.getDate("DATA_FIM"),
                                                                    resultSet.getDate("DATA_ADITAMENTO"));
                percertuais.add(percentual);
            }
            return percertuais;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

}
