package br.jus.stj.siscovi.model;

import java.sql.Date;

public class ValorRestituicaoRescisaoModel {

    private final Date inicioContagemDecimoTerceiro;
    private final float valorDecimoTerceiro;
    private final float valorIncidenciaDecimoTerceiro;
    private final float valorFGTSDecimoTerceiro;
    private final float valorFeriasIntegral;
    private final float valorTercoIntegral;
    private final float valorIncidenciaFeriasIntegral;
    private final float valorIncidenciaTercoIntegral;
    private final float valorFGTSFeriasIntegral;
    private final float valorFGTSTercoIntegral;
    private final float valorFGTSSalario;
    private final float valorFGTSRestante;
    private final float valorFeriasProporcional;
    private final float valorTercoProporcional;
    private final float valorIncidenciaFeriasProporcional;
    private final float valorIncidenciaTercoProporcional;
    private final float valorFGTSFeriasProporcional;
    private final float valorFGTSTercoProporcional;

    public ValorRestituicaoRescisaoModel (Date incioContagemDecimoTerceiro,
                                          float valorDecimoTerceiro,
                                          float valorIncidenciaDecimoTerceiro,
                                          float valorFGTSDecimoTerceiro,
                                          float valorFeriasIntegral,
                                          float valorTercoIntegral,
                                          float valorIncidenciaFeriasIntegral,
                                          float valorIncidenciaTercoIntegral,
                                          float valorFGTSFeriasIntegral,
                                          float valorFGTSTercoIntegral,
                                          float valorFGTSRestante,
                                          float valorFeriasProporcional,
                                          float valorTercoProporcional,
                                          float valorIncidenciaFeriasProporcional,
                                          float valorIncidenciaTercoProporcional,
                                          float valorFGTSFeriasProporcional,
                                          float valorFGTSTercoProporcional,
                                          float valorFGTSSalario) {

        this.inicioContagemDecimoTerceiro = incioContagemDecimoTerceiro;
        this.valorDecimoTerceiro = valorDecimoTerceiro;
        this.valorIncidenciaDecimoTerceiro = valorIncidenciaDecimoTerceiro;
        this.valorFGTSDecimoTerceiro = valorFGTSDecimoTerceiro;
        this.valorFeriasIntegral = valorFeriasIntegral;
        this.valorTercoIntegral = valorTercoIntegral;
        this.valorIncidenciaFeriasIntegral = valorIncidenciaFeriasIntegral;
        this.valorIncidenciaTercoIntegral = valorIncidenciaTercoIntegral;
        this.valorFGTSFeriasIntegral = valorFGTSFeriasIntegral;
        this.valorFGTSTercoIntegral = valorFGTSTercoIntegral;
        this.valorFGTSSalario = valorFGTSSalario;
        this.valorFGTSRestante = valorFGTSRestante;
        this.valorFeriasProporcional = valorFeriasProporcional;
        this.valorTercoProporcional = valorTercoProporcional;
        this.valorIncidenciaFeriasProporcional = valorIncidenciaFeriasProporcional;
        this.valorIncidenciaTercoProporcional = valorIncidenciaTercoProporcional;
        this.valorFGTSFeriasProporcional = valorFGTSFeriasProporcional;
        this.valorFGTSTercoProporcional = valorFGTSTercoProporcional;

    }

    public Date getIncioContagemDecimoTerceiro () {

        return inicioContagemDecimoTerceiro;

    }

    public float getValorDecimoTerceiro () {

        return valorDecimoTerceiro;

    }

    public float getValorIncidenciaDecimoTerceiro () {

        return valorIncidenciaDecimoTerceiro;

    }

    public float getValorFGTSDecimoTerceiro () {

        return valorFGTSDecimoTerceiro;

    }

    public float getValorFeriasIntegral () {

        return valorFeriasIntegral;

    }

    public float getValorTercoIntegral () {

        return valorTercoIntegral;

    }

    public float getValorIncidenciaFeriasIntegral () {

        return valorIncidenciaFeriasIntegral;

    }

    public float getValorIncidenciaTercoIntegral () {

        return valorIncidenciaTercoIntegral;

    }

    public float getValorFGTSFeriasIntegral () {

        return valorFGTSFeriasIntegral;

    }

    public float getValorFGTSTercoIntegral () {

        return valorFGTSTercoIntegral;

    }

    public float getValorFGTSSalario () {

        return valorFGTSSalario;

    }

    public float getValorFeriasProporcional () {

        return valorFeriasProporcional;

    }

    public float getValorTercoProporcional () {

        return valorTercoProporcional;

    }

    public float getValorIncidenciaFeriasProporcional () {

        return valorIncidenciaFeriasProporcional;

    }

    public float getValorIncidenciaTercoProporcional () {

        return valorIncidenciaTercoProporcional;

    }

    public float getValorFGTSFeriasProporcional () {

        return valorFGTSFeriasProporcional;

    }

    public float getValorFGTSTercoProporcional () {

        return valorFGTSTercoProporcional;

    }

    public float getValorFGTSRestante () {

        return valorFGTSRestante;

    }


}
