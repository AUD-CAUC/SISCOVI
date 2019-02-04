package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.TotalMensalAReter;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.TotalMensalDAO;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.model.ListaTotalMensalData;
import br.jus.stj.siscovi.model.RegistroUsuario;
import br.jus.stj.siscovi.model.TotalMensal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.glassfish.jersey.jaxb.internal.XmlJaxbElementProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


@Path("/total-mensal-a-reter")
public class TotalMensalController {

    @GET
    @Path("/getValoresRetidos/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValoresCalculados(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Connection connection = new ConnectSQLServer().dbConnect();
        ContratoDAO contratoDAO = new ContratoDAO(connection);
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connection);
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        String json;
        ArrayList<ListaTotalMensalData> lista = totalMensalDAO.getValoresCalculadosAnteriormente(codigoContrato, contratoDAO.codigoGestorContrato(codigoUsuario, codigoContrato));
        if(lista.size() > 0) {
           json = gson.toJson(lista);
        }else {
            json = gson.toJson(null);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/calculaTotalMensal={codigoUsuario}/codigo={codigoContrato}/mes={mes}/ano={ano}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularTotalMensal(@PathParam("codigoUsuario") int codigoUsuario, @PathParam("codigoContrato") int codigoContrato, @PathParam("mes") int mes, @PathParam("ano") int ano){
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        RegistroUsuario registro = consulta.RetornaRegistroUsuario(codigoUsuario);
        String vLoginUsuario = registro.getpLogin();

        new TotalMensalAReter(connectSQLServer.dbConnect()).CalculaTotalMensal(codigoContrato, mes, ano, vLoginUsuario);
        TotalMensalDAO totalMensalDAO = new TotalMensalDAO(connectSQLServer.dbConnect());

        Gson gson = new Gson();
        ArrayList<TotalMensal> totais = totalMensalDAO.getCalculoRealizado(new ContratoDAO(connectSQLServer.dbConnect()).codigoGestorContrato(codigoUsuario, codigoContrato), codigoContrato, mes, ano);
        String json;
        if(totais.size() > 0) {
            json = gson.toJson(totais);
        }else {
            json = gson.toJson(null);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}
