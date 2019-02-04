package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class CargoResponseModel {
    private ArrayList<CargoModel> cargos;
    private ContratoModel contrato;
    private String gestor;
    public void setCargos(ArrayList<CargoModel> cargos) {
        this.cargos = cargos;
    }
    public void setContrato(ContratoModel contrato){
        this.contrato = contrato;
    }
    public void setGestor(String gestor) {
        this.gestor = gestor;
    }
}
