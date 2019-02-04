package br.jus.stj.siscovi.model;

public class ValorRestituicaoDecimoTerceiroModel {

    private final float valorDecimoTerceiro;
    private final float valorIncidenciaDecimoTerceiro;

    public ValorRestituicaoDecimoTerceiroModel (float valorDecimoTerceiro, float valorIncidenciaDecimoTerceiro) {

        // this.valorDecimoTerceiro = Math.round(valorDecimoTerceiro * 100f)/(100f);
        // this.valorIncidenciaDecimoTerceiro = Math.round(valorIncidenciaDecimoTerceiro * 100f)/(100f);
        this.valorDecimoTerceiro = valorDecimoTerceiro;
        this.valorIncidenciaDecimoTerceiro = valorIncidenciaDecimoTerceiro;

    }

    public float getValorDecimoTerceiro () {

        return valorDecimoTerceiro;

    }

    public float getValorIncidenciaDecimoTerceiro () {

        return valorIncidenciaDecimoTerceiro;

    }

}
