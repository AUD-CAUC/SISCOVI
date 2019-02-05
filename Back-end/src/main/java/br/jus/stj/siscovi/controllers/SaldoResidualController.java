package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.SaldoResidualDecimoTerceiroDAO;
import br.jus.stj.siscovi.dao.SaldoResidualFeriasDAO;
import br.jus.stj.siscovi.dao.SaldoResidualRescisaoDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.SaldoResidualRescisao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/saldo-residual")
public class SaldoResidualController {

    @GET
    @Path("/getSaldoResidualFerias/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoResidualFerias(@PathParam("codigoContrato") int codigoContrato) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        SaldoResidualFeriasDAO saldoFerias = new SaldoResidualFeriasDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(saldoFerias.getSaldoResidualFeriasRestituido(codigoContrato));
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
        SaldoResidualDecimoTerceiroDAO saldoDecimoTerceiro = new SaldoResidualDecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(saldoDecimoTerceiro.getSaldoResidualDecimoTerceiroRestituido(codigoContrato));
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
        SaldoResidualRescisaoDAO saldorescisao = new SaldoResidualRescisaoDAO(connectSQLServer.dbConnect());
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

}