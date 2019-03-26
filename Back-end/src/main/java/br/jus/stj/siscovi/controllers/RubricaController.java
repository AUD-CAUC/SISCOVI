package br.jus.stj.siscovi.controllers;


import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.RubricasDAO;
import br.jus.stj.siscovi.model.CadastroPercentualDinamicoModel;
import br.jus.stj.siscovi.model.CadastroPercentualEstaticoModel;
import br.jus.stj.siscovi.model.CadastroRubricaModel;
import br.jus.stj.siscovi.model.RubricaModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sun.rmi.runtime.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/rubricas")
public class RubricaController {

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getaAllRubricas() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        Gson gson = new Gson();
        String json;
        json = gson.toJson(rubricasDAO.SelectAllRubricas());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getStaticPercent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPercentuaisEstaticos() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json;
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        json = gson.toJson(rubricasDAO.SelectPercentuaisEstaticos());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getAllDinamicPercent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPercentuaisDinamicos() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json;
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        json = gson.toJson(rubricasDAO.SelectAllPercentuaisDinamicos());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getDinamicPercent/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPercentuaisDinamicos() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json;
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        json = gson.toJson(rubricasDAO.SelectPercentualDinamico());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @POST
    @Path("/cadastrarPercentualDinamico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPercentualDinamico(String object) throws SQLException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json;
        CadastroPercentualDinamicoModel cadastroPercentualDinamicoModel = gson.fromJson(object, CadastroPercentualDinamicoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());

        if (rubricasDAO.InsertPercentualDinamico(cadastroPercentualDinamicoModel.getPercentual(), cadastroPercentualDinamicoModel.getCurrentUser())) {
            json = "Percentual Dinâmico cadastrado com sucesso!";
        } else {
            json = "Algum erro impossibilitou o cadastro do Percentual Dinâmico";
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(gson.toJson(json)).build();
    }

    @POST
    @Path("/cadastrarPercentualEstatico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPercentualEstatico(String object) throws SQLException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json;
        CadastroPercentualEstaticoModel cadastroPercentualEstaticoModel = gson.fromJson(object, CadastroPercentualEstaticoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());

        if (rubricasDAO.InsertPercentualEstatico(cadastroPercentualEstaticoModel.getPercentualEstaticoModel(), cadastroPercentualEstaticoModel.getCurrentUser())) {
            json = "Percentual Estático cadastrado com sucesso!";
        } else {
            json = "Algum erro impossibilitou o cadastro do Percentual Estático";
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(gson.toJson(json)).build();
    }

    @POST
    @Path("/criarRubrica")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPercentuais(String object) {
        Gson gson = new Gson();
        String json;
        CadastroRubricaModel cadastroRubricaModel = gson.fromJson(object, CadastroRubricaModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        if (rubricasDAO.InsertRubrica(cadastroRubricaModel.getRubricaModel(), cadastroRubricaModel.getCurrentUser())) {
            json = "Rubrica Cadastrada Com sucesso !";
        }else {
            json = "Algum erro impossibilitou o cadastro da respectiva rubrica";
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(gson.toJson(json)).build();
    }
    @GET
    @Path("/getRubrica/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRubrica(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        RubricaModel rubricaModel = rubricasDAO.GetRubricas(codigo);
        String json = gson.toJson(rubricaModel);
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @DELETE
    @Path("/deleteRubrica/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response apagarRubrica(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        Gson gson = new Gson();
        String json;
        if (rubricasDAO.DeleteRubrica(codigo)) {
            json = gson.toJson("Rubrica Apagada Com sucesso !");
        }else {
            json = gson.toJson("Houve uma falha ao tentar apagar a Rubrica !");
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @PUT
    @Path("/alterarRubrica")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alteraRubrica(String object) {
        Gson gson = new Gson();
        CadastroRubricaModel cadastroRubricaModel = gson.fromJson(object, CadastroRubricaModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        String json;
        if(rubricasDAO.AlteraRubrica(cadastroRubricaModel.getRubricaModel(), cadastroRubricaModel.getCurrentUser())) {
            json = gson.toJson("Alteração feita com sucesso !");
        }else {
            json = gson.toJson("Houve falha na tentativa de Salvar as Alterações");
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @DELETE
    @Path("/deleteDinamicPercent/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response apagarPercentuaisDinamicos(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RubricasDAO rubricasDAO = new RubricasDAO(connectSQLServer.dbConnect());
        Gson gson = new Gson();
        String json;
        if (rubricasDAO.DeletePercentualDinamico(codigo)) {
            json = gson.toJson("Percentual Dinâmico Apagado Com sucesso !");
        }else {
            json = gson.toJson("Houve uma falha ao tentar apagar o Percentual Dinâmico !");
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}