package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroRetroatividadeRemuneracao {

    private int pCod;
    private int pCodRemFuncaoContrato;
    private Date pInicio;
    private Date pFim;
    private Date pDataCobranca;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroRetroatividadeRemuneracao (int pCod,
                                              int pCodRemFuncaoContrato,
                                              Date pInicio,
                                              Date pFim,
                                              Date pDataCobranca,
                                              String pLoginAtualizacao,
                                              Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRemFuncaoContrato = pCodRemFuncaoContrato;
        this.pInicio = pInicio;
        this.pFim = pFim;
        this.pDataCobranca = pDataCobranca;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodRemFuncaoContrato() { return pCodRemFuncaoContrato; }

    public Date getpInicio() { return pInicio; }

    public Date getpFim() { return pFim; }

    public Date getpDataCobranca() { return pDataCobranca; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }


}
