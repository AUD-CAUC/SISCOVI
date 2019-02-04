package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ConvencoesDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/convencao")
public class ConvencoesController {

    @GET
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
    }
}
