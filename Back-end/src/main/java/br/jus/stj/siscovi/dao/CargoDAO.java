package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.*;
import com.sun.org.apache.regexp.internal.RE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {
    private Connection connection;
    public CargoDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<CargoModel> getAllCargos() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<CargoModel> cargos = new ArrayList<>();
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM TB_FUNCAO ORDER BY NOME");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                CargoModel cargo = new CargoModel(resultSet.getInt("COD"),
                        resultSet.getString("NOME"),
                        resultSet.getString("LOGIN_ATUALIZACAO"),
                        resultSet.getDate("DATA_ATUALIZACAO"));
                if (resultSet.getString("DESCRICAO") ==  null) {
                    cargo.setDescricao("-");
                }else {
                    cargo.setDescricao(resultSet.getString("DESCRICAO"));
                }
                cargos.add(cargo);
            }
            return cargos;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public ArrayList<CargoModel> getCargosDeUmContrato (int codigo) throws RuntimeException{
        ArrayList<CargoModel> cargos = new ArrayList<>();
        String sql = "SELECT CC.COD_FUNCAO,CA.NOME,CA.DESCRICAO,CA.LOGIN_ATUALIZACAO,CA.DATA_ATUALIZACAO" +
                " FROM tb_funcao_contrato CC" +
                " JOIN tb_FUNCAO CA ON CA.cod=CC.COD_FUNCAO" +
                " WHERE CC.COD_CONTRATO=?" +
                " ORDER BY CA.NOME";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigo);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CargoModel cargo = new CargoModel(resultSet.getInt("COD_FUNCAO"),
                            resultSet.getString("NOME"),
                            resultSet.getString("LOGIN_ATUALIZACAO"),
                            resultSet.getDate("DATA_ATUALIZACAO"));
                    if (resultSet.getString("DESCRICAO") != null) {
                        cargo.setDescricao(resultSet.getString("DESCRICAO"));
                    } else {
                        cargo.setDescricao("-");
                    }
                    cargos.add(cargo);
                }
            }
            return cargos;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public boolean cadastroCargos(ArrayList<CargoModel> cargos, String currentUser) {
        PreparedStatement preparedStatement = null;
        int a = 1;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO TB_FUNCAO (NOME, DESCRICAO, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES (?, ?, ?, CURRENT_TIMESTAMP)");
            for (CargoModel cargo : cargos) {
                if ((a % 4) == 0) {
                    a = 1;
                }
                preparedStatement.setString(a++, cargo.getNome());
                preparedStatement.setString(a++, cargo.getDescricao());
                preparedStatement.setString(a++, currentUser);
                preparedStatement.addBatch();
            }
            int [] updateCounts = preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }

    public List<CargoModel> getFuncoesContrato(int codigo, User user) throws RuntimeException {
        List<CargoModel> cargos = new ArrayList<>();
        UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
        int cod = usuarioDAO.verifyPermission(user.getId(), codigo);
        String sql = "SELECT CC.COD_FUNCAO,CA.NOME,CA.DESCRICAO,CA.LOGIN_ATUALIZACAO,CA.DATA_ATUALIZACAO" +
                " FROM tb_funcao_contrato CC" +
                " JOIN tb_FUNCAO CA ON CA.cod=CC.COD_FUNCAO" +
                " WHERE CC.COD_CONTRATO=?" +
                " ORDER BY CA.NOME";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigo);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CargoModel cargo = new CargoModel(resultSet.getInt("COD_FUNCAO"),
                            resultSet.getString("NOME"),
                            resultSet.getString("LOGIN_ATUALIZACAO"),
                            resultSet.getDate("DATA_ATUALIZACAO"));
                    cargo.setDescricao(resultSet.getString("DESCRICAO"));
                    cargos.add(cargo);
                }
            }
            return cargos;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException(sqle.getMessage());
        }
    }

    public List<CargosFuncionariosModel> getTerceirizadosFuncao(int codigo, User user) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(user.getId(), codigo);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(user.getId(), codigo);
        List<CargosFuncionariosModel> lista = new ArrayList<>();
        if(codigoGestor == codGestor) {
            String sql = "SELECT T.cod, T.NOME, T.CPF ,T.ATIVO, F.COD as CODIGO,F.NOME AS CARGO, TC.DATA_DISPONIBILIZACAO, TC.DATA_DESLIGAMENTO, T.LOGIN_ATUALIZACAO, T.DATA_ATUALIZACAO," +
                    " F.DATA_ATUALIZACAO AS DATAATUALIZACAO, F.LOGIN_ATUALIZACAO AS LOGINATUALIZACAO" +
                    " FROM TB_TERCEIRIZADO_CONTRATO TC JOIN TB_TERCEIRIZADO T ON T.COD=TC.COD_TERCEIRIZADO JOIN TB_FUNCAO_TERCEIRIZADO FT ON FT.COD_TERCEIRIZADO_CONTRATO=TC.COD" +
                    " JOIN TB_FUNCAO_CONTRATO FC ON FC.COD=FT.COD_FUNCAO_CONTRATO JOIN TB_FUNCAO F ON F.COD=FC.COD_FUNCAO WHERE TC.COD_CONTRATO=? ORDER BY T.NOME ASC";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigo);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        FuncionarioModel terceirizado = new FuncionarioModel(resultSet.getInt("COD"), resultSet.getString("NOME"), resultSet.getString("CPF"),
                                resultSet.getString("ATIVO").charAt(0));
                        CargoModel cargoModel = new CargoModel(resultSet.getInt("CODIGO"), resultSet.getString("CARGO"),
                                resultSet.getString("LOGINATUALIZACAO"), resultSet.getDate("DATAATUALIZACAO"));
                        CargosFuncionariosModel cfm = new CargosFuncionariosModel();
                        cfm.setFuncionario(terceirizado);
                        cfm.setFuncao(cargoModel);
                        cfm.setDataDisponibilizacao(resultSet.getDate("DATA_DISPONIBILIZACAO"));
                        cfm.setDataDesligamento(resultSet.getDate("DATA_DESLIGAMENTO"));
                        lista.add(cfm);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}