package br.jus.stj.siscovi.model;

import java.util.ArrayList;

public class DataModel {
    private String token;
    private User user;
    private AreaModel areasPai;
    private AreaModel areasChild;
    private ArrayList<ContratoModel> contratos;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public AreaModel getAreasChild() {
        return areasChild;
    }

    public AreaModel getAreasPai() {
        return areasPai;
    }

    public ArrayList<ContratoModel> getContratos() {
        return contratos;
    }

    public void setAreasChild(AreaModel areasChild) {
        this.areasChild = areasChild;
    }

    public void setAreasPai(AreaModel areasPai) {
        this.areasPai = areasPai;
    }

    public void setContratos(ArrayList<ContratoModel> contratos){
        this.contratos = contratos;
    }
}
