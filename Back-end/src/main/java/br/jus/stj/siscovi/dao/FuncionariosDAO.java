package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.model.CargoFuncionariosResponseModel;
import br.jus.stj.siscovi.model.CargosFuncionariosModel;
import br.jus.stj.siscovi.model.FuncionarioModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

    public ArrayList<CargoFuncionariosResponseModel> retornaCargosFuncionarios(int codigoContrato) throws RuntimeException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT T.cod, T.NOME, T.CPF ,T.ATIVO, F.NOME AS CARGO, TC.DATA_DISPONIBILIZACAO, TC.DATA_DESLIGAMENTO, T.LOGIN_ATUALIZACAO, T.DATA_ATUALIZACAO " +
                    " FROM TB_TERCEIRIZADO_CONTRATO TC JOIN TB_TERCEIRIZADO T ON T.COD=TC.COD_TERCEIRIZADO JOIN TB_FUNCAO_TERCEIRIZADO FT ON FT.COD_TERCEIRIZADO_CONTRATO=TC.COD " +
                    "JOIN TB_FUNCAO_CONTRATO FC ON FC.COD=FT.COD_FUNCAO_CONTRATO JOIN TB_FUNCAO F ON F.COD=FC.COD_FUNCAO WHERE TC.COD_CONTRATO=? ORDER BY CARGO ASC");
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
            throw new RuntimeException("Nenhum funcionário cadastrado para as funções do contrato ainda ");
        }
    }

    public int InsertTerceirizado (FuncionarioModel funcionario) {

        PreparedStatement preparedStatement;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        int vCod = consulta.RetornaCodSequenceTable("TB_TERCEIRIZADO");

        try {

            String sql = "SET IDENTITY_INSERT TB_TERCEIRIZADO ON;" +
                    " INSERT INTO TB_TERCEIRIZADO (COD," +
                    " NOME," +
                    " CPF," +
                    " ATIVO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO)" +
                    " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP);" +
                    " SET IDENTITY_INSERT TB_TERCEIRIZADO OFF;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vCod);
            preparedStatement.setString(2, funcionario.getNome());
            preparedStatement.setString(3, funcionario.getCpf());
            preparedStatement.setString(4, "" + funcionario.getAtivo());
            preparedStatement.setString(5, funcionario.getLoginAtualizacao());

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível inserir o novo terceirizado.");

        }

        return vCod;

    }

    public List<FuncionarioModel> getAllTerceirizados() {
        List<FuncionarioModel> allTerceirizados = new ArrayList<>();
        String sql = "SELECT * FROM TB_TERCEIRIZADO";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()) {
                    FuncionarioModel funcionarioModel = new FuncionarioModel(resultSet.getInt("COD"), resultSet.getString("NOME"), resultSet.getString("CPF"),
                            resultSet.getString("ATIVO").charAt(0));
                    funcionarioModel.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                    funcionarioModel.setLoginAtualizacao("LOGIN_ATUALIZACAO");
                    allTerceirizados.add(funcionarioModel);
                }
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return allTerceirizados;
    }

    public FuncionarioModel getTerceirizado(int codigo) {
        FuncionarioModel funcionarioModel = null;
        String sql = "SELECT * FROM TB_TERCEIRIZADO WHERE COD=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, codigo);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()) {
                    FuncionarioModel funcionario = new FuncionarioModel(codigo, resultSet.getString("NOME"),
                            resultSet.getString("CPF"),
                            resultSet.getString("ATIVO").charAt(0));
                    funcionario.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                    funcionario.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                    funcionarioModel = funcionario;
                }
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return funcionarioModel;
    }

    public boolean atualizarTerceirizado(FuncionarioModel terceirizado) {
        String sql = "UPDATE TB_TERCEIRIZADO SET NOME=?, CPF=?, ATIVO=?, LOGIN_ATUALIZACAO=?, DATA_ATUALIZACAO=GETDATE() WHERE COD=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, terceirizado.getNome());
            preparedStatement.setString(2, terceirizado.getCpf());
            preparedStatement.setString(3, "" + terceirizado.getAtivo());
            preparedStatement.setString(4, terceirizado.getLoginAtualizacao());
            preparedStatement.setInt(5, terceirizado.getCodigo());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<FuncionarioModel> getTerceirizadosNaoAlocados() throws RuntimeException {
        List<FuncionarioModel> terceirizadosNaoAlocados = new ArrayList<>();
        String sql = "SELECT T.COD, T.NOME, T.ATIVO, T.CPF, T.DATA_ATUALIZACAO, T.LOGIN_ATUALIZACAO FROM TB_TERCEIRIZADO T " +
                " LEFT JOIN TB_TERCEIRIZADO_CONTRATO TC ON TC.COD_TERCEIRIZADO=T.COD" +
                " WHERE TC.COD IS NULL";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FuncionarioModel funcionarioModel = new FuncionarioModel(resultSet.getInt("COD"),
                            resultSet.getString("NOME"), resultSet.getString("CPF"), resultSet.getString("ATIVO").charAt(0));
                    terceirizadosNaoAlocados.add(funcionarioModel);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return terceirizadosNaoAlocados;
    }

    public FuncionarioModel getFuncionarioPorCPF(String cpf) throws RuntimeException {
        FuncionarioModel funcionarioModel = null;
        String sql = "SELECT T.COD, T.NOME, T.CPF, T.ATIVO FROM tb_terceirizado T  LEFT JOIN tb_terceirizado_contrato TC ON TC.COD_TERCEIRIZADO = T.COD" +
                " WHERE TC.COD_TERCEIRIZADO IS NULL AND T.CPF=?;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, cpf);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()) {
                    funcionarioModel = new FuncionarioModel(resultSet.getInt("COD"), resultSet.getString("NOME"),
                            resultSet.getString("CPF"), resultSet.getString("ATIVO").charAt(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar pesquisar por terceirizados não cadastrados em nenhum contrato. CPF digitado: " + cpf);
        }
        return funcionarioModel;
    }

    public boolean verificaTerceirizadoExisteContrato(String cpf) throws RuntimeException {
        String sql = "SELECT COUNT(0) FROM TB_TERCEIRIZADO WHERE CPF = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cpf);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    if(resultSet.getInt(1) != 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar verificar existência de CPF em um contrato. CPF inserido: " + cpf + ". Causa: " + e.getMessage());
        }
        return false;
    }
}
