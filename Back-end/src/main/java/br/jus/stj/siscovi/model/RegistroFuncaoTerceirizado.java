package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroFuncaoTerceirizado {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pCodFuncaoContrato;
    private Date pDataInicio;
    private Date pDataFim;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroFuncaoTerceirizado(int pCod,
                                      int pCodTerceirizadoContrato,
                                      int pCodFuncaoContrato,
                                      Date pDataInicio,
                                      Date pDataFim,
                                      String pLoginAtualizacao,
                                      Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pCodFuncaoContrato = pCodFuncaoContrato;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodTerceirizadoContrato() { return pCodTerceirizadoContrato; }

    public int getpCodFuncaoContrato() { return pCodFuncaoContrato; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
