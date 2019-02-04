package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentualModel {
    private String nome;
    private float percentual;
    private Date dataInicio;
    private Date dataFim;
    private Date dataAditamento;
    private int codigo;
    private RubricaModel rubrica;

    public PercentualModel(String nome, float percentual, Date dataInício, Date dataFim, Date dataAditamento) {
        this.nome = nome;
        this.percentual = percentual;
        this.dataInicio = dataInício;
        this.dataFim = dataFim;
        this.dataAditamento = dataAditamento;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public RubricaModel getRubrica() {
        return rubrica;
    }

    public void setRubrica(RubricaModel rubrica) {
        this.rubrica = rubrica;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    @Override
    public String toString() {
        return "PercentualModel{" +
                "nome='" + nome + '\'' +
                ", percentual=" + percentual +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", dataAditamento=" + dataAditamento +
                ", codigo=" + codigo +
                ", rubrica=" + rubrica +
                '}';
    }
}
