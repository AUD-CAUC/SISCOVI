package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.TotalMensalAReter;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.TotalMensalDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.helpers.Mes;
import br.jus.stj.siscovi.model.AvaliacaoTotalMensal;
import br.jus.stj.siscovi.model.ListaTotalMensalData;
import br.jus.stj.siscovi.model.TotalMensal;
import br.jus.stj.siscovi.model.TotalMensalPendenteModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.research.ws.wadl.Param;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Path("/total-mensal-a-reter")
public class TotalMensalController {

    @GET
    @Path("/getValoresRetidos/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValoresRetidos(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Connection connection = new ConnectSQLServer().dbConnect();
        ContratoDAO contratoDAO = new ContratoDAO(connection);
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connection);
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        String json;
        ArrayList<ListaTotalMensalData> lista = totalMensalDAO.getValoresCalculadosAnteriormente(codigoContrato, contratoDAO.codigoGestorContrato(codigoUsuario, codigoContrato));
        if(lista.size() > 0) {
            if(lista.get(0).getTotaisSize() > 0) {
                json = gson.toJson(lista);
            } else {
                json = gson.toJson(null);
            }
        }else {
            json = gson.toJson(null);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(e.getStackTrace());
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/calculaTotalMensal={codigoUsuario}/codigo={codigoContrato}/mes={mes}/ano={ano}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularTotalMensal(@PathParam("codigoUsuario") int codigoUsuario, @PathParam("codigoContrato") int codigoContrato, @PathParam("mes") int mes, @PathParam("ano") int ano){
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();


        Gson gson = new Gson();
        String json = null;
        try {
            new TotalMensalAReter(connectSQLServer.dbConnect()).CalculaTotalMensal(codigoContrato, mes, ano, "SYSTEM");
            TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
            ArrayList<TotalMensal> totais = totalMensalDAO.getCalculoRealizado(new ContratoDAO(connectSQLServer.dbConnect()).codigoGestorContrato(codigoUsuario, codigoContrato), codigoContrato, mes, ano);

            if (totais.size() > 0) {
                json = gson.toJson(totais);
            }
            connectSQLServer.dbConnect().close();
        } catch(NullPointerException npe) {
            System.err.println(npe.toString());
            ErrorMessage error = new ErrorMessage();
            error.error = npe.getMessage();
            json = gson.toJson(error);
        } catch (SQLException e) {
            System.err.println(e.toString());
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getValoresPendentes/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValoresPendentes(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            ArrayList<TotalMensalPendenteModel> calculosPendentes  = totalMensalDAO.getTotalMensalPendente(codigoContrato, codigoUsuario);
            if(calculosPendentes == null || calculosPendentes.size() == 0) {
                ErrorMessage em = new ErrorMessage();
                em.error = "Nenhum cálculo pendente de avaliação encontrado";
                json = gson.toJson(em);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
           json = gson.toJson(calculosPendentes);

        }catch(RuntimeException rte){
            rte.printStackTrace();
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        try {
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            System.err.println(sqle.getStackTrace());
            return  Response.status(500).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getValoresPendentesExecucao/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValoresPendentesExecucao(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            ArrayList<TotalMensalPendenteModel> calculosPendentes  = totalMensalDAO.getTotalMensalPendenteExecucao(codigoContrato, codigoUsuario);
            if(calculosPendentes == null || calculosPendentes.size() == 0) {
                json = gson.toJson(null);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            json = gson.toJson(calculosPendentes);

        }catch(RuntimeException rtel) {
            System.err.println(rtel.toString());
            json = gson.toJson(new ErrorMessage().handleError(rtel));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        try {
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            System.err.println(sqle.getStackTrace());
            return  Response.status(500).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/enviarAvaliacaoCalculosTotalMensal/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recebeAvaliacaoCalculos(String object) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        AvaliacaoTotalMensal calculosAvaliados = gson.fromJson(object, AvaliacaoTotalMensal.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        if(new TotalMensalDAO(connectSQLServer.dbConnect()).salvaAvaliacaoCalculosPendentes(calculosAvaliados.getCodigoContrato(), calculosAvaliados.getTotalMensalPendenteModels(),
                calculosAvaliados.getUser())) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "As alterações foram feitas com sucesso");
            json = gson.toJson(jsonObject);
        }else {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as avaliações. Tente novamente mais tarde";
            json = gson.toJson(errorMessage);
        }
        try {
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            json = gson.toJson(ErrorMessage.handleError(sqle));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @POST
    @Path("/enviarAvaliacaoCalculosExecutadosTotalMensal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response receveAvaliacaoCalculosExecucao(String object){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AvaliacaoTotalMensal avaliacaoTotalMensal = gson.fromJson(object, AvaliacaoTotalMensal.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        if(new TotalMensalDAO(connectSQLServer.dbConnect()).salvaAvaliacaoCalculosPendentesExecucao(avaliacaoTotalMensal.getCodigoContrato(), avaliacaoTotalMensal.getTotalMensalPendenteModels(),
                avaliacaoTotalMensal.getUser())) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "As alterações foram feitas com sucesso");
            json = gson.toJson(jsonObject);
        }else {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
        }
        try{
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            json = gson.toJson(ErrorMessage.handleError(sqle));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getMesesCalculo/{anoCalculo}/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMesesCalculoValidos(@PathParam("anoCalculo") int anoCalculo, @PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new Gson();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        try{
            TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
            List<Mes> meses = totalMensalDAO.getMesesDeCalculoPermitidosPorAno(codigoContrato, anoCalculo);
            json = gson.toJson(meses);
        }catch (Exception ex) {
            System.err.println(ex.getStackTrace());
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getAnosValidosContrato/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnosValidosContrato(@PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new Gson();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        try{
            TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
            List<Integer> anos = totalMensalDAO.getAnosValidosdoContrato(codigoContrato);
            json = gson.toJson(anos);
        }catch (Exception ex) {
            System.err.println(ex.getStackTrace());
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/apagarTotalMensalAReter/{codigoContrato}/{codigoUsuario}/{anoReferencia}/{mesReferencia}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCalculoTotalMensal(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario, @PathParam("anoReferencia") int anoReferencia,
                                             @PathParam("mesReferencia") int mesReferencia) {
        Gson gson = new Gson();

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();


        return Response.ok().build();
    }

    @GET
    @Path("/getNumFuncAtivos/{mesSelecionado}/{anoSelecionado}/{codigoContrato}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumeroFuncionariosAtivos(@PathParam("mesSelecionado") int mesCalculo, @PathParam("anoSelecionado") int anoCalculo, @PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new Gson();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        String json = "";
        int numFuncionariosAtivos;
        try{
            TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
            numFuncionariosAtivos = totalMensalDAO.getNumFuncionariosAtivos(mesCalculo, anoCalculo, codigoContrato);
            json = gson.toJson(numFuncionariosAtivos);
        }catch (Exception ex) {
            System.err.println(ex.getStackTrace());
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/confirmarTotalMensalReter/{mesSelecionado}/{anoSelecionado}/{codigoContrato}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response salvarFeriasAvaliadas(@PathParam("mesSelecionado") int mesCalculo, @PathParam("anoSelecionado") int anoCalculo, @PathParam("codigoContrato") int codigoContrato) {
        Gson gson = new Gson();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            totalMensalDAO.confirmaCalculo(mesCalculo, anoCalculo, codigoContrato);
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
