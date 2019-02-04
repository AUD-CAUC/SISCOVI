package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.HistoricoGestorModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HistoricoDAO {
    private Connection connection;
    public HistoricoDAO(Connection connection) {
        this.connection = connection;
    }
    public ArrayList<HistoricoGestorModel> getHistoricoGestor(int codigo) {
        ArrayList<HistoricoGestorModel> listaHistorico = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT HGC.COD, U.NOME AS GESTOR, DATA_INICIO, DATA_FIM, HGC.LOGIN_ATUALIZACAO, HGC.DATA_ATUALIZACAO FROM TB_PERFIL_GESTAO PG " +
                    " JOIN tb_historico_gestao_contrato HGC ON HGC.COD_PERFIL_GESTAO=PG.cod JOIN tb_usuario U ON U.cod=HGC.COD_USUARIO WHERE HGC.COD_CONTRATO=?");
            preparedStatement.setInt(1, codigo);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                HistoricoGestorModel historicoGestorModel = new HistoricoGestorModel(resultSet.getInt("COD"), resultSet.getString("GESTOR"), resultSet.getDate("DATA_INICIO"),
                        resultSet.getDate("DATA_FIM"), resultSet.getString("LOGIN_ATUALIZACAO"), resultSet.getDate("DATA_ATUALIZACAO"));
                listaHistorico.add(historicoGestorModel);
            }
            return listaHistorico;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
}
