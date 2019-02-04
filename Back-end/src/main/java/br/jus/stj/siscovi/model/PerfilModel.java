package br.jus.stj.siscovi.model;

public class PerfilModel {
    private int cod;
    private String sigla;
    private String descricao;

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCod() {
        return cod;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
