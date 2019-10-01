package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ChartDAO;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/chart")
public class ChartController {
    @GET
    @Path("/getSaldoAcumuladoContrato")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllCargos() throws SQLException {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ChartDAO chartDAO = new ChartDAO(connectSQLServer.dbConnect());
//        String json = gson.toJson(chartDAO.getAllCargos());
        connectSQLServer.dbConnect().close();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}
