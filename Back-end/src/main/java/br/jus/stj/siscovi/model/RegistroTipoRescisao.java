package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroTipoRescisao {

    private int pCod;
    private String pTipoRescisao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTipoRescisao (int pCod,
                                 String pTipoRescisao,
                                 String pLoginAtualizacao,
                                 Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pTipoRescisao = pTipoRescisao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpTipoRescisao() { return pTipoRescisao; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
