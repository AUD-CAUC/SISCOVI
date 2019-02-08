package br.jus.stj.siscovi.model;

import java.sql.Date;

public class CalcularRescisaoModel {

    private int codTerceirizadoContrato;
    private String tipoRestituicao;
    private String tipoRescisao;
    private Date dataDesligamento;
    private Date inicioFeriasIntegrais;
    private Date fimFeriasIntegrais;
    private Date inicioFeriasProporcionais;
    private Date fimFeriasProporcionais;
    private Date inicioContagemDecTer;
    private float valorFeriasVencidasMovimentado;
    private float valorFeriasProporcionaisMovimentado;
    private float valorDecimoTerceiroMovimentado;
    private float totalDecimoTerceiro;
    private float totalIncidenciaDecimoTerceiro;
    private float totalMultaFgtsDecimoTerceiro;
    private float totalFeriasVencidas;
    private float totalTercoConstitucionalvencido;
    private float totalIncidenciaFeriasVencidas;
    private float totalIncidenciaTercoVencido;
    private float totalMultaFgtsFeriasVencidas;
    private float totalMultaFgtsTercoVencido;
    private float totalFeriasProporcionais;
    private float totalTercoProporcional;
    private float totalIncidenciaFeriasProporcionais;
    private float totalIncidenciaTercoProporcional;
    private float totalMultaFgtsFeriasProporcionais;
    private float totalMultaFgtsTercoProporcional;
    private float totalMultaFgtsSalario;
    private String username;

    public CalcularRescisaoModel(int codTerceirizadoContrato,
                                 String tipoRestituicao,
                                 String tipoRescisao,
                                 Date dataDesligamento,
                                 Date inicioFeriasIntegrais,
                                 Date fimFeriasIntegrais,
                                 Date inicioFeriasProporcionais,
                                 Date fimFeriasProporcionais,
                                 Date inicioContagemDecTer,
                                 float valorFeriasVencidasMovimentado,
                                 float valorFeriasProporcionaisMovimentado,
                                 float valorDecimoTerceiroMovimentado,
                                 float totalDecimoTerceiro,
                                 float totalIncidenciaDecimoTerceiro,
                                 float totalMultaFgtsDecimoTerceiro,
                                 float totalFeriasVencidas,
                                 float totalTercoConstitucionalvencido,
                                 float totalIncidenciaFeriasVencidas,
                                 float totalIncidenciaTercoVencido,
                                 float totalMultaFgtsFeriasVencidas,
                                 float totalMultaFgtsTercoVencido,
                                 float totalFeriasProporcionais,
                                 float totalTercoProporcional,
                                 float totalIncidenciaFeriasProporcionais,
                                 float totalIncidenciaTercoProporcional,
                                 float totalMultaFgtsFeriasProporcionais,
                                 float totalMultaFgtsTercoProporcional,
                                 float totalMultaFgtsSalario) {

        this.codTerceirizadoContrato = codTerceirizadoContrato;
        this.tipoRestituicao = tipoRestituicao;
        this.tipoRescisao = tipoRescisao;
        this.dataDesligamento = dataDesligamento;
        this.inicioFeriasIntegrais = inicioFeriasIntegrais;
        this.fimFeriasIntegrais = fimFeriasIntegrais;
        this.inicioFeriasProporcionais = inicioFeriasProporcionais;
        this.fimFeriasProporcionais = fimFeriasProporcionais;
        this.inicioContagemDecTer = inicioContagemDecTer;
        this.valorFeriasVencidasMovimentado = valorFeriasVencidasMovimentado;
        this.valorFeriasProporcionaisMovimentado = valorFeriasProporcionaisMovimentado;
        this.valorDecimoTerceiroMovimentado = valorDecimoTerceiroMovimentado;
        this.totalDecimoTerceiro = totalDecimoTerceiro;
        this.totalIncidenciaDecimoTerceiro = totalIncidenciaDecimoTerceiro;
        this.totalMultaFgtsDecimoTerceiro = totalMultaFgtsDecimoTerceiro;
        this.totalFeriasVencidas = totalFeriasVencidas;
        this.totalTercoConstitucionalvencido = totalTercoConstitucionalvencido;
        this.totalIncidenciaFeriasVencidas = totalIncidenciaFeriasVencidas;
        this.totalIncidenciaTercoVencido = totalIncidenciaTercoVencido;
        this.totalMultaFgtsFeriasVencidas = totalMultaFgtsFeriasVencidas;
        this.totalMultaFgtsTercoVencido = totalMultaFgtsTercoVencido;
        this.totalFeriasProporcionais = totalFeriasProporcionais;
        this.totalTercoProporcional = totalTercoProporcional;
        this.totalIncidenciaFeriasProporcionais = totalIncidenciaFeriasProporcionais;
        this.totalIncidenciaTercoProporcional = totalIncidenciaTercoProporcional;
        this.totalMultaFgtsFeriasProporcionais = totalMultaFgtsFeriasProporcionais;
        this.totalMultaFgtsTercoProporcional = totalMultaFgtsTercoProporcional;
        this.totalMultaFgtsSalario = totalMultaFgtsSalario;

    }

