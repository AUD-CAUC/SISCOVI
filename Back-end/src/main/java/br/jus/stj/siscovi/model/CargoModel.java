package br.jus.stj.siscovi.model;
import java.sql.Date;

public class CargoModel {
    private int codigo;
    private String nome;
    private String descricao;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public CargoModel(int codigo, String nome,String loginAtualizacao, Date dataAtualizacao) {
        this.codigo = codigo;
        this.nome = nome;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome () {
        return  nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getLoginAtualizacao() {
        return loginAtualizacao;
    }
    public Date getDataAtualizacao () {
        return dataAtualizacao;
    }
}
