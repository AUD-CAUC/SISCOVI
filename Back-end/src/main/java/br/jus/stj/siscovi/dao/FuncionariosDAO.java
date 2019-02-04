package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.CargoFuncionariosResponseModel;
import br.jus.stj.siscovi.model.CargosFuncionariosModel;
import br.jus.stj.siscovi.model.FuncionarioModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FuncionariosDAO {
    private Connection connection;

    public FuncionariosDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<FuncionarioModel> getFuncionariosContrato(int codigoContrato) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<FuncionarioModel> funcionarios = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT F.cod, F.NOME, F.CPF ,F.ATIVO, F.LOGIN_ATUALIZACAO, F.DATA_ATUALIZACAO FROM tb_terceirizado F " +
                    "JOIN TB_TERCEIRIZADO_CONTRATO CF ON CF.COD_TERCEIRIZADO=F.cod " +
                    "JOIN tb_contrato CO ON CO.COD=CF.COD_CONTRATO WHERE CO.COD=? ORDER BY F.NOME");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                FuncionarioModel funcionario = new FuncionarioModel(resultSet.getInt("COD"),
                        resultSet.getString("NOME"),
                        resultSet.getString("CPF"),
                        resultSet.getString("ATIVO").charAt(0));
                funcionario.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                funcionario.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                funcionarios.add(funcionario);
            }
            return funcionarios;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public ArrayList<CargoFuncionariosResponseModel> retornaCargosFuncionarios(int codigoContrato) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT T.cod, T.NOME, T.CPF ,T.ATIVO, F.NOME AS CARGO, TC.DATA_DISPONIBILIZACAO, TC.DATA_DESLIGAMENTO, T.LOGIN_ATUALIZACAO, T.DATA_ATUALIZACAO " +
                    " FROM TB_TERCEIRIZADO_CONTRATO TC JOIN TB_TERCEIRIZADO T ON T.COD=TC.COD_TERCEIRIZADO JOIN TB_FUNCAO_TERCEIRIZADO FT ON FT.COD_TERCEIRIZADO_CONTRATO=TC.COD " +
                    "JOIN TB_FUNCAO_CONTRATO FC ON FC.COD=FT.COD_FUNCAO_CONTRATO JOIN TB_FUNCAO F ON F.COD=FC.COD_FUNCAO WHERE TC.COD_CONTRATO=?");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            String cargoTemp = null;
            ArrayList<CargosFuncionariosModel> cargosFuncionariosModels = null; // Lista de funcionários com data de disponibilização  e data de desligamento
            CargosFuncionariosModel cargosFuncionariosModel = null; // Funcionários com data de disponibilização e data de desligamento
            CargoFuncionariosResponseModel cfrm = null; // Lista de funcionários com data de disponibilizacação e data de desligamento de um cargo
            ArrayList<CargoFuncionariosResponseModel> listacfrm = new ArrayList<>(); // lista de cargos com cada cargo com sua lista de funcionários com data de disponibilização e data de desligamento
            while(resultSet.next()) {
                if(cargoTemp == null || !(cargoTemp.equals(resultSet.getString("CARGO")))) {
                    if(cargoTemp != null){
                        cfrm.setCargosFuncionarios(cargosFuncionariosModels);
                        listacfrm.add(cfrm);
                        cfrm = new CargoFuncionariosResponseModel();
                        cargosFuncionariosModels = new ArrayList<>();
                        cargosFuncionariosModel = new CargosFuncionariosModel();
                        cfrm.setNomeCargo(resultSet.getString("CARGO"));
                        cargoTemp = resultSet.getString("CARGO");

                    }else {
                        cargosFuncionariosModel = new CargosFuncionariosModel(); // cria uma nova instância de funcionários com data de disponibilização e data de desligamento
                        cfrm = new CargoFuncionariosResponseModel(); // cria uma nova instância de lista de funcionários de um cargo
                        cfrm.setNomeCargo(resultSet.getString("CARGO")); // Define o nome do cargo
                        cargoTemp = resultSet.getString("CARGO"); // cargoTemp deixa de ser nulo
                        cargosFuncionariosModels = new ArrayList<>(); // cria lista de funcionarios de um cargo
                    }

                }else if( cargoTemp != null || cargoTemp.equals(resultSet.getString("CARGO"))) {
                    cargosFuncionariosModel = new CargosFuncionariosModel();
                }
                FuncionarioModel funcionario = new FuncionarioModel(resultSet.getInt("COD"), resultSet.getString("NOME"), resultSet.getString("CPF"),
                        resultSet.getString("ATIVO").charAt(0));
                funcionario.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                funcionario.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                cargosFuncionariosModel.setFuncionario(funcionario); // Adiciona funcionário a um objeto que possui data de disponibilização e data de desligamento
                cargosFuncionariosModel.setDataDisponibilizacao(resultSet.getDate("DATA_DISPONIBILIZACAO")); //Atribui data de disponibilização ao funcionário
                cargosFuncionariosModel.setDataDesligamento(resultSet.getDate("DATA_DESLIGAMENTO")); // Atribui data de desligamento ao funcionário
                cargosFuncionariosModels.add(cargosFuncionariosModel); // Adiciona funcionário com data de desligamento à lista
            }
            cfrm.setCargosFuncionarios(cargosFuncionariosModels);
            listacfrm.add(cfrm);
            return listacfrm;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
}
