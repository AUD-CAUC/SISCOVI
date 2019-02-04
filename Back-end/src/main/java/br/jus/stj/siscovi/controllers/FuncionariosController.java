package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.FuncionariosDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.FuncionarioModel;
import br.jus.stj.siscovi.model.FuncionariosResponseModel;
import com.google.gson.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/funcionarios")
public class FuncionariosController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFuncionariosContratos")
    public Response getAllFuncionarios(String object) throws SQLException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ContratoModel contratos[] = gson.fromJson(object, ContratoModel[].class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        ArrayList<FuncionariosResponseModel> listaFuncionarios = new ArrayList<>();
        for(int i = 0; i < contratos.length; i++) {
            FuncionariosResponseModel funcionarios = new FuncionariosResponseModel();
            funcionarios.setFuncionarios(funcionariosDAO.getFuncionariosContrato(contratos[i].getCodigo()));
            funcionarios.setContrato(contratos[i]);
            funcionarios.setGestor(usuarioDAO.retornaNomeDoGestorDoContrato(contratos[i].getCodigo()));
            listaFuncionarios.add(funcionarios);
        }
        String json = gson.toJson(listaFuncionarios);
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFuncionariosContrato={codigo}")
    public Response getFuncionariosDeUmContrato(@PathParam("codigo") int codigo) throws SQLException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        UsuarioDAO usuarioDAO = new UsuarioDAO(connectSQLServer.dbConnect());
        ArrayList<FuncionarioModel> funcionarios = funcionariosDAO.getFuncionariosContrato(codigo);
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(funcionarios);
        jsonObject.getAsJsonObject().addProperty("gestor", usuarioDAO.retornaNomeDoGestorDoContrato(codigo));
        connectSQLServer.dbConnect().close();
        jsonObject.add("funcionarios", jsonElement.getAsJsonArray());
        return Response.ok(gson.toJson(jsonObject), MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFuncioECargos={codigoContrato}")
    public Response getFuncionariosECargos(@PathParam("codigoContrato") int codigoContrato) throws SQLException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        String json = gson.toJson(funcionariosDAO.retornaCargosFuncionarios(codigoContrato));
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
 }
