package br.jus.stj.siscovi.model;

import java.util.ArrayList;
public class ListaConvencoesModel {
    private String nomeCargo;
    private ArrayList<ConvencaoColetivaModel> convencoes;
    private String gestor;
    public ListaConvencoesModel(String nomeCargo, String gestor){
        this.nomeCargo = nomeCargo;
        this.gestor = gestor;
    }
    public void setConvencoes(ArrayList<ConvencaoColetivaModel> convencoes) {
        this.convencoes = convencoes;
    }
}
