package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroConvencaoColetiva {

    private int pCod;
    private String pNome;
    private String pSigla;
    private Date pDataBase;
    private String pDescricao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroConvencaoColetiva(int pCod,
                                     String pNome,
                                     String pSigla,
                                     String pDescricao,
                                     Date pDataBase,
                                     String pLoginAtualizacao,
                                     Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pNome = pNome;
        this.pSigla = pSigla;
        this.pDataBase = pDataBase;
        this.pDescricao = pDescricao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpNome() { return pNome; }

    public String getpSigla() { return pSigla; }

    public Date getpDataBase() { return pDataBase; }

    public String getpDescricao() { return pDescricao; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
