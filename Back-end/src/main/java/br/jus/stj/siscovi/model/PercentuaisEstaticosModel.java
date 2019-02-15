package br.jus.stj.siscovi.model;


import java.sql.Date;

public class PercentuaisEstaticosModel {
    private String nome;
    private int codigo;
    private float percentual;
    private Date dataInicio;
    private Date dataFim;
    private Date dataAditamento;

    public String getNome() {
        return nome;
    }

    public float getPercentual() {
        return percentual;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public Date getDataAditamento() {
        return dataAditamento;
    }

    public int getCodigo(){ return codigo; }

    public PercentuaisEstaticosModel(int codigo, String nome, float percentual, Date dataInicio, Date dataFim, Date dataAditamento) {
        this.nome = nome;
        this.codigo = codigo;
        this.percentual = percentual;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataAditamento = dataAditamento;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
}