    public int getCodTerceirizadoContrato() {
        return codTerceirizadoContrato;
    }

    public String getTipoRestituicao() {
        return tipoRestituicao;
    }

    public String getTipoRescisao() { return tipoRescisao; }

    public Date getDataDesligamento() {
        return dataDesligamento;
    }

    public Date getInicioFeriasIntegrais() {
        return inicioFeriasIntegrais;
    }

    public Date getFimFeriasIntegrais() {
        return fimFeriasIntegrais;
    }

    public Date getInicioFeriasProporcionais() {
        return inicioFeriasProporcionais;
    }

    public Date getFimFeriasProporcionais() {
        return fimFeriasProporcionais;
    }

    public Date getInicioContagemDecTer() {
        return inicioContagemDecTer;
    }

    public float getValorFeriasVencidasMovimentado() {
        return valorFeriasVencidasMovimentado;
    }

    public float getValorFeriasProporcionaisMovimentado() {
        return valorFeriasProporcionaisMovimentado;
    }

    public float getValorDecimoTerceiroMovimentado() {
        return valorDecimoTerceiroMovimentado;
    }

    public float getTotalDecimoTerceiro() {
        return totalDecimoTerceiro;
    }

    public float getTotalIncidenciaDecimoTerceiro() {
        return totalIncidenciaDecimoTerceiro;
    }

    public float getTotalMultaFgtsDecimoTerceiro() {
        return totalMultaFgtsDecimoTerceiro;
    }

    public float getTotalFeriasVencidas() {
        return totalFeriasVencidas;
    }

    public float getTotalTercoConstitucionalvencido() {
        return totalTercoConstitucionalvencido;
    }

    public float getTotalIncidenciaFeriasVencidas() {
        return totalIncidenciaFeriasVencidas;
    }

    public float getTotalIncidenciaTercoVencido() {
        return totalIncidenciaTercoVencido;
    }

    public float getTotalMultaFgtsFeriasVencidas() {
        return totalMultaFgtsFeriasVencidas;
    }

    public float getTotalMultaFgtsTercoVencido() {
        return totalMultaFgtsTercoVencido;
    }

    public float getTotalFeriasProporcionais() {
        return totalFeriasProporcionais;
    }

    public float getTotalTercoProporcional() {
        return totalTercoProporcional;
    }

    public float getTotalIncidenciaFeriasProporcionais() {
        return totalIncidenciaFeriasProporcionais;
    }

    public float getTotalIncidenciaTercoProporcional() {
        return totalIncidenciaTercoProporcional;
    }

    public float getTotalMultaFgtsFeriasProporcionais() {
        return totalMultaFgtsFeriasProporcionais;
    }

    public float getTotalMultaFgtsTercoProporcional() {
        return totalMultaFgtsTercoProporcional;
    }

    public float getTotalMultaFgtsSalario() {
        return totalMultaFgtsSalario;
    }

    public void setTipoRestituicao(String tipoRestituicao) {
        this.tipoRestituicao = tipoRestituicao;
    }

    public void setTipoRescisao(String tipoRescisao) { this.tipoRescisao = tipoRestituicao; }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "CalcularFeriasModel{" +
                "codTerceirizadoContrato=" + codTerceirizadoContrato +
                ", tipoRestituicao='" + tipoRestituicao + '\'' +
                ", tipoRescisao=" + tipoRescisao +
                ", inicioFeriasVencidas=" + inicioFeriasIntegrais +
                ", fimFeriavencidass=" + fimFeriasIntegrais +
                ", inicioFeriasProporcionais=" + inicioFeriasProporcionais +
                ", fimFeriasProporcionais=" + fimFeriasProporcionais +
                '}';
    }

}
