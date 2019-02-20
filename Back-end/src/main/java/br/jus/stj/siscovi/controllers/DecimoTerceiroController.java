package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.calculos.RestituicaoDecimoTerceiro;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.ContratoDAO;
import br.jus.stj.siscovi.dao.DecimoTerceiroDAO;
import br.jus.stj.siscovi.dao.UsuarioDAO;
import br.jus.stj.siscovi.helpers.CalculosPendentesDecTerHelper;
import br.jus.stj.siscovi.helpers.ErrorMessage;
import br.jus.stj.siscovi.model.AvaliacaoDecimoTerceiro;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.TerceirizadoDecimoTerceiro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/decimo-terceiro")
public class DecimoTerceiroController {

    @GET
    @Path("/getTerceirizadosDecimoTerceiro={codigoContrato}/{tipoRestituicao}/{anoContagem}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerceirizadosParaDecimoTerceiro (@PathParam("codigoContrato") int codigoContrato,
                                                        @PathParam("tipoRestituicao") String tipoRestituicao,
                                                        @PathParam("anoContagem") int anoContagem) {
        Gson gson = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json = "";
            json = gson.toJson(decimoTerceiroDAO.getListaTerceirizadoParaCalculoDeDecimoTerceiro(codigoContrato, anoContagem));
        try {
            connectSQLServer.dbConnect().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/calcularDecimoTerceiroTerceirizados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calcularDecimoTerceiroListaTerceirizados(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ArrayList<TerceirizadoDecimoTerceiro> lista = gson.fromJson(object, new TypeToken<List<TerceirizadoDecimoTerceiro>>(){}.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoDecimoTerceiro restituicaoDecimoTerceiro = new RestituicaoDecimoTerceiro(connectSQLServer.dbConnect());
        String json = "";
        for(TerceirizadoDecimoTerceiro calculo : lista) {
            Date ultimoDiaDoAno = Date.valueOf("" + calculo.getInicioContagem().toLocalDate().getYear() + "-12-31");
            try {
                calculo.setValoresDecimoTerceiro(restituicaoDecimoTerceiro.CalculaRestituicaoDecimoTerceiro(calculo.getCodigoTerceirizadoContrato(), calculo.getParcelas(),
                        calculo.getInicioContagem(), ultimoDiaDoAno));
                calculo.setFimContagem(ultimoDiaDoAno);
            }catch(NullPointerException npe) {
                ErrorMessage error = new ErrorMessage();
                error.error = npe.getMessage();
                json = gson.toJson(error);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
        }
        json = gson.toJson(lista);
        try {
            connectSQLServer.dbConnect().close();
        }catch(SQLException sqle) {
            return Response.accepted().status(500).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/registrarCalculoDecimoTerceiro")
    public Response registratCalculoDecimoTerceiro(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ArrayList<TerceirizadoDecimoTerceiro> lista = gson.fromJson(object, new TypeToken<List<TerceirizadoDecimoTerceiro>>(){}.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        RestituicaoDecimoTerceiro restituicaoDecimoTerceiro = new RestituicaoDecimoTerceiro(connectSQLServer.dbConnect());
        String json = "";
        for(TerceirizadoDecimoTerceiro decimoTerceiro : lista) {
            Date ultimoDiaDoAno = Date.valueOf("" + decimoTerceiro.getInicioContagem().toLocalDate().getYear() + "-12-31");
            if(decimoTerceiro.getTipoRestituicao().equals("RESGATE")) {
                try {
                    restituicaoDecimoTerceiro.RegistraRestituicaoDecimoTerceiro(decimoTerceiro.getCodigoTerceirizadoContrato(),
                            decimoTerceiro.getTipoRestituicao(),
                            decimoTerceiro.getParcelas(),
                            decimoTerceiro.getInicioContagem(),
                            decimoTerceiro.getValoresDecimoTerceiro().getValorDecimoTerceiro(),
                            decimoTerceiro.getValoresDecimoTerceiro().getValorIncidenciaDecimoTerceiro(),
                            decimoTerceiro.getValorMovimentado(),
                            decimoTerceiro.getId());
                }catch(NullPointerException npe) {
                    System.err.println(npe.getStackTrace());
                    ErrorMessage error = new ErrorMessage();
                    error.error = "Houve um erro ao tentar registrar o cálculo de Décimo Terceiro";
                    json = gson.toJson(error);
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                }
            }else if(decimoTerceiro.getTipoRestituicao().equals("MOVIMENTAÇÃO")) {
                try {
                    decimoTerceiro.setValoresDecimoTerceiro(restituicaoDecimoTerceiro.CalculaRestituicaoDecimoTerceiro(decimoTerceiro.getCodigoTerceirizadoContrato(),
                            decimoTerceiro.getParcelas(), decimoTerceiro.getInicioContagem(), ultimoDiaDoAno));
                    decimoTerceiro.setFimContagem(ultimoDiaDoAno);
                    restituicaoDecimoTerceiro.RegistraRestituicaoDecimoTerceiro(decimoTerceiro.getCodigoTerceirizadoContrato(),
                            decimoTerceiro.getTipoRestituicao(),
                            decimoTerceiro.getParcelas(),
                            decimoTerceiro.getInicioContagem(),
                            decimoTerceiro.getValoresDecimoTerceiro().getValorDecimoTerceiro(),
                            decimoTerceiro.getValoresDecimoTerceiro().getValorIncidenciaDecimoTerceiro(),
                            decimoTerceiro.getValorMovimentado(),
                            decimoTerceiro.getId());
                } catch(NullPointerException npe) {
                    ErrorMessage error = new ErrorMessage();
                    error.error = "Houve um erro ao tentar registrar o cálculo !";
                    json = gson.toJson(error);
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                }catch (RuntimeException rte) {
                    json = gson.toJson(ErrorMessage.handleError(rte));
                    return Response.ok(json, MediaType.APPLICATION_JSON).build();
                }
            }
        }
        try {
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            return Response.accepted().status(500).build();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", true);
        json = gson.toJson(jsonObject);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentes/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesDecTer(@PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        try {
            List<ContratoModel> contratos = new ContratoDAO(connectSQLServer.dbConnect())
                    .getCodigosContratosCalculosPendentes(codigoUsuario, 2);
            JsonArray jsonArray = new JsonArray();
            for(ContratoModel contrato : contratos) {
                jsonArray.add(CalculosPendentesDecTerHelper.formataCalculosPendentes(contrato, gson, decimoTerceiroDAO,
                        codigoUsuario, 1));
            }
            json = gson.toJson(jsonArray);
            connectSQLServer.dbConnect().close();
        }catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/avaliarCalculosPendentes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response avaliarCalculosPendentes(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<AvaliacaoDecimoTerceiro> avaliacoesDecimoTerceiro = gson.fromJson(object,
                new TypeToken<List<AvaliacaoDecimoTerceiro>>(){}.getType());
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            for(AvaliacaoDecimoTerceiro avaliacaoDecimoTerceiro : avaliacoesDecimoTerceiro) {
                decimoTerceiroDAO.salvarAlteracoesCalculo(avaliacaoDecimoTerceiro);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", "As alterações foram feitas com sucesso");
            json = gson.toJson(jsonObject);
        }catch (Exception ex) {
            json = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentesNegados/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesNegados(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        try {
            json = gson.toJson(decimoTerceiroDAO.getCalculosPendentesNegados(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        } catch (SQLException slqe) {
            slqe.printStackTrace();
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
        }catch (RuntimeException re) {
            System.err.println(re.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = re.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosPendentesExecucao/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosPendentesExecucao(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try {
            json = gson.toJson(decimoTerceiroDAO.getCalculosPendentesExecucao(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch (SQLException sqle) {
            sqle.printStackTrace();
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
        }catch(RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getCalculosNaoPendentesNegados/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalculosNaoPendentesNegados(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        try {
            json = gson.toJson(decimoTerceiroDAO.getCalculosNaoPendentesNegados(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        } catch (SQLException slqe) {
            slqe.printStackTrace();
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
        }catch (RuntimeException re) {
            System.err.println(re.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = re.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/executarCalculos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response executarCalculos(String object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AvaliacaoDecimoTerceiro avaliacaoDecimoTerceiro = gson.fromJson(object, AvaliacaoDecimoTerceiro.class);
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json = "";
        try {
            if (decimoTerceiroDAO.executarCalculos(avaliacaoDecimoTerceiro)) {
                connectSQLServer.dbConnect().close();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", "As alterações foram salvas com sucesso");
                json = gson.toJson(jsonObject);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
        }catch (SQLException sqle) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getRestituicoes/{codigoContrato}/{codigoUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestituicoes(@PathParam("codigoContrato") int codigoContrato, @PathParam("codigoUsuario") int codigoUsuario) {
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connectSQLServer.dbConnect());
        String json;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try{
            json = gson.toJson(decimoTerceiroDAO.getRestituicoes(codigoContrato, codigoUsuario));
            connectSQLServer.dbConnect().close();
        }catch (SQLException slqe) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = "Houve um erro ao tentar salvar as execuções de cálculos !";
            json = gson.toJson(errorMessage);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }catch (RuntimeException rte) {
            System.err.println(rte.toString());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.error = rte.getMessage();
            json = gson.toJson(errorMessage);
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    @GET
    @Path("/getAnosCalculoDecimoTerceiro/{codigoContrato}/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnosCalculoDecimoTerceiro(@PathParam("codigoContrato") int codigoContrato,
                                                 @PathParam("username") String username){
        Gson gson = new Gson();
        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        Connection connection = connectSQLServer.dbConnect();
        DecimoTerceiroDAO decimoTerceiroDAO = new DecimoTerceiroDAO(connection);
        List<Integer> anos;
        try {
            if(new UsuarioDAO(connection).isAdmin(username) ||
                    new UsuarioDAO(connection).isGestor(username, codigoContrato)) {
                anos = decimoTerceiroDAO.getAnosDecimoTerceiro(codigoContrato);
            } else {
                throw new RuntimeException("Permissão negada !");
            }
            connection.close();
        } catch (Exception ex) {
            String error = gson.toJson(ErrorMessage.handleError(ex));
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        return Response.ok(gson.toJson(anos), MediaType.APPLICATION_JSON).build();
    }
}