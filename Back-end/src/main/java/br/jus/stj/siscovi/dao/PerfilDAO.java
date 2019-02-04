package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.PerfilModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO {
    Connection connection;
    public PerfilDAO(Connection connection){
        this.connection = connection;
    }

    public PerfilModel perfilDoUsuario(String username) throws NullPointerException, SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PerfilModel perfilUser = new PerfilModel();
        try{
            preparedStatement = connection.prepareStatement("SELECT P.COD, SIGLA, DESCRICAO FROM TB_USUARIO U JOIN TB_PERFIL_USUARIO P ON P.COD=U.COD_PERFIL WHERE U.LOGIN=?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                perfilUser.setCod(resultSet.getInt("COD"));
                perfilUser.setSigla(resultSet.getString("SIGLA"));
                perfilUser.setDescricao(resultSet.getString("DESCRICAO"));
                return perfilUser;
            }
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getPerfis() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> perfis = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT SIGLA FROM TB_PERFIL_USUARIO");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                perfis.add(resultSet.getString("SIGLA"));
            }
            return perfis;
        }catch(NullPointerException npe) {
            npe.printStackTrace();
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public int getPerfilCod(String sigla) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COD FROM TB_PERFIL_USUARIO WHERE SIGLA=?");
            preparedStatement.setString(1, sigla);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("COD");
            }else {
                return 0;
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return 0;
    }

    public List<PerfilModel> getPerfisGestao() {
        List<PerfilModel> perfis = new ArrayList<>();
        String sql = "SELECT COD, NOME, SIGLA, DESCRICAO FROM tb_perfil_gestao";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    PerfilModel perfilModel = new PerfilModel();
                    perfilModel.setCod(resultSet.getInt("COD"));
                    perfilModel.setSigla(resultSet.getString("SIGLA"));
                    perfilModel.setDescricao(resultSet.getString("DESCRICAO"));
                    perfis.add(perfilModel);
                }
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return perfis;
    }
}