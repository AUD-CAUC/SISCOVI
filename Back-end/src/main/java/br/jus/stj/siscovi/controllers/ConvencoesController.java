package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ConvencoesDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.CadastroConvencaoModel;
import br.jus.stj.siscovi.model.ConvencaoColetivaModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/convencao")
public class ConvencoesController {

    /*@GET
    @Path("/getConvencoesDoContrato={codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConvencoesDosContratos (@PathParam("codigoContrato") int codigoContrato) throws SQLException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ConnectSQLServer connect = new ConnectSQLServer();
        ConvencoesDAO convencoesDAO = new ConvencoesDAO(connect.dbConnect());
        String json = gson.toJson(convencoesDAO.getConvencoesContrato(codigoContrato));
        try {
            connect.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }*/

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConvencoes() {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Connection connection = connectSQLServer.dbConnect();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConvencoesDAO convencoesDAO = new ConvencoesDAO(connection);
        List<ConvencaoColetivaModel> ccm = new ArrayList<>();
        String json = "";
        try {
            ccm = convencoesDAO.getAllConvencoes();
            json = gson.toJson(ccm);
            connection.close();
        } catch (SQLException e) {
            json = gson.toJson(ErrorMessage.handleError(e));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getConvencao={codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConvencoes(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Connection connection = connectSQLServer.dbConnect();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConvencoesDAO convencoesDAO = new ConvencoesDAO(connection);
        String json = "";
        try {
            json = gson.toJson(convencoesDAO.getConvencaoColetiva(codigo));
            connection.close();
        } catch (SQLException e) {
            json = gson.toJson(ErrorMessage.handleError(e));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/criarConvencao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertConvencao(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json;
        CadastroConvencaoModel cadastroConvencaoModel = gson.fromJson(object, CadastroConvencaoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConvencoesDAO convencoesDAO = new ConvencoesDAO(connectSQLServer.dbConnect());
        if (convencoesDAO.InsertConvencao(cadastroConvencaoModel.getConvencaoModel(), cadastroConvencaoModel.getCurrentUser())) {
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
    @PUT
    @Path("/alterarConvencao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alteraRubrica(String object) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        CadastroConvencaoModel cadastroConvencaoModel = gson.fromJson(object, CadastroConvencaoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConvencoesDAO rubricasDAO = new ConvencoesDAO(connectSQLServer.dbConnect());
        String json;
        if(rubricasDAO.AlterarConvencao(cadastroConvencaoModel.getConvencaoModel(), cadastroConvencaoModel.getCurrentUser())) {
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
    @Path("/deleteConvencao/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response apagarRubrica(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConvencoesDAO convencoesDAO = new ConvencoesDAO(connectSQLServer.dbConnect());
        Gson gson = new Gson();
        String json;
        if (convencoesDAO.DeleteConvencao(codigo)) {
            json = gson.toJson("Convencao Apagada Com sucesso !");
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
}
