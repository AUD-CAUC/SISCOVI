package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.FuncionariosDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.dao.sql.UpdateTSQL;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.FuncionarioModel;
import br.jus.stj.siscovi.model.FuncionariosResponseModel;
import br.jus.stj.siscovi.model.TerceirizadoDecimoTerceiro;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.glass.ui.delegate.MenuItemDelegate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public Response getFuncionariosECargos(@PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json = "";
        try {
            ConnectSQLServer connectSQLServer = new ConnectSQLServer();
            FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
            json = gson.toJson(funcionariosDAO.retornaCargosFuncionarios(codigoContrato));
            connectSQLServer.dbConnect().close();
        }catch(Exception ex) {
            ex.printStackTrace();
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.NOT_FOUND).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarTerceirizado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastraTerceirizado(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FuncionarioModel funcionario = gson.fromJson(object, FuncionarioModel.class);
        String json = "";
        try{
            ConnectSQLServer connectSQLServer = new ConnectSQLServer();
            FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
            funcionariosDAO.InsertTerceirizado(funcionario);
            connectSQLServer.dbConnect().close();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "O terceirizado foi cadastrado com sucesso !");
            json = gson.toJson(jsonObject);
        }catch (SQLException slqe){
            json = gson.toJson(ErrorMessage.handleError(slqe));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch(NullPointerException npe) {
            json = gson.toJson(ErrorMessage.handleError(npe));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarTerceirizados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastraTerceirizados(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<FuncionarioModel> terceirizados = gson.fromJson(object, new TypeToken<List<FuncionarioModel>>(){}.getType());
        String json = "";
        try{
            ConnectSQLServer connectSQLServer = new ConnectSQLServer();
            FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
            for(FuncionarioModel terceirizado : terceirizados) {
                funcionariosDAO.InsertTerceirizado(terceirizado);
            }
            connectSQLServer.dbConnect().close();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "Os terceirizados foram cadastrados com sucesso");
            json = gson.toJson(jsonObject);
        }catch (SQLException slqe){
            json = gson.toJson(ErrorMessage.handleError(slqe));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch(NullPointerException npe) {
            json = gson.toJson(ErrorMessage.handleError(npe));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getAllTerceirizados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTerceirizados() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        try {
           List<FuncionarioModel> terceirirzados = funcionariosDAO.getAllTerceirizados();
           json = gson.toJson(terceirirzados);
           connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            json = gson.toJson(ErrorMessage.handleError(sqle));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getTerceirizado/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerceirizado(@PathParam("codigo") int codigo){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json;
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        try {
            FuncionarioModel funcionarioModel = funcionariosDAO.getTerceirizado(codigo);
            json = gson.toJson(funcionarioModel);
            connectSQLServer.dbConnect().close();
        }catch(SQLException sqle) {
            json = gson.toJson(ErrorMessage.handleError(sqle));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/updateTerceirizado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizaTerceirizado(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        FuncionarioModel terceirizado = gson.fromJson(object, FuncionarioModel.class);
        String json = "";
        try {
            if (funcionariosDAO.atualizarTerceirizado(terceirizado)) {
                connectSQLServer.dbConnect().close();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", "As alterações foram salvas com sucesso");
                json = gson.toJson(jsonObject);
            }
        }catch (SQLException sqle) {
            json = gson.toJson(ErrorMessage.handleError(sqle));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getTerceirizadosNaoAlocados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerceirizadosNaoAlocados() {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            List<FuncionarioModel> funcinoariosNaoAlocados = funcionariosDAO.getTerceirizadosNaoAlocados();
            json = gson.toJson(funcinoariosNaoAlocados);
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(sqle))).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/verificaExistenciaTerceirizado/{cpf}/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verficaExistenciaTerceirizado(@PathParam("cpf") String cpf, @PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        FuncionariosDAO funcionariosDAO = new FuncionariosDAO(connectSQLServer.dbConnect());
        try {
            FuncionarioModel terceirizado = funcionariosDAO.getFuncionarioPorCPF(cpf);
            json = gson.toJson(terceirizado);
            if(terceirizado == null) {
                if(funcionariosDAO.verificaTerceirizadoExisteContrato(cpf)){
                    json = gson.toJson(ErrorMessage.handleError(new RuntimeException("Terceirizado está ativo em outro contrato tente outro CPF !")));
                    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
                }
            }

            connectSQLServer.dbConnect().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
 }