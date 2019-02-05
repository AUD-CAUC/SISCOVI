package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.PercentualDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.PercentualModel;
import br.jus.stj.siscovi.model.PercentualModelResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/percentual")
public class PercentualController {
    @POST
    @Path("/percentualDosContratos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response retrievePercentuaisDosContratos (String object) throws SQLException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ArrayList<ContratoModel> contratos = gson.fromJson(object, new ArrayList<ContratoModel>().getClass());
        PercentualModelResponse pmr = null;
        ArrayList<PercentualModelResponse> percentuais = new ArrayList<>();
        ArrayList<ContratoModel> contratoModels = new ArrayList<>();
        for(int i = 0; i < contratos.size(); i++) {
            String temp = gson.toJson(contratos.get(i)); // A conversão precisa ser refeita classe a classe pois converter a lista toda não estava funcionando
            contratoModels.add(gson.fromJson(temp, ContratoModel.class)); // realiza a conversão da classe de json para o objeto respectivo e o adiciona à lista
        }
        for(int i = 0; i < contratoModels.size(); i++){
            ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
            PercentualDAO percentualDAO = new PercentualDAO(connectSQLServer.dbConnect());
            pmr = new PercentualModelResponse();
            pmr.setPercentuais(percentualDAO.getPercentuaisDoContrato(contratoModels.get(i).getCodigo())); // insere no Modelo de Resposta de Percentual os percentuais de um contrato
            pmr.setContrato(contratoModels.get(i)); // Insere o contrato no Modelo de Respostas de Percentual
            pmr.setGestor(contratoDAO.retornaNomeDoGestorDoContrato(contratoModels.get(i).getCodigo())); // Adiciona o nome do gestor do contrato no Modelo de resposta de percentual
            percentuais.add(pmr); // Adiciona o modelo de resposta de percentual na lista
        }
        String json = gson.toJson(percentuais);
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getPercentuaisDecimoTerceiro")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPercentuaisDecimoTerceiro() {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PercentualDAO percentualDAO = new PercentualDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            List<PercentualModel> percentuais = percentualDAO.getPercentuaisDecimoTerceiro();
            json = gson.toJson(percentuais);
            connectSQLServer.dbConnect().close();
        }catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getPercentuaisFerias")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPercentuaisFerias() {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PercentualDAO percentualDAO = new PercentualDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try{
            List<PercentualModel> percentuais = percentualDAO.getPercentuaisFerias();
            json = gson.toJson(percentuais);
            connectSQLServer.dbConnect().close();
        }catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(ErrorMessage.handleError(ex))).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}