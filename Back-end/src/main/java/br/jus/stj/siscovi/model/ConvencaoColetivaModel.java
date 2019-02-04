package br.jus.stj.siscovi.model;

import java.sql.Date;

public class ConvencaoColetivaModel {
    private int codigo;
    private String sigla;
    private String nome;
    private Date dataBase;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    private String descricao;

    public ConvencaoColetivaModel(String sigla, String nome, Date dataBase) {
        this.sigla = sigla;
        this.nome = nome;
        this.dataBase = dataBase;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataBase() {
        return dataBase;
    }

    public void setDataBase(Date dataBase) {
        this.dataBase = dataBase;
    }

    public String getLoginAtualizacao() {
        return loginAtualizacao;
    }

    public void setLoginAtualizacao(String loginAtualizacao) {
        this.loginAtualizacao = loginAtualizacao;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "ConvencaoColetivaModel{" +
                "codigo=" + codigo +
                ", sigla='" + sigla + '\'' +
                ", nome='" + nome + '\'' +
                ", dataBase=" + dataBase +
                ", loginAtualizacao='" + loginAtualizacao + '\'' +
                ", dataAtualizacao=" + dataAtualizacao +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
