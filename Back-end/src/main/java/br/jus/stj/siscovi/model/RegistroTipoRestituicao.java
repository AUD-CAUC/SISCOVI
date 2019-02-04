package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroTipoRestituicao {

    private int pCod;
    private String pNome;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTipoRestituicao (int pCod,
                                    String pNome,
                                    String pLoginAtualizacao,
                                    Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pNome = pNome;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpNome() { return pNome; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
