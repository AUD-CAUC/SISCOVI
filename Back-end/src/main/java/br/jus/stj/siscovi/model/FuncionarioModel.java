package br.jus.stj.siscovi.model;

import java.sql.Date;

public class FuncionarioModel {
    private int codigo;
    private String nome;
    private String cpf;
    private char ativo;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public FuncionarioModel(int codigo, String nome, String cpf, char ativo) {
        this.ativo = ativo;
        this.codigo = codigo;
        this.nome = nome;
        this.cpf = cpf;
    }
    public void setLoginAtualizacao(String loginAtualizacao) {
        this.loginAtualizacao = loginAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getNome() {
        return nome;
    }
}
