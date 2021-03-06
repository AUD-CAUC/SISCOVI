package br.jus.stj.siscovi.model;

import java.sql.Date;

public class ValorRestituicaoFeriasModel {

    private final float valorFerias;
    private final float valorTercoConstitucional;
    private final float valorIncidenciaFerias;
    private final float valorIncidenciaTercoConstitucional;
    private final Date inicioPeriodoAquisitivo;
    private final Date fimPeriodoAquisitivo;

    public ValorRestituicaoFeriasModel (float valorFerias, float valorTercoConstitucional, float valorIncidenciaFerias,
                                        float valorIncidenciaTercoConstitucional,
                                        Date inicioPeriodoAquisitivo,
                                        Date fimPeriodoAquisitivo) {

        this.valorFerias = valorFerias;
        this.valorIncidenciaFerias = valorIncidenciaFerias;
        this.valorTercoConstitucional = valorTercoConstitucional;
        this.valorIncidenciaTercoConstitucional = valorIncidenciaTercoConstitucional;
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;

    }

    public float getValorFerias () {

        return valorFerias;

    }

    public float getValorTercoConstitucional () {

        return valorTercoConstitucional;

    }

    public float getValorIncidenciaFerias () {

        return valorIncidenciaFerias;

    }

    public float getValorIncidenciaTercoConstitucional () {

        return valorIncidenciaTercoConstitucional;

    }

    @Override
    public String toString() {
        return "ValorRestituicaoFeriasModel{" +
                "valorFerias=" + valorFerias +
                ", valorTercoConstitucional=" + valorTercoConstitucional +
                ", valorIncidenciaFerias=" + valorIncidenciaFerias +
                ", valorIncidenciaTercoConstitucional=" + valorIncidenciaTercoConstitucional +
                ", inicioPeriodoAquisitivo=" + inicioPeriodoAquisitivo +
                ", fimPeriodoAquisitivo=" + fimPeriodoAquisitivo +
                '}';
    }
}