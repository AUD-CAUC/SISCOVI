package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroTrienioTercContrato {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pNumeroDeTrienios;
    private Date pDataInicio;
    private Date pDataFim;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTrienioTercContrato (int pCod,
                                        int pCodTerceirizadoContrato,
                                        int pNumeroDeTrienios,
                                        Date pDataInicio,
                                        Date pDataFim,
                                        String pLoginAtualizacao,
                                        Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pNumeroDeTrienios = pNumeroDeTrienios;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodTerceirizadoContrato() { return pCodTerceirizadoContrato; }

    public int getpNumeroDeTrienios() { return pNumeroDeTrienios; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
