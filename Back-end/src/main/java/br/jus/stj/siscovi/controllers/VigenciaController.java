package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.VigenciaDAO;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.VigenciaResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/vigencia")
public class VigenciaController {

    @POST
    @Path("/getVigenciaContratos")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getVigenciasContratos(String object) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd/MM/yyyy").create();
        ContratoModel contratos[] = gson.fromJson(object, ContratoModel[].class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        VigenciaDAO vigenciaDAO = new VigenciaDAO(connectSQLServer.dbConnect());
        ContratoDAO contratoDAO = new ContratoDAO(connectSQLServer.dbConnect());
        ArrayList<VigenciaResponseModel> listaVigencias = new ArrayList<>();
        for(int i = 0; i < contratos.length; i++) {
            VigenciaResponseModel vigenciaResponseModel = new VigenciaResponseModel();
            vigenciaResponseModel.setVigencias(vigenciaDAO.retornaVigenciasDeUmContrato(contratos[i].getCodigo()));
            vigenciaResponseModel.setContrato(contratos[i]);
            vigenciaResponseModel.setGestor(contratoDAO.retornaNomeDoGestorDoContrato(contratos[i].getCodigo()));
            listaVigencias.add(vigenciaResponseModel);
        }
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String json = gson.toJson(listaVigencias);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}