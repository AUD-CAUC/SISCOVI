package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.RestituicaoRescisao;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.RescisaoDAO;
import br.jus.stj.siscovi.helpers.ErrorMessage;
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
        List<TerceirizadoRescisao> lista = gson.fromJson(object, new TypeToken<List<TerceirizadoRescisao>>(){}.getType());
        List<CalculoRestituicaoRescisaoModel> lista1 = new ArrayList<>();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoRescisao restituicaoRescisao = new RestituicaoRescisao(connectSQLServer.dbConnect());
        String json = "";
        try {
            for (TerceirizadoRescisao terceirizadoRescisao : lista) {
                ValorRestituicaoRescisaoModel valorRestituicaoRescisaoModel = restituicaoRescisao.CalculaRestituicaoRescisao(terceirizadoRescisao.getCodTerceirizadoContrato(), terceirizadoRescisao.getDataDesligamento(), terceirizadoRescisao.getpDataInicioFeriasIntegrais(),
                terceirizadoRescisao.getpDataFimFeriasIntegrais(), terceirizadoRescisao.getpDataInicioFeriasProporcionais(), terceirizadoRescisao.getpDataFimFeriasProporcionais());
                CalculoRestituicaoRescisaoModel crrm = new CalculoRestituicaoRescisaoModel(terceirizadoRescisao, valorRestituicaoRescisaoModel);
                lista1.add(crrm);
            }
        }catch (NullPointerException npe) {
            json = gson.toJson(new ErrorMessage().handleError(npe));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch(RuntimeException rte) {
            json = gson.toJson(new ErrorMessage().handleError(rte));
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        try {
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            sqle.printStackTrace();
            System.err.println(sqle.getStackTrace());
            System.err.println(sqle.toString());
            return Response.status(500).build();
        }
        json = gson.toJson(lista1);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}