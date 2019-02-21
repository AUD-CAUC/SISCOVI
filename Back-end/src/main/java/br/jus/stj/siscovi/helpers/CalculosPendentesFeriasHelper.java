package br.jus.stj.siscovi.helpers;

import br.jus.stj.siscovi.dao.FeriasDAO;
import br.jus.stj.siscovi.model.CalcularFeriasModel;
import br.jus.stj.siscovi.model.ContratoModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CalculosPendentesFeriasHelper {

    public static JsonObject formataCalculosPendentes(ContratoModel contrato, Gson gson, FeriasDAO feriasDAO,
                                                      int codigoUsuario, int pOperacao) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("titulo", contrato.getNomeDaEmpresa() + " - Contrato Nº: "
                + contrato.getNumeroDoContrato());
        jsonObject.addProperty("codigo", contrato.getCodigo());
        JsonElement jsonElement = null;
        // Cálculos de férias pendentes de avaliação pela SAD
        if(pOperacao == 1) {
            jsonElement = gson.toJsonTree(feriasDAO.getCalculosPendentes(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
        }
        // Cálculos de férias negados pela SAD
        if(pOperacao == 2) {
            jsonElement = gson.toJsonTree(feriasDAO.getCalculosNegados(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
        }

        // Cálculos de férias pendentes de avaliação pela SOF
        if(pOperacao == 3) {
            jsonElement = gson.toJsonTree(feriasDAO.getCalculosPendentesExecucao(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
        }
        //Cálculos de férias negados pela SOF
        if(pOperacao == 4) {
            jsonElement = gson.toJsonTree(feriasDAO.getCalculosNaoPendentesNegados(contrato.getCodigo(), codigoUsuario),
                    new TypeToken<List<CalcularFeriasModel>>(){}.getType());
        }
        jsonObject.add("calculos", jsonElement);
        return jsonObject;
    }
}
