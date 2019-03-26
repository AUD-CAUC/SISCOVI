package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.model.PercentuaisEstaticosModel;
import br.jus.stj.siscovi.model.PercentuaisDinamicosModel;
import br.jus.stj.siscovi.model.RubricaModel;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;
import java.util.ArrayList;


public class RubricasDAO {
    private Connection connection;

    public RubricasDAO(Connection connection){
        this.connection = connection;
    }

    public ArrayList<RubricaModel> SelectAllRubricas(){
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<RubricaModel> rubricas = new ArrayList<RubricaModel>();
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM TB_RUBRICA");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                RubricaModel rubrica = new RubricaModel(resultSet.getString("NOME"), resultSet.getString("SIGLA"), resultSet.getInt("COD"));
                if(resultSet.getString("DESCRICAO") != null){
                    rubrica.setDescricao(resultSet.getString("DESCRICAO"));
                }else{
                    rubrica.setDescricao("-");
                }
                rubricas.add(rubrica);
            }
            return rubricas;
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }catch (SQLServerException sse){
            sse.printStackTrace();
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    public ArrayList<PercentuaisEstaticosModel> SelectPercentuaisEstaticos() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<PercentuaisEstaticosModel> listaDePercentuais = new ArrayList<PercentuaisEstaticosModel>();
        PercentuaisEstaticosModel meuPercentual;
        try{
            preparedStatement = connection.prepareStatement("SELECT COD_RUBRICA,NOME,PERCENTUAL, DATA_INICIO, DATA_FIM, DATA_ADITAMENTO FROM tb_percentual_estatico JOIN tb_rubrica R ON " +
                    " COD_RUBRICA=R.cod");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                meuPercentual = new PercentuaisEstaticosModel(resultSet.getInt("COD_RUBRICA"), resultSet.getString("NOME"), resultSet.getFloat("PERCENTUAL"),
                        resultSet.getDate("DATA_INICIO"), resultSet.getDate("DATA_FIM"), resultSet.getDate("DATA_ADITAMENTO"));
                listaDePercentuais.add(meuPercentual);
            }
            return listaDePercentuais;
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }catch (SQLServerException sqlse) {
            sqlse.printStackTrace();
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public ArrayList<PercentuaisDinamicosModel> SelectAllPercentuaisDinamicos() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<PercentuaisDinamicosModel> listaDePercentuaisDinamicos = new ArrayList<PercentuaisDinamicosModel>();
        PercentuaisDinamicosModel meuPercentual;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM tb_percentual_dinamico");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                meuPercentual = new PercentuaisDinamicosModel(resultSet.getFloat("PERCENTUAL"), resultSet.getString("LOGIN_ATUALIZACAO"),
                        resultSet.getDate("DATA_ATUALIZACAO"));
                listaDePercentuaisDinamicos.add(meuPercentual);
            }
            return listaDePercentuaisDinamicos;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (SQLServerException sqlse) {
            sqlse.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public ArrayList<PercentuaisDinamicosModel> SelectPercentualDinamico() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<PercentuaisDinamicosModel> listaDePercentuaisDinamicos = new ArrayList<PercentuaisDinamicosModel>();
        PercentuaisDinamicosModel meuPercentual;
        try{
            preparedStatement = connection.prepareStatement("SELECT PERCENTUAL, DATA_ATUALIZACAO FROM tb_percentual_dinamico WHERE COD=?");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                meuPercentual = new PercentuaisDinamicosModel(resultSet.getFloat("PERCENTUAL"),
                        resultSet.getString("LOGIN_ATUALIZACAO"), resultSet.getDate("DATA_ATALIZACAO"));
                listaDePercentuaisDinamicos.add(meuPercentual);
            }
            return listaDePercentuaisDinamicos;
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }catch (SQLServerException sqlse) {
            sqlse.printStackTrace();
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public boolean InsertPercentualDinamico(float percentual, String currentUser) {
        InsertTSQL insertTSQL = new InsertTSQL(connection);
        insertTSQL.InsertPercentualDinamico(percentual, currentUser);
        return true;
    }
    public boolean InsertPercentualEstatico(PercentuaisEstaticosModel percentuaisEstaticosModel, String currentUser) {
        PreparedStatement preparedStatement;
        InsertTSQL insertTSQL = new InsertTSQL(connection);
        Date novaDataInicio = Date.valueOf(percentuaisEstaticosModel.getDataInicio().toLocalDate().minusDays(1));
        try {
            preparedStatement = connection.prepareStatement("UPDATE tb_percentual_estatico SET DATA_FIM = ? WHERE COD_RUBRICA = ? AND DATA_FIM is NULL");
            preparedStatement.setDate(1, novaDataInicio);
            preparedStatement.setInt(2, percentuaisEstaticosModel.getCodigo());
            preparedStatement.executeUpdate();

            insertTSQL.InsertPercentualEstatico(percentuaisEstaticosModel.getCodigo(), percentuaisEstaticosModel.getPercentual(), percentuaisEstaticosModel.getDataInicio(),
                    percentuaisEstaticosModel.getDataFim(), percentuaisEstaticosModel.getDataAditamento(), currentUser);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }

        return true;
    }
    public boolean InsertRubrica(RubricaModel rubricaModel, String currentUser) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO TB_RUBRICA (NOME, SIGLA, DESCRICAO, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)");
            preparedStatement.setString(1, rubricaModel.getNome());
            preparedStatement.setString(2, rubricaModel.getSigla());
            preparedStatement.setString(3, rubricaModel.getDescricao());
            preparedStatement.setString(4, currentUser.toUpperCase());
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
    public RubricaModel GetRubricas(int codigo) {
        RubricaModel rubrica;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement("SELECT COD, NOME, SIGLA, DESCRICAO FROM TB_RUBRICA WHERE COD=?");
            preparedStatement.setInt(1, codigo);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                rubrica = new RubricaModel(resultSet.getString("NOME"), resultSet.getString("SIGLA"), resultSet.getInt("COD"));
                if (resultSet.getString("DESCRICAO") == null) {
                    rubrica.setDescricao("-");
                }else {
                    rubrica.setDescricao(resultSet.getString("DESCRICAO"));
                }
                return rubrica;
            }
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public boolean DeleteRubrica(int codigo) {
        PreparedStatement preparedStatement;
        try{
            preparedStatement = connection.prepareStatement("DELETE FROM TB_RUBRICA WHERE COD=?");
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
    public boolean AlteraRubrica(RubricaModel rubrica, String currentUser) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("UPDATE TB_RUBRICA SET NOME=?, SIGLA=?, DESCRICAO=?, LOGIN_ATUALIZACAO=?, DATA_ATUALIZACAO=CURRENT_TIMESTAMP WHERE COD=?");
            preparedStatement.setString(1, rubrica.getNome());
            preparedStatement.setString(2, rubrica.getSigla());
            preparedStatement.setString(3, rubrica.getDescricao());
            preparedStatement.setString(4, currentUser);
            preparedStatement.setInt(5, rubrica.getCodigo());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
//    public boolean AlteraPercentualDinamico(PercentuaisDinamicosModel percentual, String currentUser) {
//        PreparedStatement preparedStatement;
//        try {
//            preparedStatement = connection.prepareStatement("UPDATE TB_PERCENTUAL_DINAMICO SET PERCENTUAL=?, LOGIN_ATUALIZACAO=?, DATA_ATUALIZACAO=CURRENT_TIMESTAMP WHERE COD=?");
//            preparedStatement.setFloat(1, percentual.getPercentual());
//            preparedStatement.setString(2, currentUser);
//            preparedStatement.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    public boolean DeletePercentualDinamico(int codigo) {
        PreparedStatement preparedStatement;
        try{
            preparedStatement = connection.prepareStatement("DELETE FROM tb_percentual_dinamico WHERE COD=?");
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
}
