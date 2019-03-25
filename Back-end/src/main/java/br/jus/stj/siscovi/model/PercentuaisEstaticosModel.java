package br.jus.stj.siscovi.model;


import java.sql.Date;

public class PercentuaisEstaticosModel {
    private int cod;
    private String nome;
    private int codigoRubrica;
    private float percentual;
    private Date dataInicio;
    private Date dataFim;
    private Date dataAditamento;

    public int getCod() { return cod;}
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

    public Date getDataAditamento() { return dataAditamento; }

    public int getCodigoRubrica(){ return codigoRubrica; }

    public PercentuaisEstaticosModel(int cod, int codigoRubrica, String nome, float percentual, Date dataInicio, Date dataFim, Date dataAditamento) {
        this.cod = cod;
        this.nome = nome;
        this.codigoRubrica = codigoRubrica;
        this.percentual = percentual;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataAditamento = dataAditamento;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
}
