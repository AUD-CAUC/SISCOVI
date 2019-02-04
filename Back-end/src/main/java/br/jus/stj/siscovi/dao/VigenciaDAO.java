package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.VigenciaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VigenciaDAO {
    private Connection connection;
    public VigenciaDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<VigenciaModel> retornaVigenciasDeUmContrato(int codigoContrato) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<VigenciaModel> vigencias = new ArrayList<>();
        try{
            preparedStatement = connection.prepareStatement("SELECT VC.cod, DATA_INICIO_VIGENCIA , DATA_FIM_VIGENCIA, VC.LOGIN_ATUALIZACAO, VC.DATA_ATUALIZACAO " +
                    "FROM tb_vigencia_contrato VC JOIN tb_contrato CO ON CO.cod=VC.COD_CONTRATO WHERE CO.cod=?");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                VigenciaModel vigencia = new VigenciaModel(resultSet.getInt("COD"), resultSet.getDate("DATA_INICIO_VIGENCIA"),
                        resultSet.getDate("DATA_FIM_VIGENCIA"));
                vigencia.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                if(resultSet.getString("LOGIN_ATUALIZACAO") == null) {
                    vigencia.setLoginAtualizacao("-");
                }else {
                    vigencia.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                }
                vigencias.add(vigencia);
            }
            return vigencias;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }

        return null;
    }
}