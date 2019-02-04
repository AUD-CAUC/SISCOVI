package br.jus.stj.siscovi.model;

public class DatasDeCalculoModel {
    private final int ano;
    private final int mes;

    public DatasDeCalculoModel(long ano, long mes) {
        this.ano = (int) ano;
        this.mes = (int) mes;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }
}
