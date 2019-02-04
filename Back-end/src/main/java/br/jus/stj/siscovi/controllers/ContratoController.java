package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/contrato")
public class ContratoController {
    @GET
    @Path("/getContrato/usuario={username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContrato(@PathParam("username") String username){
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json = "";
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        try {
            json = gson.toJson(contratoDAO.retornaContratoDoUsuario(username));
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getGestorContrato={codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGestorContrato(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new Gson();
        String json;
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        String nomeGestor = contratoDAO.retornaNomeDoGestorDoContrato(codigo);
        if (nomeGestor != null) {
            json = gson.toJson(nomeGestor);
        }else {
            ErrorMessage errorMessage = new ErrorMessage();
            json = gson.toJson(errorMessage.error = "Este contrato não existe !");
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}