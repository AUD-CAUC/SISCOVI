package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroTerceirizado {

    private int pCod;
    private String pNome;
    private String pCpf;
    private String pAtivo;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTerceirizado(int pCod,
                                          String pNome,
                                          String pCpf,
                                          String pAtivo,
                                          String pLoginAtualizacao,
                                          Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pNome = pNome;
        this.pCpf = pCpf;
        this.pAtivo = pAtivo;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpNome() { return pNome; }

    public String getpCpf() { return pCpf; }

    public String getpAtivo() { return pAtivo; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
