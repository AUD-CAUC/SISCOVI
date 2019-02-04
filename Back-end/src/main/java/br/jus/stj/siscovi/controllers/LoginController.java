package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.PerfilDAO;
import br.jus.stj.siscovi.helpers.Token;
import br.jus.stj.siscovi.model.*;
import com.google.gson.Gson;
import br.jus.stj.siscovi.dao.LoginDAO;
import com.google.gson.GsonBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;




@Path("/login")
public class LoginController {
    @POST
    @Path("/createAccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastro(String object) throws SQLException {
        Gson gson = new Gson();
        LoginModel login = gson.fromJson(object, LoginModel.class);
        String nome;
        String json;
        String username = login.getUsername();
        String password = login.getPassword();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        LoginDAO ldao = new LoginDAO(connectSQLServer.dbConnect());
        if(ldao.checkLoginCredentials(username,password)){
            nome = "Verdadeiro";

        }else{
            nome = "Falso";
        }
        connectSQLServer.dbConnect().close();
        json = gson.toJson(nome);

        return Response.ok(json,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/checkLogin/username={username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificaCadastro(@PathParam("username") String username) throws SQLException {
        Gson gson = new Gson();
        Status status = new Status();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        LoginDAO ldao = new LoginDAO(connectSQLServer.dbConnect());
        String json;
        if(ldao.checkLogin(username)){
            status.setStatus(true);
            json = gson.toJson(status);
        }else{
            status.setStatus(false);
            json = gson.toJson(status);
        }
        connectSQLServer.dbConnect().close();
        return Response.ok(json,MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response geraToken(String object) throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException{

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ResponseLoginModel rlm = new ResponseLoginModel();
        ContratoDAO contratoDAO;
        LoginModel loginModel = gson.fromJson(object,LoginModel.class);
        User user = new User();
        AreaModel areaModel = new AreaModel();
        AreaModel areasPai = new AreaModel();
        AreaModel areasChild = new AreaModel();
        DataModel dataModel =  new DataModel();
        PerfilModel perfilModel = new PerfilModel();
        PerfilDAO perfilDAO;
        ResultSet rs;
        String token = "";
        String json;
        String password = loginModel.getPassword();
        String username = loginModel.getUsername();

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        LoginDAO ldao = new LoginDAO(connectSQLServer.dbConnect());

        if(ldao.validateLogin(username,password)){
            rlm.setSuccess(true);
            rs = ldao.getUser(username, password);
            // refazer token
            areaModel.setId(rs.getInt("COD"));
            areaModel.setPaiId(37946);
            areaModel.setSigla("CAUC");
            areaModel.setNome("Cordenadoria de Auditoria de Compras e Contratos");
            areaModel.setAuditoria(true);
            areasPai.setId(72456);
            areasPai.setNome("CAUC");
            areasPai.setPaiId(37946);
            areasChild.setId(72456);
            areasChild.setNome("CAUC");
            areasChild.setPaiId(72456);
            user.setActive(true);
            user.setModified(rs.getDate("DATA_ATUALIZACAO"));
            user.setCreated(rs.getDate("DATA_ATUALIZACAO"));
            user.setUsername(rs.getString("LOGIN").toLowerCase());
            user.setId(rs.getInt("COD"));
            user.setArea(areaModel);
            contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
            perfilDAO = new PerfilDAO(connectSQLServer.dbConnect());
            user.setPerfil(perfilDAO.perfilDoUsuario(username));
            System.out.println(token);
            Token tk = new Token();
            token = tk.tokenGen(String.valueOf(user.getId()), rs.getString("NOME"), rs.getString("LOGIN"), 900000);
            dataModel.setToken(token);
            dataModel.setAreasChild(areasChild);
            dataModel.setAreasPai(areasPai);
            dataModel.setUser(user);
            dataModel.setContratos(contratoDAO.retornaContratoDoUsuario(username));
            rlm.setData(dataModel);
            json = gson.toJson(rlm);
        }else{
            rlm.setSuccess(false);
            ArrayList al = new ArrayList();
            String errorMsg = "Login ou senha inválidos";
            al.add(rlm);
            al.add(errorMsg);
            json = gson.toJson(al);
        }
        connectSQLServer.dbConnect().close();

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}
