package br.jus.stj.siscovi.model;

public class RubricaModel {
    private String nome;
    private String sigla;
    private int codigo;
    private String descricao;

    public RubricaModel(String nome, String sigla, int codigo){
        this.nome = nome;
        this.sigla = sigla;
        this.codigo = codigo;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}
