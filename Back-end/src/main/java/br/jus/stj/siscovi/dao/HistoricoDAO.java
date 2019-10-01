package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.model.HistoricoGestorModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class HistoricoDAO {
    private Connection connection;

    public HistoricoDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<HistoricoGestorModel> getHistoricoGestor(int codigo) {
        ArrayList<HistoricoGestorModel> listaHistorico = new ArrayList<>();
        String sql = "SELECT HGC.COD, HGC.COD_CONTRATO, HGC.COD_PERFIL_GESTAO, " +
                "U.NOME AS GESTOR, PG.SIGLA, DATA_INICIO, HGC.LOGIN_ATUALIZACAO, HGC.DATA_ATUALIZACAO, " +
                "CASE WHEN DATA_FIM IS NULL THEN (SELECT MAX(DATA_FIM_VIGENCIA) FROM tb_evento_contratual WHERE COD_CONTRATO = HGC.COD_CONTRATO) ELSE DATA_FIM END AS DATA_FIM " +
                "FROM TB_PERFIL_GESTAO PG " +
                "JOIN tb_historico_gestao_contrato HGC ON HGC.COD_PERFIL_GESTAO=PG.cod JOIN tb_usuario U ON U.cod=HGC.COD_USUARIO " +
                "WHERE HGC.COD_CONTRATO=? ORDER BY PG.COD";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, codigo);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                HistoricoGestorModel historicoGestorModel = new HistoricoGestorModel(resultSet.getInt("COD"), resultSet.getInt("COD_CONTRATO"),
                        resultSet.getInt("COD_PERFIL_GESTAO"), resultSet.getString("GESTOR"), resultSet.getString("SIGLA"),
                        resultSet.getDate("DATA_INICIO"), resultSet.getDate("DATA_FIM"),
                        resultSet.getString("LOGIN_ATUALIZACAO"), resultSet.getDate("DATA_ATUALIZACAO"));
                listaHistorico.add(historicoGestorModel);
            }
            return listaHistorico;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    ArrayList<HistoricoGestorModel> getHistoricoGestorAjuste(int codigoContrato, int codigoAjuste) {
        ArrayList<HistoricoGestorModel> listaHistorico = new ArrayList<>();
        String sql = "SELECT HGC.COD, HGC.COD_CONTRATO, HGC.COD_PERFIL_GESTAO, " +
                "U.NOME AS GESTOR, PG.SIGLA, DATA_INICIO, HGC.LOGIN_ATUALIZACAO, HGC.DATA_ATUALIZACAO, " +
                "CASE WHEN DATA_FIM IS NULL THEN (SELECT MAX(DATA_FIM_VIGENCIA) FROM tb_evento_contratual WHERE COD_CONTRATO = HGC.COD_CONTRATO) ELSE DATA_FIM END AS DATA_FIM " +
                "FROM TB_PERFIL_GESTAO PG " +
                "JOIN tb_historico_gestao_contrato HGC ON HGC.COD_PERFIL_GESTAO=PG.cod JOIN tb_usuario U ON U.cod=HGC.COD_USUARIO " +
                "JOIN tb_evento_contratual EC ON EC.COD_CONTRATO=HGC.COD_CONTRATO " +
                "JOIN TB_TIPO_EVENTO_CONTRATUAL TEC ON EC.COD_TIPO_EVENTO=TEC.COD " +
                "WHERE TEC.TIPO != 'CONTRATO' AND HGC.COD_CONTRATO=? AND EC.cod=? " +
                "AND ((EC.DATA_INICIO_VIGENCIA BETWEEN HGC.DATA_INICIO AND (CASE WHEN HGC.DATA_FIM IS NULL THEN (SELECT MAX(DATA_FIM_VIGENCIA) FROM tb_evento_contratual WHERE COD_CONTRATO = ?) ELSE DATA_FIM END)) " +
                "OR EC.DATA_INICIO_VIGENCIA = HGC.DATA_INICIO)" +
                "ORDER BY PG.COD";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, codigoContrato);
            preparedStatement.setInt(2, codigoAjuste);
            preparedStatement.setInt(3, codigoContrato);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                HistoricoGestorModel historicoGestorModel = new HistoricoGestorModel(resultSet.getInt("COD"), resultSet.getInt("COD_CONTRATO"),
                        resultSet.getInt("COD_PERFIL_GESTAO"), resultSet.getString("GESTOR"), resultSet.getString("SIGLA"),
                        resultSet.getDate("DATA_INICIO"), resultSet.getDate("DATA_FIM"),
                        resultSet.getString("LOGIN_ATUALIZACAO"), resultSet.getDate("DATA_ATUALIZACAO"));
                listaHistorico.add(historicoGestorModel);
            }
            return listaHistorico;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public void insereHistoricoGestaoContrato(int pCodContrato, String nomeGestor, int pCodPerfilGestao, Date pDataInicio, String pUsername) throws NullPointerException {
        InsertTSQL insertTSQL = new InsertTSQL(connection);
        int vCodUsuarioGestor = 0;
        String sql = "SELECT COD FROM TB_USUARIO WHERE NOME=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nomeGestor);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    vCodUsuarioGestor = resultSet.getInt("COD");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro. Usuário indicado para gestor do contrato não existe no sistema !");
        }
        insertTSQL.InsertHistoricoGestaoContrato(pCodContrato, vCodUsuarioGestor, pCodPerfilGestao, pDataInicio, null, pUsername);
    }
}
