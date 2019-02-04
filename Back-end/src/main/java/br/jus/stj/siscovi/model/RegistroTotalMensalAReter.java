package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroTotalMensalAReter {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pCodFuncaoTerceirizado;
    private float pFerias;
    private float pTercoConstitucional;
    private float pDecimoTerceiro;
    private float pIncidenciaSubmodulo41;
    private float pMultaFgts;
    private float pTotal;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRetido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTotalMensalAReter (int pCod,
                                      int pCodTerceirizadoContrato,
                                      int pCodFuncaoTerceirizado,
                                      float pFerias,
                                      float pTercoConstitucional,
                                      float pDecimoTerceiro,
                                      float pIncidenciaSubmodulo41,
                                      float pMultaFgts,
                                      float pTotal,
                                      Date pDataReferencia,
                                      String pAutorizado ,
                                      String pRetido,
                                      String pObservacao,
                                      String pLoginAtualizacao,
                                      Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pCodFuncaoTerceirizado = pCodFuncaoTerceirizado;
        this.pFerias = pFerias;
        this.pTercoConstitucional = pTercoConstitucional;
        this.pTercoConstitucional = pTercoConstitucional;
        this.pDecimoTerceiro = pDecimoTerceiro;
        this.pIncidenciaSubmodulo41 = pIncidenciaSubmodulo41;
        this.pMultaFgts = pMultaFgts;
        this.pTotal = pTotal;
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRetido = pRetido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodTerceirizadoContrato() { return pCodTerceirizadoContrato; }

    public int getpCodFuncaoTerceirizado() { return pCodFuncaoTerceirizado; }

    public float getpFerias() { return pFerias; }

    public float getpTercoConstitucional() { return pTercoConstitucional; }

    public float getpDecimoTerceiro() { return pDecimoTerceiro; }

    public float getpIncidenciaSubmodulo41() { return pIncidenciaSubmodulo41; }

    public float getpMultaFgts() { return pMultaFgts; }

    public float getpTotal() { return pTotal; }

    public Date getpDataReferencia() { return pDataReferencia; }

    public String getpAutorizado() { return pAutorizado; }

    public String getpRetido() { return pRetido; }

    public String getpObservacao() { return pObservacao; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
