package br.jus.stj.siscovi.model;

public class TotalMensal {

    private String funcao;
    private float ferias;
    private float tercoConstitucional;
    private float decimoTerceiro;
    private float incidencia;
    private float multaFGTS;
    private float total;
    private int numeroTerceirizados;

    public TotalMensal (float ferias, float tercoConstitucional, float decimoTerceiro, float incidencia, float multaFGTS, float total, String funcao, int numeroTerceirizados) {
        this.ferias = ferias;
        this.tercoConstitucional = tercoConstitucional;
        this.decimoTerceiro = decimoTerceiro;
        this.incidencia = incidencia;
        this.multaFGTS = multaFGTS;
        this.total = total;
        this.funcao = funcao;
        this.numeroTerceirizados = numeroTerceirizados;
    }

    public String getFuncao() {
        return funcao;
    }

    public float getFerias() {
        return ferias;
    }

    public float getTercoConstitucional() {
        return tercoConstitucional;
    }

    public float getDecimoTerceiro() {
        return decimoTerceiro;
    }

    public float getIncidencia() {
        return incidencia;
    }

    public float getMultaFGTS() {
        return multaFGTS;
    }

    public float getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "TotalMensal{" +
                "funcao='" + funcao + '\'' +
                ", ferias=" + ferias +
                ", tercoConstitucional=" + tercoConstitucional +
                ", decimoTerceiro=" + decimoTerceiro +
                ", incidencia=" + incidencia +
                ", multaFGTS=" + multaFGTS +
                ", total=" + total +
                '}';
    }
}
