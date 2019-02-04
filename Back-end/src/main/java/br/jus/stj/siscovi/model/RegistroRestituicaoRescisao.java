package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroRestituicaoRescisao {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pCodTipoRestituicao;
    private int pCodTipoRescisao;
    private Date pDataDesligamento;
    private Date pDataInicioFerias;
    private float pValorDecimoTerceiro;
    private float pIncidSubmod41DecTerceiro;
    private float pIncidMultaFGTSDecTeceriro;
    private float pValorFerias;
    private float pValorTerco;
    private float pIncidSubmod41Ferias;
    private float pIncidSubmod41Terco;
    private float pIncidMultaFGTSFerias;
    private float pIncidMultaFGTSTerco;
    private float pMultaFGTSSalario;
    private float valorFeriasProporcional;
    private float valorTercoProporcional;
    private float valorIncidenciaFeriasProporcional;
    private float valorIncidenciaTercoProporcional;
    private float valorFGTSFeriasProporcional;
    private float valorFGTSTercoProporcional;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRestituido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroRestituicaoRescisao(int pCod,
                                       int pCodTerceirizadoContrato,
                                       int pCodTipoRestituicao,
                                       int pCodTipoRescisao,
                                       Date pDataDesligamento,
                                       Date pDataInicioFerias,
                                       float pValorDecimoTerceiro,
                                       float pIncidSubmod41DecTerceiro,
                                       float pIncidMultaFGTSDecTeceriro,
                                       float pValorFerias,
                                       float pValorTerco,
                                       float pIncidSubmod41Ferias,
                                       float pIncidSubmod41Terco,
                                       float pIncidMultaFGTSFerias,
                                       float pIncidMultaFGTSTerco,
                                       float valorFeriasProporcional,
                                       float valorTercoProporcional,
                                       float valorIncidenciaFeriasProporcional,
                                       float valorIncidenciaTercoProporcional,
                                       float valorFGTSFeriasProporcional,
                                       float valorFGTSTercoProporcional,
                                       float pMultaFGTSSalario,
                                       Date pDataReferencia,
                                       String pAutorizado,
                                       String pRestituido,
                                       String pObservacao,
                                       String pLoginAtualizacao,
                                       Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pCodTipoRestituicao = pCodTipoRestituicao;
        this.pCodTipoRescisao = pCodTipoRescisao;
        this.pDataDesligamento = pDataDesligamento;
        this.pDataInicioFerias = pDataInicioFerias;
        this.pValorDecimoTerceiro = pValorDecimoTerceiro;
        this.pIncidSubmod41DecTerceiro = pIncidSubmod41DecTerceiro;
        this.pIncidMultaFGTSDecTeceriro = pIncidMultaFGTSDecTeceriro;
        this.pValorFerias = pValorFerias;
        this.pValorTerco = pValorTerco;
        this.pIncidSubmod41Ferias = pIncidSubmod41Ferias;
        this.pIncidSubmod41Terco = pIncidSubmod41Terco;
        this.pIncidMultaFGTSFerias = pIncidMultaFGTSFerias;
        this.pIncidMultaFGTSTerco = pIncidMultaFGTSTerco;
        this.valorFeriasProporcional = valorFeriasProporcional;
        this.valorTercoProporcional = valorTercoProporcional;
        this.valorIncidenciaFeriasProporcional = valorIncidenciaFeriasProporcional;
        this.valorIncidenciaTercoProporcional = valorIncidenciaTercoProporcional;
        this.valorFGTSFeriasProporcional = valorFGTSFeriasProporcional;
        this.valorFGTSTercoProporcional = valorFGTSTercoProporcional;
        this.pMultaFGTSSalario = pMultaFGTSSalario;
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod () {
        return pCod;
    }

    public int getpCodTerceirizadoContrato() {
        return pCodTerceirizadoContrato;
    }

    public int getpCodTipoRestituicao() {
        return pCodTipoRestituicao;
    }

    public int getpCodTipoRescisao() {
        return pCodTipoRescisao;
    }

    public Date getpDataDesligamento() {
        return pDataDesligamento;
    }

    public Date getpDataInicioFerias() {
        return pDataInicioFerias;
    }

    public float getpValorDecimoTerceiro() {
        return pValorDecimoTerceiro;
    }

    public float getpIncidSubmod41DecTerceiro() {
        return pIncidSubmod41DecTerceiro;
    }

    public float getpIncidMultaFGTSDecTeceriro() {
        return pIncidMultaFGTSDecTeceriro;
    }

    public float getpValorFerias() {
        return pValorFerias;
    }

    public float getpValorTerco() {
        return pValorTerco;
    }

    public float getpIncidSubmod41Ferias() {
        return pIncidSubmod41Ferias;
    }

    public float getpIncidSubmod41Terco() {
        return pIncidSubmod41Terco;
    }

    public float getpIncidMultaFGTSFerias() {
        return pIncidMultaFGTSFerias;
    }

    public float getpIncidMultaFGTSTerco() {
        return pIncidMultaFGTSTerco;
    }

    public float getpMultaFGTSSalario() {
        return pMultaFGTSSalario;
    }

    public Date getpDataReferencia() { return pDataReferencia; }

    public String getpAutorizado() {
        return pAutorizado;
    }

    public String getpRestituido() { return pRestituido; }

    public String getpObservacao() {
        return pObservacao;
    }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() {
        return pDataAtualizacao;
    }

    public float getValorFeriasProporcional () { return valorFeriasProporcional; }

    public float getValorTercoProporcional () { return valorTercoProporcional; }

    public float getValorIncidenciaFeriasProporcional () { return valorIncidenciaFeriasProporcional; }

    public float getValorIncidenciaTercoProporcional () { return valorIncidenciaTercoProporcional; }

    public float getValorFGTSFeriasProporcional () { return valorFGTSFeriasProporcional; }

    public float getValorFGTSTercoProporcional () { return valorFGTSTercoProporcional; }

}

