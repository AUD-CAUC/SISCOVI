package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class FuncionariosResponseModel {
    private ContratoModel contrato;
    private String gestor;
    private ArrayList<FuncionarioModel> funcionarios;

    public void setGestor(String gestor) {
        this.gestor = gestor;
    }

    public void setContrato(ContratoModel contrato) {
        this.contrato = contrato;
    }

    public void setFuncionarios(ArrayList<FuncionarioModel> funcionarios) {
        this.funcionarios = funcionarios;
    }
}
