package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.PerfilDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.model.CadastroUsuarioModel;
import br.jus.stj.siscovi.model.RegistroUsuario;
import br.jus.stj.siscovi.model.UsuarioModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;



@Path("/usuario")
public class UsuarioController {

    @GET
    @Path("/getUsuarios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = null;
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        json = gson.toJson(usuarioDAO.getAllUsers());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getPerfis/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfis(@PathParam("user") String usuario) throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        PerfilDAO perfilDAO = new PerfilDAO(connectSQLServer.dbConnect());
        String json = null;
        if(perfilDAO.perfilDoUsuario(usuario).getSigla().equals("ADMINISTRADOR")) {
            json = gson.toJson(perfilDAO.getPerfis());
        }else {
            json = gson.toJson("{msg:" + "Credenciais Inválidas}");
        }
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/alterarUsuario")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterarUsuario(String object) {
        Gson gson = new Gson();
        CadastroUsuarioModel cadastroUsuarioModel = gson.fromJson(object, CadastroUsuarioModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        String json;
        if (usuarioDAO.verificarSenha(cadastroUsuarioModel.getUsuario().getCodigo(), cadastroUsuarioModel.getPassword())) {
            if(usuarioDAO.alteraUsuario(cadastroUsuarioModel.getUsuario(), cadastroUsuarioModel.getCurrentUser())) {
                json = gson.toJson("Alteração feita com sucesso !");
            }else {
                json = gson.toJson("Houve falha na tentativa de Salvar as Alterações");
            }
            try {
                connectSQLServer.dbConnect().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            json = gson.toJson("Senha antiga não confere com a senha digitada");
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getUsuario/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuario(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        RegistroUsuario registroUsuario = consulta.RetornaRegistroUsuario(codigo);
        UsuarioModel usuarioModel = new UsuarioModel(registroUsuario.getpCod(), registroUsuario.getpCodPerfil(), registroUsuario.getpNome(),
                registroUsuario.getpLogin(),  registroUsuario.getpLoginAtualizacao(),
                registroUsuario.getpDataAtualizacao());
        String json = gson.toJson(usuarioModel);
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarUsuario")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastrarUsuario(String object) throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        String json = null;
        ArrayList<String> erros = new ArrayList<>();
        CadastroUsuarioModel cadastroUsuarioModel = gson.fromJson(object, CadastroUsuarioModel.class);
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        PerfilDAO perfilDAO = new PerfilDAO(connectSQLServer.dbConnect());
        if(cadastroUsuarioModel.getUsuario().getLogin() == null || cadastroUsuarioModel.getUsuario().getNome().length() < 4) {
            erros.add("O nome do usuário deve possuir mais de 4 letras");
        }else if(cadastroUsuarioModel.getUsuario().getLogin() == null || cadastroUsuarioModel.getUsuario().getLogin().length() < 4) {
            erros.add("Nome de usuário inválido ! Tente novamente");
        }else {
            if(usuarioDAO.existeNome(cadastroUsuarioModel.getUsuario().getNome())) {
                erros.add("Já existe alguém com esse nome cadastrado no Sistema"); //
            }else if(usuarioDAO.existeLogin(cadastroUsuarioModel.getUsuario().getLogin())) {
                erros.add("Já existe um usuário com esse nome de Usuário");
            }else {
                String hashed = BCrypt.hashpw(cadastroUsuarioModel.getPassword(), BCrypt.gensalt());
                usuarioDAO.cadastrarUsuario(cadastroUsuarioModel.getUsuario(), hashed, cadastroUsuarioModel.getCurrentUser(), perfilDAO.getPerfilCod(cadastroUsuarioModel.getUsuario().getPerfil()));
            }
        }
        connectSQLServer.dbConnect().close();
        if(erros.size() > 0) {
            json = gson.toJson(erros);
        }else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mensagem", "Usuário Cadastrado Com Sucesso !");
            json = gson.toJson(jsonObject);
        }
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("deleteUsuario/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response apagarUsuario(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());
        Gson gson = new Gson();
        String json;
        if (delete.DeleteRegistro(codigo, "TB_USUARIO") == 0) {
            json = gson.toJson("Usuário excluída com sucesso!");
        }else {
            json = gson.toJson("Houve uma falha ao tentar exluir o usuário!");
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getGestores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGestores() {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        String json = gson.toJson(usuarioDAO.getGestores());
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}