package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.CargoDAO;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.dao.sql.UpdateTSQL;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.regexp.internal.RE;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/cargo")
public class CargoController {

    @GET
    @Path("/getAllCargos")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllCargos() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        String json = gson.toJson(cargoDAO.getAllCargos());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/getCargosDosContratos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCargosDosContratos(String request) throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        ArrayList<ContratoModel> contratos;
        ArrayList<ContratoModel> contratoModels = new ArrayList<>();
        ArrayList<CargoResponseModel> cargos = new ArrayList<>();
        contratos = gson.fromJson(request, new ArrayList<ContratoModel>().getClass());
        for (int i = 0; i < contratos.size(); i++) {
            String temp = gson.toJson(contratos.get(i));
            contratoModels.add(gson.fromJson(temp, ContratoModel.class));
        }
        for (int i = 0; i < contratoModels.size(); i++) {
            CargoResponseModel cargoResponse = new CargoResponseModel();
            cargoResponse.setCargos(cargoDAO.getCargosDeUmContrato(contratoModels.get(i).getCodigo()));
            cargoResponse.setContrato(contratoModels.get(i));
            cargoResponse.setGestor(contratoDAO.retornaNomeDoGestorDoContrato(contratoModels.get(i).getCodigo()));
            cargos.add(cargoResponse);
        }
        connectSQLServer.dbConnect().close();
        String json = gson.toJson(cargos);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarCargos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrarCargos(String object) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        CadastroCargoModel ccm = gson.fromJson(object, CadastroCargoModel.class);
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        String json;
        if (cargoDAO.cadastroCargos(ccm.getCargos(), ccm.getCurrentUser())) {
            json = gson.toJson("Cadastro realizado com sucesso !");
        } else {
            json = gson.toJson("Ocorreu Algum erro");
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/getFuncoesContrato/{codigo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFuncoesContrato(@PathParam("codigo") int codigo, String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        User user = gson.fromJson(object, User.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        List<CargoModel> funcoes = new ArrayList<>();
        try {
            funcoes = cargoDAO.getFuncoesContrato(codigo, user);
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            return Response.ok(gson.toJson(ErrorMessage.handleError(e))).build();
        }
        return Response.ok(gson.toJson(funcoes)).build();
    }

    @POST
    @Path("/getTerceirizadosFuncao/{codigo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerceirizadosFuncao(@PathParam("codigo") int codigo, String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        User user = gson.fromJson(object, User.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        List<CargosFuncionariosModel> lista = new ArrayList<>();
        try {
            lista = cargoDAO.getTerceirizadosFuncao(codigo, user);
            connectSQLServer.dbConnect().close();
        } catch (SQLException sqle) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(sqle))).build();
        } catch (RuntimeException rte) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(rte))).build();
        }
        return Response.ok(gson.toJson(lista)).build();
    }

    @POST
    @Path("/alocarTerceirizadosContrato/{codigo}/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alocarTerceirizados(String object, @PathParam("codigo") int codigoContrato, @PathParam("username") String username) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<CargosFuncionariosModel> lista = gson.fromJson(object, new TypeToken<List<CargosFuncionariosModel>>() {
        }.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        try {
            InsertTSQL insertTSQL = new InsertTSQL(connectSQLServer.dbConnect());
            CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
            for (CargosFuncionariosModel cfm : lista) {
                int a = insertTSQL.InsertTerceirizadoContrato(codigoContrato, cfm.getFuncionario().getCodigo(), cfm.getDataDisponibilizacao(), cfm.getDataDesligamento(), username);
                int codFuncaoContrato = cargoDAO.recuperaCodigoFuncaoContrato(codigoContrato, cfm.getFuncao().getCodigo());
                insertTSQL.InsertFuncaoTerceirizado(a, codFuncaoContrato, cfm.getDataDisponibilizacao(), null, username);
            }
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(e))).build();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", "O terceirizado foi inserido no contrato com sucesso !");
        String json = gson.toJson(jsonObject);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/alterarFuncaoTerceirizado/{codigoContrato}/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterarFuncaoTerceirizado(String object, @PathParam("codigoContrato") int codigoContrato, @PathParam("username") String username) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<CargosFuncionariosModel> listaFuncionariosAlteracao = gson.fromJson(object, new TypeToken<List<CargosFuncionariosModel>>() {
        }.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        CargoDAO cargoDAO = new CargoDAO(connectSQLServer.dbConnect());
        try {
            for (CargosFuncionariosModel cfm : listaFuncionariosAlteracao) {
                if (!cargoDAO.alterarFuncaoTerceirizado(codigoContrato, cfm.getFuncionario().getCodigo(), cfm.getFuncao().getCodigo(), cfm.getDataDisponibilizacao(), username)) {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.error = "Erro ao tentar alterar a função de um terceirizado. Entre em contato com o administrador do sistema";
                    return Response.ok(gson.toJson(errorMessage)).build();
                }
            }
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(e))).build();
        } catch (RuntimeException rte) {
            return Response.ok(gson.toJson(ErrorMessage.handleError(rte))).build();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", "As alterações foram feitas com sucesso");
        String json = gson.toJson(jsonObject);
        return Response.ok(json).build();
    }
}
