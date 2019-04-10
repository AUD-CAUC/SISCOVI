package br.jus.stj.siscovi.helpers;

import br.jus.stj.siscovi.dao.SaldoResidualDAO;
import br.jus.stj.siscovi.model.ContratoModel;
import br.jus.stj.siscovi.model.SaldoResidualRestituidoFerias;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CalculosPendentesResiduaisHelper {

    public static JsonObject formataCalculosPendentes(ContratoModel contrato, Gson gson, SaldoResidualDAO saldoResidualDAO,
                                                      int codigoUsuario, int pOperacao) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("titulo", contrato.getNomeDaEmpresa() + " - Contrato Nº: "
                + contrato.getNumeroDoContrato());
        jsonObject.addProperty("codigo", contrato.getCodigo());
        JsonElement jsonElementFerias = null;
        JsonElement jsonElementDecTer = null;
        JsonElement jsonElementRescisao = null;
        // Cálculos de férias pendentes de avaliação pela SAD
        if(pOperacao == 1) {
            jsonElementFerias = gson.toJsonTree(saldoResidualDAO.getCalculosPendentesFerias(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<SaldoResidualRestituidoFerias>>(){}.getType());

            jsonElementDecTer = gson.toJsonTree(saldoResidualDAO.getCalculosPendentesDecTer(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<SaldoResidualRestituidoFerias>>(){}.getType());
        }
//        // Cálculos de férias negados pela SAD
//        if(pOperacao == 2) {
//            jsonElementFerias = gson.toJsonTree(saldoResidualDAO.getCalculosNegados(contrato.getCodigo(), codigoUsuario),
//                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
//        }
//
//        // Cálculos de férias pendentes de avaliação pela SOF
//        if(pOperacao == 3) {
//            jsonElementFerias = gson.toJsonTree(saldoResidualDAO.getCalculosPendentesExecucao(contrato.getCodigo(), codigoUsuario),
//                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
//        }
//        //Cálculos de férias negados pela SOF
//        if(pOperacao == 4) {
//            jsonElementFerias = gson.toJsonTree(saldoResidualDAO.getCalculosNaoPendentesNegados(contrato.getCodigo(), codigoUsuario),
//                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
//        }
        jsonObject.add("calculosFerias", jsonElementFerias);
        jsonObject.add("calculosDecTer", jsonElementDecTer);
        return jsonObject;
    }

}
