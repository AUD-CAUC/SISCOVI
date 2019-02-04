package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentualModel {
    private String nome;
    private float percentual;
    private Date dataInicio;
    private Date dataFim;
    private Date dataAditamento;

    public PercentualModel(String nome, float percentual, Date dataInício, Date dataFim, Date dataAditamento) {
        this.nome = nome;
        this.percentual = percentual;
        this.dataInicio = dataInício;
        this.dataFim = dataFim;
        this.dataAditamento = dataAditamento;
    }
    public String getNome() {
        return nome;
    }

    public Date getDataAditamento() {
        return dataAditamento;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public float getPercentual() {
        return percentual;
    }

}
