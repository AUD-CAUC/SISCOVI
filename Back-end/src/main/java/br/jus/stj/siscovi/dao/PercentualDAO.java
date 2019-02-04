package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Percentual;
import br.jus.stj.siscovi.model.PercentualModel;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PercentualDAO {
    private Connection connection;

    public PercentualDAO (Connection connection) {
        this.connection = connection;
    }

    public ArrayList<PercentualModel> getPercentuaisDoContrato(int codigoContrato) throws RuntimeException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<PercentualModel> percertuais = new ArrayList<PercentualModel>();
        try {
            preparedStatement = connection.prepareStatement("SELECT NOME, PERCENTUAL, DATA_INICIO, DATA_FIM, DATA_ADITAMENTO FROM TB_CONTRATO C JOIN TB_PERCENTUAL_CONTRATO PC " +
                    " ON PC.COD_CONTRATO=C.cod JOIN TB_RUBRICA R ON R.COD=PC.COD_RUBRICA WHERE C.cod=?");
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
            throw new RuntimeException("Erro ao tentar recuperar os percentuais dos contratos ! " + sqle.getMessage());
        }
    }

    public List<PercentualModel> getPercentuaisDecimoTerceiro() throws RuntimeException {
        List<PercentualModel> percentuais = new ArrayList<>();
        /* String sql = "";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()){
                    PercentualModel percentual = new PercentualModel();
                }
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            throw new RuntimeException("Erro ao tentar carregar os percentuais de Décimo terceiro. Favor entrar em contato com o administrador do sistema. " + sqle.getMessage());
        }*/
        PercentualModel percentual = new PercentualModel("Décimo terceiro salário", 9.09f, Date.valueOf("2018-10-10"),
                Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10"));
        percentuais.add(percentual);
        percentual = new PercentualModel("Décimo terceiro salário", 8.33f, Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10"));
        percentuais.add(percentual);
        return percentuais;
    }

    public List<PercentualModel> getPercentuaisFerias() {
        List<PercentualModel> percentuais = new ArrayList<>();
        PercentualModel percentualModel = new PercentualModel("Férias", 9.09f, Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10") , Date.valueOf("2018-10-10"));
        percentuais.add(percentualModel);
        percentualModel = new PercentualModel("Férias", 8.33f, Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10"), Date.valueOf("2018-10-10"));
        percentuais.add(percentualModel);
        return percentuais;
    }
}
