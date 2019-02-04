package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class CadastroCargoModel {
    String currentUser;
    ArrayList<CargoModel> cargos;

    public String getCurrentUser() {
        return currentUser;
    }

    public ArrayList<CargoModel> getCargos() {
        return cargos;
    }
}
