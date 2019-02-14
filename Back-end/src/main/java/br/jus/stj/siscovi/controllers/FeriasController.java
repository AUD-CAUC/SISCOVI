package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.calculos.RestituicaoFerias;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.FeriasDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/ferias")
public class FeriasController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getTerceirizadosFerias={codigoContrato}/{tipoRestituicao}")
    public Response getTerceirizadosParaFerias(@PathParam("codigoContrato") int codigoContrato, @PathParam("tipoRestituicao") String tipoRestituicao) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO= new FeriasDAO(connectSQLServer.dbConnect());
        String json = "";
        json = gson.toJson(feriasDAO.getListaTerceirizadoParaCalculoDeFerias(codigoContrato));
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getValorRestituicaoFerias")
    public Response getValoresFeriasTerceirizado(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CalcularFeriasModel cfm = gson.fromJson(object, CalcularFeriasModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoFerias restituicaoFerias = new RestituicaoFerias(connectSQLServer.dbConnect());
        String json = "";
        try {
            if(cfm.getTipoRestituicao().equals("MOVIMENTACAO")) {
                cfm.setTipoRestituicao("MOVIMENTAÇÃO");
            } else if (cfm.getTipoRestituicao().equals("RESGATE")) {

            }
        ValorRestituicaoFeriasModel vrfm = restituicaoFerias.CalculaRestituicaoFerias(cfm.getCodTerceirizadoContrato(),
                cfm.getDiasVendidos(),
                cfm.getInicioFerias(),
                cfm.getFimFerias(),
                cfm.getInicioPeriodoAquisitivo(),
                cfm.getFimPeriodoAquisitivo());
        json = gson.toJson(vrfm);
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            System.err.println(e.toString());
        }catch(NullPointerException npe) {
            npe.printStackTrace();
            System.err.println(npe.toString());
            ErrorMessage error = new ErrorMessage();
            error.error = npe.getMessage();
            json = gson.toJson(error);
            return Response.accepted(json).status(200).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/calcularFeriasTerceirizados")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calcularFeriasTerceirizados(String object) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        ArrayList<CalcularFeriasModel> listaTerceirizadosParaCalculo = gson.fromJson(object, new TypeToken<List<CalcularFeriasModel>>(){}.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoFerias restituicaoFerias = new RestituicaoFerias(connectSQLServer.dbConnect());
        if (listaTerceirizadosParaCalculo.get(0).getTipoRestituicao().equals("RESGATE")) {
            for (CalcularFeriasModel calcularFeriasModel : listaTerceirizadosParaCalculo) {
                restituicaoFerias.RegistraRestituicaoFerias(calcularFeriasModel.getCodTerceirizadoContrato(),
                        calcularFeriasModel.getTipoRestituicao(),
                        calcularFeriasModel.getDiasVendidos(),
                        calcularFeriasModel.getInicioFerias(),
                        calcularFeriasModel.getFimFerias(),
                        calcularFeriasModel.getInicioPeriodoAquisitivo(),
                        calcularFeriasModel.getFimPeriodoAquisitivo(),
                        calcularFeriasModel.getParcelas(),
                        0,
                        calcularFeriasModel.getpTotalFerias(),
                        calcularFeriasModel.getpTotalTercoConstitucional(),
                        calcularFeriasModel.getpTotalIncidenciaFerias(),
                        calcularFeriasModel.getpTotalIncidenciaTerco(),
                        calcularFeriasModel.getUsername().toUpperCase());
            }
        }
        if (listaTerceirizadosParaCalculo.get(0).getTipoRestituicao().equals("MOVIMENTAÇÃO")) {
            for (CalcularFeriasModel calcularFeriasModel : listaTerceirizadosParaCalculo) {
                restituicaoFerias.RegistraRestituicaoFerias(calcularFeriasModel.getCodTerceirizadoContrato(),
                        calcularFeriasModel.getTipoRestituicao(),
                        calcularFeriasModel.getDiasVendidos(),
                        calcularFeriasModel.getInicioFerias(),
                        calcularFeriasModel.getFimFerias(),
                        calcularFeriasModel.getInicioPeriodoAquisitivo(),
                        calcularFeriasModel.getFimPeriodoAquisitivo(),
                        calcularFeriasModel.getParcelas(),
                        calcularFeriasModel.getValorMovimentado(),
                        calcularFeriasModel.getpTotalFerias(),
                        calcularFeriasModel.getpTotalTercoConstitucional(),
                        calcularFeriasModel.getpTotalIncidenciaFerias(),
                        calcularFeriasModel.getpTotalIncidenciaTerco(),
                        calcularFeriasModel.getUsername().toUpperCase());
            }
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            System.err.println(e.toString());
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", true);
        String json = gson.toJson(jsonObject);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentes={codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentes(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            json = gson.toJson(feriasDAO.getCalculosPendentes(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch(NullPointerException npe) {
            System.err.println(npe.toString());
            ErrorMessage error = new ErrorMessage();
            error.error = npe.getMessage();
            json = gson.toJson(error);
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentesNegados={codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesNegados(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            json = gson.toJson(feriasDAO.getCalculosNegados(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch(NullPointerException erro) {
            System.err.println(erro.toString());
            ErrorMessage error = new ErrorMessage();
            error.error = erro.getMessage();
            json = gson.toJson(error);
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentesExecucao={codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesExecucao(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            json = gson.toJson(feriasDAO.getCalculosPendentesExecucao(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch(NullPointerException npe) {
            npe.printStackTrace();
            ErrorMessage error = new ErrorMessage();
            error.error = npe.getMessage();
            json = gson.toJson(error);
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getCalculosNaoPendentesNegados={codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosNaoPendentesNegados(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            json = gson.toJson(feriasDAO.getCalculosNaoPendentesNegados(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch(NullPointerException npe) {
            npe.printStackTrace();
            ErrorMessage error = new ErrorMessage();
            error.error = npe.getMessage();
            json = gson.toJson(error);
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/salvarFeriasAvaliadas")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response salvarFeriasAvaliadas(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AvaliacaoFerias avaliacaoFerias = gson.fromJson(object, AvaliacaoFerias.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        if(feriasDAO.salvaAvaliacaoCalculosFerias(avaliacaoFerias)) {
            try {
               connectSQLServer.dbConnect().close();
            }catch (SQLException sqle) {
                String json = gson.toJson(new ErrorMessage().handleError(sqle));
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", true);
            String json = gson.toJson(jsonObject);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }else {
            return Response.status(409).build();
        }
    }

    @GET
    @Path("/getRetencoesFerias/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosRestituidos(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            json = gson.toJson(feriasDAO.getRestituicoesFerias(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch(NullPointerException exception) {
            exception.printStackTrace();
            ErrorMessage error = new ErrorMessage();
            error.error = exception.getMessage();
            json = gson.toJson(error);
        }catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @PUT
    @Path("/salvarExecucaoFerias")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response salvarExecucaoFerias(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AvaliacaoFerias avaliacaoFerias = gson.fromJson(object, AvaliacaoFerias.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        if(feriasDAO.salvarExecucaoFerias(avaliacaoFerias)) {
            try {
                connectSQLServer.dbConnect().close();
            }catch (SQLException sqle) {
                System.err.println(sqle.getStackTrace());
                String json = gson.toJson(sqle);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", true);
            String json = gson.toJson(jsonObject);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }else {
            return Response.status(409).build();
        }
    }


    @GET
    @Path("/getCalculosPendentes/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentes(@PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            JsonArray jsonArray = new JsonArray();
            List<ContratoModel> contratos = new ContratoDAO(connectSQLServer.dbConnect()).getCodigosContratosCalculosPendentes();
            for(ContratoModel contrato : contratos) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("titulo", contrato.getNomeDaEmpresa() + " - Contrato Nº: " + contrato.getNumeroDoContrato());
                jsonObject.addProperty("codigo", contrato.getCodigo());
                JsonElement jsonElement = gson.toJsonTree(feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario), new TypeToken<List<CalcularFeriasModel>>(){}.getType());
                jsonObject.add("calculos", jsonElement);
                jsonArray.add(jsonObject);

                // JsonArray ja = new JSONArray(feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario));
                // jsonObject.addProperty("calculosPendentes", feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario));

            }
            json = gson.toJson(jsonArray);
            connectSQLServer.dbConnect().close();
        }catch(Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return  Response.status(Response.Status.NOT_FOUND).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentesNegados/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesNegadosLista(@PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        FeriasDAO feriasDAO = new FeriasDAO(connectSQLServer.dbConnect());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String json = "";
        try {
            JsonArray jsonArray = new JsonArray();
            List<ContratoModel> contratos = new ContratoDAO(connectSQLServer.dbConnect()).getCodigosContratosCalculosPendentes();
            for(ContratoModel contrato : contratos) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("titulo", contrato.getNomeDaEmpresa() + " - Contrato Nº: " + contrato.getNumeroDoContrato());
                jsonObject.addProperty("codigo", contrato.getCodigo());
                JsonElement jsonElement = gson.toJsonTree(feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario), new TypeToken<List<CalcularFeriasModel>>(){}.getType());
                jsonObject.add("calculos", jsonElement);
                jsonArray.add(jsonObject);

                // JsonArray ja = new JSONArray(feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario));
                // jsonObject.addProperty("calculosPendentes", feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario));

            }
            json = gson.toJson(jsonArray);
            connectSQLServer.dbConnect().close();
        }catch(Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return  Response.status(Response.Status.NOT_FOUND).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}
