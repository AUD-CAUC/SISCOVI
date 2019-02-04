package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class VigenciaResponseModel {
    private String gestor;
    private ArrayList<VigenciaModel> vigencias;
    private ContratoModel contrato;

    public void setVigencias(ArrayList<VigenciaModel> vigencias) {
        this.vigencias = vigencias;
    }

    public void setContrato(ContratoModel contrato) {
        this.contrato = contrato;
    }

    public void setGestor(String gestor) {
        this.gestor = gestor;
    }
}
