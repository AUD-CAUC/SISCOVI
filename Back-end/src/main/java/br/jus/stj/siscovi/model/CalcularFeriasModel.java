package br.jus.stj.siscovi.model;

import java.sql.Date;

public class CalcularFeriasModel {
    private int codTerceirizadoContrato;
    private String tipoRestituicao;
    private int diasVendidos;
    private Date inicioFerias;
    private Date fimFerias;
    private Date inicioPeriodoAquisitivo;
    private Date fimPeriodoAquisitivo;
    private float valorMovimentado;
    private int parcelas;
    private float pValorMovimentado;
    private float pTotalFerias;
    private float pTotalTercoConstitucional;
    private float pTotalIncidenciaFerias;
    private float pTotalIncidenciaTerco;
    private String username;

    public CalcularFeriasModel(int codTerceirizadoContrato,
                               String tipoRestituicao,
                               int diasVendidos,
                               Date inicioFerias,
                               Date fimFerias,
                               Date inicioPeriodoAquisitivo,
                               Date fimPeriodoAquisitivo,
                               float valorMovimentado,
                               int parcelas,
                               float pValorMovimentado,
                               float pTotalFerias,
                               float pTotalTercoConstitucional,
                               float pTotalIncidenciaFerias,
                               float pTotalIncidenciaTerco) {
        this.codTerceirizadoContrato = codTerceirizadoContrato;
        this.tipoRestituicao = tipoRestituicao;
        this.diasVendidos = diasVendidos;
        this.inicioFerias = inicioFerias;
        this.fimFerias = fimFerias;
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
        this.valorMovimentado = valorMovimentado;
        this.parcelas = parcelas;
        this.pValorMovimentado = valorMovimentado;
        this.pTotalFerias = pTotalFerias;
        this.pValorMovimentado = pValorMovimentado;
        this.pTotalTercoConstitucional = pTotalTercoConstitucional;
        this.pTotalIncidenciaFerias = pTotalIncidenciaFerias;
        this.pTotalIncidenciaTerco = pTotalIncidenciaTerco;
    }

    public int getCodTerceirizadoContrato() {
        return codTerceirizadoContrato;
    }

    public String getTipoRestituicao() {
        return tipoRestituicao;
    }

    public int getDiasVendidos() {
        return diasVendidos;
    }

    public Date getInicioFerias() {
        return inicioFerias;
    }

    public Date getFimFerias() {
        return fimFerias;
    }

    public Date getInicioPeriodoAquisitivo() {
        return inicioPeriodoAquisitivo;
    }

    public Date getFimPeriodoAquisitivo() {
        return fimPeriodoAquisitivo;
    }

    public float getValorMovimentado() {
        return valorMovimentado;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setTipoRestituicao(String tipoRestituicao) {
        this.tipoRestituicao = tipoRestituicao;
    }

    public float getpValorMovimentado() {
        return pValorMovimentado;
    }

    public float getpTotalFerias() {
        return pTotalFerias;
    }

    public float getpTotalTercoConstitucional() {
        return pTotalTercoConstitucional;
    }

    public float getpTotalIncidenciaFerias() {
        return pTotalIncidenciaFerias;
    }

    public float getpTotalIncidenciaTerco() {
        return pTotalIncidenciaTerco;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "CalcularFeriasModel{" +
                "codTerceirizadoContrato=" + codTerceirizadoContrato +
                ", tipoRestituicao='" + tipoRestituicao + '\'' +
                ", diasVendidos=" + diasVendidos +
                ", inicioFerias=" + inicioFerias +
                ", fimFerias=" + fimFerias +
                ", inicioPeriodoAquisitivo=" + inicioPeriodoAquisitivo +
                ", fimPeriodoAquisitivo=" + fimPeriodoAquisitivo +
                ", valorMovimentado=" + valorMovimentado +
                ", parcelas=" + parcelas +
                '}';
    }
}