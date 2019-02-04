package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class CargoFuncionariosResponseModel {
    private ArrayList<CargosFuncionariosModel> cargosFuncionarios;
    private String nomeCargo;

    public void setCargosFuncionarios(ArrayList<CargosFuncionariosModel> cargosFuncionarios) {
        this.cargosFuncionarios = cargosFuncionarios;
    }

    public void setNomeCargo(String nomeCargo) {
        this.nomeCargo = nomeCargo;
    }
}
