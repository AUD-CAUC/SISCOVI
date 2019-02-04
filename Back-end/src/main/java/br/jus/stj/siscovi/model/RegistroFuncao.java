package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroFuncao {

    private int pCod;
    private String pNome;
    private String pDescricao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroFuncao(int pCod,
                          String pNome,
                          String pDescricao,
                          String pLoginAtualizacao,
                          Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pNome = pNome;
        this.pDescricao = pDescricao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpNome() { return pNome; }

    public String getpDescricao() { return pDescricao; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
