package br.jus.stj.siscovi.model;

import java.sql.Timestamp;
import java.util.Date;

public class RegistroHistRestituicaoFerias {

    private int pCod;
    private int pCodRestituicaoFerias;
    private int pCodTipoRestituicao;
    private Date pDataInicioPeriodoAquisitivo;
    private Date pDataFimPeriodoAquisitivo;
    private Date pDataInicioUsufruto;
    private Date pDataFimUsufruto;
    private int pDiasVendidos;
    private float pValorFerias;
    private float pValorTercoConstitucional;
    private float pIncidSubmod41Ferias;
    private float pIncidSubmod41Terco;
    private int pParcela;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRestituido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroHistRestituicaoFerias(int pCod,
                                         int pCodRestituicaoFerias,
                                         int pCodTipoRestituicao,
                                         Date pDataInicioPeriodoAquisitivo,
                                         Date pDataFimPeriodoAquisitivo,
                                         Date pDataInicioUsufruto,
                                         Date pDataFimUsufruto,
                                         int pDiasVendidos,
                                         float pValorFerias,
                                         float pValorTercoConstitucional,
                                         float pIncidSubmod41Ferias,
                                         float pIncidSubmod41Terco,
                                         int pParcela,
                                         Date pDataReferencia,
                                         String pAutorizado,
                                         String pRestituido,
                                         String pObservacao,
                                         String pLoginAtualizacao,
                                         Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRestituicaoFerias = pCodRestituicaoFerias;
        this.pCodTipoRestituicao = pCodTipoRestituicao;
        this.pDataInicioPeriodoAquisitivo = pDataInicioPeriodoAquisitivo;
        this.pDataFimPeriodoAquisitivo = pDataFimPeriodoAquisitivo;
        this.pDataInicioUsufruto = pDataInicioUsufruto;
        this.pDataFimUsufruto = pDataFimUsufruto;
        this.pDiasVendidos = pDiasVendidos;
        this.pValorFerias = pValorFerias;
        this.pValorTercoConstitucional = pValorTercoConstitucional;
        this.pIncidSubmod41Ferias = pIncidSubmod41Ferias;
        this.pIncidSubmod41Terco = pIncidSubmod41Terco;
        this.pParcela = pParcela;
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() {
        return pCod;
    }

    public int getpCodRestituicaoFerias() {
        return pCodRestituicaoFerias;
    }

    public int getpCodTipoRestituicao() {
        return pCodTipoRestituicao;
    }

    public Date getpDataInicioPeriodoAquisitivo() {
        return pDataInicioPeriodoAquisitivo;
    }

    public Date getpDataFimPeriodoAquisitivo() {
        return pDataFimPeriodoAquisitivo;
    }

    public Date getpDataInicioUsufruto() {
        return pDataInicioUsufruto;
    }

    public Date getpDataFimUsufruto() {
        return pDataFimUsufruto;
    }

    public int getpDiasVendidos() {
        return pDiasVendidos;
    }

    public float getpValorFerias() {
        return pValorFerias;
    }

    public float getpValorTercoConstitucional() {
        return pValorTercoConstitucional;
    }

    public float getpIncidSubmod41Ferias() {
        return pIncidSubmod41Ferias;
    }

    public float getpIncidSubmod41Terco() {
        return pIncidSubmod41Terco;
    }

    public int getpParcela() {
        return pParcela;
    }

    public Date getpDataReferencia() {
        return pDataReferencia;
    }

    public String getpAutorizado() {
        return pAutorizado;
    }

    public String getpRestituido() {
        return pRestituido;
    }

    public String getpObservacao() { return pObservacao; }

    public String getpLoginAtualizacao() {
        return pLoginAtualizacao;
    }

    public Timestamp getpDataAtualizacao() {
        return pDataAtualizacao;
    }

}
