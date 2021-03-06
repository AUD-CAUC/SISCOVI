package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.EventoContratualModel;
import br.jus.stj.siscovi.model.TipoEventoContratualModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.org.apache.regexp.internal.RE;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Path("/contrato")
public class ContratoController {


    @GET
    @Path("/getContrato/usuario={username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContrato(@PathParam("username") String username){
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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

    @POST
    @Path("/cadastrarContrato/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrarContrato(@PathParam("username") String username, String object){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ContratoModel contrato = gson.fromJson(object, ContratoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Connection connection = connectSQLServer.dbConnect();
        ContratoDAO contratoDAO = new ContratoDAO(connection);
        String json = null;
        try {
            contratoDAO.cadastrarContrato(contrato, username);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "As alterações foram salvas com sucesso");
            json = gson.toJson(jsonObject);
            connection.close();
        }catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json , MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getEventosContratuais/{username}/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventosContratuaisContrato(@PathParam("username") String username, @PathParam("codigoContrato") int codigoContrato){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            List<EventoContratualModel> eventos = contratoDAO.retornaEventosContratuais(username, codigoContrato);
            connectSQLServer.dbConnect().close();
            json = gson.toJson(eventos);
        }catch (Exception ex) {
            ex.printStackTrace();
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.NOT_FOUND).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getAjusteCompleto/{username}/{codigoContrato}/{codigoAjuste}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAjusteCompletoContrato(@PathParam("username") String username, @PathParam("codigoContrato") int codigoContrato, @PathParam("codigoAjuste") int codigoAjuste) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            Connection connection = connectSQLServer.dbConnect();
            if (new UsuarioDAO(connection).isAdmin(username) || new UsuarioDAO(connection).isGestor(username, codigoContrato)) {
                ContratoModel contrato = new ContratoDAO(connectSQLServer.dbConnect()).getEventoContratualCompleto(username, codigoContrato, codigoAjuste);
                json = gson.toJson(contrato);
                connectSQLServer.dbConnect().close();
            }
        } catch (Exception ex) {
            json = new Gson().toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getContratoCompleto/{username}/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContratoCompletoUsuario(@PathParam("username") String username, @PathParam("codigoContrato") int codigoContrato){
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        try {
            Connection connection = connectSQLServer.dbConnect();
            if (new UsuarioDAO(connection).isAdmin(username) || new UsuarioDAO(connection).isGestor(username, codigoContrato)) {
                ContratoModel contrato = new ContratoDAO(connectSQLServer.dbConnect()).getContratoCompleto(username, codigoContrato);
                json = gson.toJson(contrato);
                connectSQLServer.dbConnect().close();
            }
        }catch (Exception ex) {
            json = new Gson().toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getTiposEventosContratuais")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventosContratuais() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        try{
            List<TipoEventoContratualModel> tiposEventosContratuais = new ContratoDAO(connectSQLServer.dbConnect()).getTiposEventosContratuais();
            json = gson.toJson(tiposEventosContratuais);
            connectSQLServer.dbConnect().close();
        }catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/cadastrarAjuste/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrarAjuste(@PathParam("username") String username, String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ContratoModel contrato = gson.fromJson(object, ContratoModel.class);
        String json = "";
        try {
            if (new UsuarioDAO(connectSQLServer.dbConnect()).isAdmin(username) || new UsuarioDAO(connectSQLServer.dbConnect()).isGestor(username, contrato.getCodigo())) {
                ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
                contratoDAO.cadastrarAjusteContrato(contrato, username);
                connectSQLServer.dbConnect().close();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", "O ajuste foi cadastrado com sucesso");
                json = gson.toJson(jsonObject);
            }

        } catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}
