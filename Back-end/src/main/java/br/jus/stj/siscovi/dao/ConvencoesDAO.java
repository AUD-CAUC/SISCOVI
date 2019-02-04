package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.ConvencaoColetivaModel;
import br.jus.stj.siscovi.model.ListaConvencoesModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConvencoesDAO {
    private Connection connection;

    public ConvencoesDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ConvencaoColetivaModel> getAllConvencoes() throws RuntimeException{
        List<ConvencaoColetivaModel> allConvencoes = new ArrayList<>();
        String sql = "SELECT * FROM TB_CONVENCAO_COLETIVA";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    ConvencaoColetivaModel convencaoColetivaModel = new ConvencaoColetivaModel(resultSet.getString("SIGLA"), resultSet.getString("NOME"),
                            resultSet.getDate("DATA_BASE"));
                    convencaoColetivaModel.setCodigo(resultSet.getInt("COD"));
                    convencaoColetivaModel.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                    convencaoColetivaModel.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                    convencaoColetivaModel.setDescricao(resultSet.getString("DESCRICAO"));
                    allConvencoes.add(convencaoColetivaModel);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Erro ao recuperar as convenções coletivas Cadastradas. Tente novamente mais tarde ou entre em contato com o administrador do sistema !");
        }
        return allConvencoes;
    }

    public ConvencaoColetivaModel getConvencaoColetiva(int codConvencaoColetiva) throws RuntimeException{
        String sql = "SELECT * FROM TB_CONVENCAO_COLETIVA WHERE COD=?";
        ConvencaoColetivaModel convencaoColetivaModel = null;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codConvencaoColetiva);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    convencaoColetivaModel = new ConvencaoColetivaModel(resultSet.getString("SIGLA"),
                            resultSet.getString("NOME"),
                            resultSet.getDate("DATA_BASE"));
                    convencaoColetivaModel.setCodigo(resultSet.getInt("COD"));
                    convencaoColetivaModel.setDescricao(resultSet.getString("DESCRICAO"));
                    convencaoColetivaModel.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                    convencaoColetivaModel.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        throw new RuntimeException("Erro ao tentar recuperar as convenções coletivas desta função no contrato !");
        }
        return convencaoColetivaModel;
    }
    /*public ArrayList getConvencoesContrato(int codigoContrato) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ListaConvencoesModel listaConvencoes = null;
        ArrayList<ConvencaoColetivaModel> tempConvencoes = new ArrayList<>();
        ArrayList<ListaConvencoesModel> convencoes = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT DISTINCT CA.NOME, REMUNERACAO, DATA_INICIO_CONVENCAO, DATA_FIM_CONVENCAO, DATA_ADITAMENTO, CON.LOGIN_ATUALIZACAO," +
                    " CON.DATA_ATUALIZACAO, U.NOME FROM tb_convencao_coletiva CON JOIN tb_cargo_contrato CC ON cc.cod=CON.COD_CARGO_CONTRATO JOIN tb_contrato CO ON CO.cod=CC.COD_CONTRATO " +
                    "JOIN tb_funcao CA ON CA.cod=cc.COD_CARGO JOIN tb_usuario U ON U.cod=COD_GESTOR WHERE CO.cod=?");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            String tempCargo = null;
            while(resultSet.next()){
                if((tempCargo == null) || !(tempCargo.equals(resultSet.getString("NOME")))){
                    if(tempCargo != null) {
                        listaConvencoes.setConvencoes(tempConvencoes);
                        tempCargo = resultSet.getString("NOME");
                        convencoes.add(listaConvencoes);
                        listaConvencoes = new ListaConvencoesModel(tempCargo, resultSet.getString(8));
                        tempConvencoes = new ArrayList<>();
                    }else {
                        tempCargo = resultSet.getString("NOME");
                        listaConvencoes = new ListaConvencoesModel(tempCargo, resultSet.getString(8));
                    }
                }
                ConvencaoColetivaModel convencaoColetivaModel = new ConvencaoColetivaModel(resultSet.getFloat("REMUNERACAO"),
                            resultSet.getDate("DATA_INICIO_CONVENCAO"),
                            resultSet.getDate("DATA_FIM_CONVENCAO"),
                            resultSet.getDate("DATA_ADITAMENTO"));
                    convencaoColetivaModel.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                    convencaoColetivaModel.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                    tempConvencoes.add(convencaoColetivaModel);

            }
            listaConvencoes.setConvencoes(tempConvencoes);
            convencoes.add(listaConvencoes);
            return convencoes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
