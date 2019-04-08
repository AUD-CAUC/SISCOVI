package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.*;
import br.jus.stj.siscovi.helpers.CalculosPendentesResiduaisHelper;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.AvaliacaoResiduais;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.SaldoResidualRescisao;
import br.jus.stj.siscovi.model.SaldoResidualRestituidoFerias;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/saldo-residual")
public class SaldoResidualController {

    @GET
    @Path("/getSaldoResidualFerias/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoResidualFerias(@PathParam("codigoContrato") int codigoContrato) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        SaldoResidualDAO saldoResidualDAO = new SaldoResidualDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(saldoResidualDAO.getSaldoResidualFeriasRestituido(codigoContrato));
            connectSQLServer.dbConnect().close();
        }catch (SQLException slqe) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar recuperar o saldo residual de férias da conta vinculada!";
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch (RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getSaldoResidualDecimoTerceiro/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoResidualDecimoTerceiro(@PathParam("codigoContrato") int codigoContrato) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        SaldoResidualDAO saldoResidualDAO = new SaldoResidualDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(saldoResidualDAO.getSaldoResidualDecimoTerceiroRestituido(codigoContrato));
            connectSQLServer.dbConnect().close();
        }catch (SQLException slqe) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar recuperar o saldo residual de décimo tericeiro da conta vinculada!";
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch (RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getSaldoResidualRescisao/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoResidualRescisao(@PathParam("codigoContrato") int codigoContrato) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        SaldoResidualDAO saldorescisao = new SaldoResidualDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(saldorescisao.getSaldoResidualRescisaoRestituido(codigoContrato));
            connectSQLServer.dbConnect().close();
        }catch (SQLException slqe) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar recuperar o saldo residual de rescisão da conta vinculada!";
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch (RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/confirmarFeriasResiduais")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response salvarFeriasAvaliadas(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<AvaliacaoResiduais> avaliacoesResiduais = gson.fromJson(object, new TypeToken<List<AvaliacaoResiduais>>() {
        }.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        SaldoResidualDAO saldoResidualDAO = new SaldoResidualDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            for (AvaliacaoResiduais avaliacaoResiduais : avaliacoesResiduais) {
                saldoResidualDAO.confirmarFeriasResiduais(avaliacaoResiduais);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", true);
            json = gson.toJson(jsonObject);
        } catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}