package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TipoRestituicao {

    private int cod;
    private String nome;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public TipoRestituicao(int cod, String nome, String loginAtualizacao, Date dataAtualizacao) {
        this.cod = cod;
        this.nome = nome;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }
}