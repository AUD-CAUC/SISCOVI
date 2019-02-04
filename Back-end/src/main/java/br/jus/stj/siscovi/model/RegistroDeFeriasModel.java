package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroDeFeriasModel {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pCodTipoRestituicao;
    private Date pDataInicioPeriodoAquisitivo;
    private Date pDataFimPeriodoAquisitivo;
    private Date pDataInicioUsufruto;
    private Date pDataFimUsufruto;
    private int pDiasVendidos;
    private float pValorFerias;
    private float pValorTercoConstitucional;
    private float pIncidenciaSubmod41Ferias;
    private float pIncidenciaSubmod41Terco;
    private int pParcela;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRestituido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizcao;

    public RegistroDeFeriasModel (int pCod,
                                  int pCodTerceirizadoContrato,
                                  int pCodTipoRestituicao,
                                  Date pDataInicioPeriodoAquisitivo,
                                  Date pDataFimPeriodoAquisitivo,
                                  Date pDataInicioUsufruto,
                                  Date pDataFimUsufruto,
                                  int pDiasVendidos,
                                  float pValorFerias,
                                  float pValorTercoConstitucional,
                                  float pIncidenciaSubmod41Ferias,
                                  float pIncidenciaSubmod41Terco,
                                  int pParcela,
                                  Date pDataReferencia,
                                  String pAutorizado,
                                  String pRestituido,
                                  String pObservacao,
                                  String pLoginAtualizacao,
                                  Timestamp pDataAtualizcao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pCodTipoRestituicao = pCodTipoRestituicao;
        this.pDataInicioPeriodoAquisitivo = pDataInicioPeriodoAquisitivo;
        this.pDataFimPeriodoAquisitivo = pDataFimPeriodoAquisitivo;
        this.pDataInicioUsufruto = pDataInicioUsufruto;
        this.pDataFimUsufruto = pDataFimUsufruto;
        this.pDiasVendidos = pDiasVendidos;
        this.pValorFerias = pValorFerias;
        this.pValorTercoConstitucional = pValorTercoConstitucional;
        this.pIncidenciaSubmod41Ferias = pIncidenciaSubmod41Ferias;
        this.pIncidenciaSubmod41Terco = pIncidenciaSubmod41Terco;
        this.pParcela = pParcela;
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizcao = pDataAtualizcao;

    }

    public int getpCod () { return pCod; }

    public int getpCodTerceirizadoContrato () { return pCodTerceirizadoContrato; }

    public int getpCodTipoRestituicao () { return pCodTipoRestituicao; }

    public Date getpDataInicioPeriodoAquisitivo () { return pDataInicioPeriodoAquisitivo; }

    public Date getpDataFimPeriodoAquisitivo () { return pDataFimPeriodoAquisitivo; }

    public Date getpDataInicioUsufruto () { return pDataInicioUsufruto; }

    public Date getpDataFimUsufruto () { return pDataFimUsufruto; }

    public int getpDiasVendidos () { return pDiasVendidos; }

    public float getpValorFerias () { return pValorFerias; }

    public float getpValorTercoConstitucional () { return pValorTercoConstitucional; }

    public float getpIncidenciaSubmod41Ferias () { return pIncidenciaSubmod41Ferias; }

    public float getpIncidenciaSubmod41Terco () { return pIncidenciaSubmod41Terco; }

    public int getpParcela () { return pParcela; }

    public Date getpDataReferencia () { return pDataReferencia; }

    public String getpAutorizado () { return pAutorizado; }

    public String getpRestituido () { return pRestituido; }

    public String getpObservacao () { return pObservacao; }

    public String getpLoginAtualizacao () { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizcao () { return pDataAtualizcao; }


}