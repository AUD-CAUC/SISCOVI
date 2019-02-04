package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroContrato {

    private int pCod;
    private String pNomeEmpresa;
    private String pCnpj;
    private String pNumeroContrato;
    private String pNumeroProcessoStj;
    private String pSeAtivo;
    private String pObjeto;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroContrato(int pCod,
                            String pNomeEmpresa,
                            String pCnpj,
                            String pNumeroContrato,
                            String pNumeroProcessoStj,
                            String pSeAtivo,
                            String pObjeto,
                            String pLoginAtualizacao,
                            Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pNomeEmpresa = pNomeEmpresa;
        this.pCnpj = pCnpj;
        this.pNumeroContrato = pNumeroContrato;
        this.pNumeroProcessoStj = pNumeroProcessoStj;
        this.pSeAtivo = pSeAtivo;
        this.pObjeto = pObjeto;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpCnpj() { return pCnpj; }

    public String getpNomeEmpresa() { return pNomeEmpresa; }

    public String getpNumeroContrato() { return pNumeroContrato; }

    public String getpNumeroProcessoStj() { return pNumeroProcessoStj; }

    public String getpSeAtivo() { return pSeAtivo; }

    public String getpObjeto() { return pObjeto; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
