package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.HistoricoDAO;
import br.jus.stj.siscovi.dao.PerfilDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.HistoricoGestorModel;
import br.jus.stj.siscovi.model.PerfilModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.org.apache.regexp.internal.RE;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/historico")
public class HistoricoController {
    @GET
    @Path("/getHistoricoGestores={codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoricoGestores(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        HistoricoDAO historicoDAO = new HistoricoDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        String json = gson.toJson(historicoDAO.getHistoricoGestor(codigo));
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getPerfisGestao")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfisGestao() {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PerfilDAO perfilDAO = new PerfilDAO(connectSQLServer.dbConnect());
        String json =  "";
        try {
            List<PerfilModel> perfis = perfilDAO.getPerfisGestao();
            connectSQLServer.dbConnect().close();
            json = gson.toJson(perfis);
        } catch (SQLException e) {
            ErrorMessage errorMessage = ErrorMessage.handleError(e);
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarGestorContrato")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cadastrarGestorContrato(String object){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        HistoricoGestorModel historicoGestorModel = gson.fromJson(object, HistoricoGestorModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        HistoricoDAO historicoDAO = new HistoricoDAO(connectSQLServer.dbConnect());
        String json = null;
        try{
            if(historicoDAO.createHistoricoGestor(historicoGestorModel)){ connectSQLServer.dbConnect().close();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", "As alterações foram salvas com sucesso");
                json = gson.toJson(jsonObject);
            }
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            ErrorMessage errorMessage = ErrorMessage.handleError(sqle);
            return Response.ok(gson.toJson(errorMessage), MediaType.APPLICATION_JSON).build();
        }catch (NullPointerException npe) {
            ErrorMessage errorMessage = ErrorMessage.handleError(npe);
            return Response.ok(gson.toJson(errorMessage), MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /*@GET
    @Path("/getHistoricoGestor/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHisotircoGestor(@PathParam("codigo") int codigo) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConsultaTSQL consultaTSQL = new ConsultaTSQL(connectSQLServer.dbConnect());
        try{
            consultaTSQL
        }
        return Response.ok().build();
    }*/
}
