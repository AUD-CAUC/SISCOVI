package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.RestituicaoRescisao;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.RescisaoDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.CalcularRescisaoModel;
import br.jus.stj.siscovi.model.CalculoRestituicaoRescisaoModel;
import br.jus.stj.siscovi.model.TerceirizadoRescisao;
import br.jus.stj.siscovi.model.ValorRestituicaoRescisaoModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/rescisao")
public class RescisaoController {
    @GET
    @Path("/getTerceirizadosRescisao={codigoContrato}/{tipoRestituicao}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerceirizadosParaRescisao (@PathParam("codigoContrato") int codigoContrato,
                                                  @PathParam("tipoRestituicao") String tipoRestituicao) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RescisaoDAO rescisaoDAO = new RescisaoDAO(connectSQLServer.dbConnect());
        String json = "";
        json = gson.toJson(rescisaoDAO.getListaTerceirizadoParaCalculoDeRescisao(codigoContrato));

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/calculaRescisaoTerceirizados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calculaRescisaoTerceirizados(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CalcularRescisaoModel crm = gson.fromJson(object, CalcularRescisaoModel.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoRescisao restituicaoRescisao = new RestituicaoRescisao(connectSQLServer.dbConnect());
        String json = "";
        try {
            if(crm.getTipoRestituicao().equals("MOVIMENTACAO")) {
                crm.setTipoRestituicao("MOVIMENTAÇÃO");
            } else if (crm.getTipoRestituicao().equals("RESGATE")) {

            }
            ValorRestituicaoRescisaoModel vrrm = restituicaoRescisao.CalculaRestituicaoRescisao(crm.getCodTerceirizadoContrato(),
                    crm.getDataDesligamento(),
                    crm.getInicioFeriasIntegrais(),
                    crm.getFimFeriasIntegrais(),
                    crm.getInicioFeriasProporcionais(),
                    crm.getDataDesligamento());
            json = gson.toJson(vrrm);
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
    @GET
    @Path("/getRestituicoesRescisao/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosRestituidos(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RescisaoDAO rescisaoDAO = new RescisaoDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            json = gson.toJson(rescisaoDAO.getRestituicoesRescisao(codigoContrato, codigoUsuario));
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

}