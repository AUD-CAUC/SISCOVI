package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroTipoEventoContratual {

    private int pCod;
    private String pTipo;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTipoEventoContratual (int pCod,
                                         String pTipo,
                                         String pLoginAtualizacao,
                                         Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pTipo = pTipo;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpTipo() { return pTipo; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
