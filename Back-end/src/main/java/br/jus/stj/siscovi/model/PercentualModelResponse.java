package br.jus.stj.siscovi.model;


import java.util.ArrayList;

public class PercentualModelResponse {
    private ArrayList<PercentualModel> percentuais;
    private ContratoModel contrato;
    private String gestor;

    public void setPercentuais(ArrayList<PercentualModel> percentuais){
        this.percentuais = percentuais;
    }
    public void setContrato (ContratoModel contrato) {
        this.contrato = contrato;
    }
    public void adicionaPercentual(PercentualModel percentual) {
        this.percentuais.add(percentual);
    }
    public void setGestor(String gestor) {
        this.gestor = gestor;
    }
}
