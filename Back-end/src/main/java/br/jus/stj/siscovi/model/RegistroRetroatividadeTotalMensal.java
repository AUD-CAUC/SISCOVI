package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroRetroatividadeTotalMensal {

    private int pCod;
    private int pCodTotalMensalAReter;
    private float pFerias;
    private float pTercoConstitucional;
    private float pDecimoTerceiro;
    private float pIncidenciaSubmodulo41;
    private float pMultaFgts;
    private float pTotal;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroRetroatividadeTotalMensal (int pCod,
                                              int pCodTotalMensalAReter,
                                              float pFerias,
                                              float pTercoConstitucional,
                                              float pDecimoTerceiro,
                                              float pIncidenciaSubmodulo41,
                                              float pMultaFgts,
                                              float pTotal,
                                              String pLoginAtualizacao,
                                              Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTotalMensalAReter = pCodTotalMensalAReter;
        this.pFerias = pFerias;
        this.pTercoConstitucional = pTercoConstitucional;
        this.pTercoConstitucional = pTercoConstitucional;
        this.pDecimoTerceiro = pDecimoTerceiro;
        this.pIncidenciaSubmodulo41 = pIncidenciaSubmodulo41;
        this.pMultaFgts = pMultaFgts;
        this.pTotal = pTotal;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodTotalMensalAReter() { return pCodTotalMensalAReter; }

    public float getpFerias() { return pFerias; }

    public float getpTercoConstitucional() { return pTercoConstitucional; }

    public float getpDecimoTerceiro() { return pDecimoTerceiro; }

    public float getpIncidenciaSubmodulo41() { return pIncidenciaSubmodulo41; }

    public float getpMultaFgts() { return pMultaFgts; }

    public float getpTotal() { return pTotal; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
