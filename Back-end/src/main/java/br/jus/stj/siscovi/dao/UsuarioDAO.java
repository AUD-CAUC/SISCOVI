package br.jus.stj.siscovi.dao;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.UpdateTSQL;
import br.jus.stj.siscovi.model.UsuarioModel;


import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO( Connection connection){
        this.connection = connection;
    }

    public ArrayList<UsuarioModel> getAllUsers() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<UsuarioModel> usuarios = new ArrayList<UsuarioModel>();
        try {
            preparedStatement = connection.prepareStatement("SELECT U.cod, U.NOME, LOGIN, SIGLA, U.LOGIN_ATUALIZACAO, U.DATA_ATUALIZACAO FROM tb_usuario U" +
                    " JOIN TB_PERFIL_USUARIO P ON P.cod=u.COD_PERFIL ORDER BY NOME ASC");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                UsuarioModel usuarioModel = new UsuarioModel(resultSet.getInt("COD"),
                        resultSet.getString("NOME"),
                        resultSet.getString("LOGIN"),
                        resultSet.getString("LOGIN_ATUALIZACAO"),
                        resultSet.getDate("DATA_ATUALIZACAO"));
                usuarioModel.setPerfil(resultSet.getString("SIGLA"));
                usuarios.add(usuarioModel);
            }
            return usuarios;
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    public String retornaNomeDoGestorDoContrato(int codigoContrato){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection.prepareStatement("SELECT U.NOME FROM TB_USUARIO U JOIN TB_HISTORICO_GESTAO_CONTRATO HGC ON HGC.COD_USUARIO=U.COD JOIN TB_PERFIL_GESTAO PG ON PG.COD=HGC.COD_PERFIL_GESTAO" +
                    " WHERE HGC.COD_CONTRATO=?");
            preparedStatement.setInt(1, codigoContrato);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("NOME");
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public Boolean existeNome(String nome) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT NOME FROM TB_USUARIO WHERE NOME=?");
            preparedStatement.setString(1, nome);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return true; // Verdadeiro: Existe um usuário com esse nome no Sistema, ou seja, esta pessoa provavelmente já está cadastrada
            }else {
                return false; // Falso: Não existe um usuário com esse nome no Sistema, ou seja, esta pessoa provavelmente está sendo cadastrada pela primeira vez
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return true;
    }

    public Boolean existeLogin(String login) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT LOGIN FROM TB_USUARIO WHERE LOGIN=?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }else {
                return false;
            }
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return true;
    }

    public Boolean cadastrarUsuario(UsuarioModel usuario, String password, String currentUser, int codigo) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO TB_USUARIO(COD_PERFIL, NOME, LOGIN, PASSWORD, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)");
            preparedStatement.setInt(1, codigo);
            preparedStatement.setString(2, usuario.getNome().toUpperCase());
            preparedStatement.setString(3, usuario.getLogin().toUpperCase());
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, currentUser.toUpperCase());
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }

    public ArrayList<UsuarioModel> getGestores() throws  RuntimeException{
        String sql = "SELECT U.cod, U.NOME, LOGIN, SIGLA, U.LOGIN_ATUALIZACAO, U.DATA_ATUALIZACAO FROM tb_usuario U JOIN TB_PERFIL_USUARIO P ON P.cod=u.COD_PERFIL WHERE P.SIGLA = 'USUÁRIO'";
        ArrayList<UsuarioModel> usuarios = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UsuarioModel usuarioModel = new UsuarioModel(resultSet.getInt("COD"),
                            resultSet.getString("NOME"),
                            resultSet.getString("LOGIN"),
                            resultSet.getString("LOGIN_ATUALIZACAO"),
                            resultSet.getDate("DATA_ATUALIZACAO"));
                    usuarioModel.setPerfil(resultSet.getString("SIGLA"));
                    usuarios.add(usuarioModel);
                }
            }
            return usuarios;
        }catch(SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException("Usuários não encontrados. " + sqle.getMessage());
        }
    }

    public int verifyPermission(int codUsuario, int codContrato) {
        int codGestor = 0;
        String perfil = "";
        String perfilUsuario = "";
        String sql = "SELECT SIGLA FROM TB_USUARIO U JOIN TB_PERFIL_USUARIO PU ON PU.COD=U.COD_PERFIL WHERE U.COD=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codUsuario);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    perfil =  resultSet.getString("SIGLA");
                }
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        sql = "SELECT PG.SIGLA FROM TB_CONTRATO CO JOIN TB_HISTORICO_GESTAO_CONTRATO HGC ON HGC.COD_CONTRATO=CO.COD JOIN tb_perfil_gestao PG ON PG.COD=HGC.COD_PERFIL_GESTAO WHERE HGC.COD_USUARIO=? AND CO.COD=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codUsuario);
            preparedStatement.setInt(2, codContrato);
            try(ResultSet resultSet  = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    perfilUsuario = resultSet.getString("SIGLA");
                }
            }
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        if(perfil.equals("ADMINISTRADOR") || perfilUsuario.equals("1° SUBSTITUTO") || perfilUsuario.equals("2° SUBSTITUTO")) {
            sql = "SELECT HGC.COD_USUARIO FROM TB_CONTRATO CO JOIN tb_historico_gestao_contrato HGC ON HGC.COD_CONTRATO=CO.COD WHERE COD_CONTRATO=?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setInt(1, codContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()) {
                        codGestor = resultSet.getInt("COD_USUARIO");
                    }
                }
            }catch(SQLException sqle) {
                sqle.printStackTrace();
            }
        }else {
            if(perfilUsuario.equals("GESTOR")) {
                codGestor = codUsuario;
            }else {
                System.err.println("Ação maliciosa detectada. Codigo Usuário: " + codUsuario + ". Codigo do Contrato da tentativa de  acesso: " + codContrato);
                throw new RuntimeException("Acesso negado ! Entre em contato com o responsável pelo Sistema");
            }
        }
        return codGestor;
    }

    public boolean isAdmin(String username) throws RuntimeException {
        String sql = "SELECT PU.SIGLA FROM TB_USUARIO U JOIN TB_PERFIL_USUARIO PU ON U.COD_PERFIL=PU.COD  WHERE LOGIN = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()){
                    if(resultSet.getString("SIGLA").contains("ADMINISTRADOR")) {
                        return  true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Usuário não existe !");
        }
        return false;
    }

    public boolean isGestor(String username, int codigoContrato) {
        String sql = "SELECT PG.SIGLA FROM tb_historico_gestao_contrato HGC" +
                " JOIN tb_perfil_gestao PG ON PG.COD=HGC.COD_PERFIL_GESTAO" +
                " JOIN TB_USUARIO U ON U.COD=HGC.COD_USUARIO" +
                " WHERE HGC.COD_CONTRATO=? AND U.LOGIN=? AND (HGC.DATA_FIM IS NULL)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            preparedStatement.setString(2, username);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return true;
                }
            }
        }catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException("Usuário não existe ou não tem permissão para acessar os dados deste contrato !");
        }
        return false;
    }

    public boolean alteraUsuario(UsuarioModel usuario, String currentUser) {
        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        UpdateTSQL update = new UpdateTSQL(connection);

        try {

            update.UpdateUsuario(usuario.getCodigo(),consulta.RetornaCodPerfilUsuario(usuario.getPerfil()), usuario.getNome(), usuario.getLogin(), "SYSTEM");

            return true;

        } catch (Exception exception) {

            throw new NullPointerException("Não foi possível atualizar o uusuário.");

        }

    }

}

