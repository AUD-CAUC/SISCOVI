package br.jus.stj.siscovi.model;

public class SaldoTotalContaVinculadaModel {

    private String funcao;
    //private String gestor;
    //private String empresa;
    //private String numeroContrato;
    private float valorFeriasRetido;
    private float valorTercoRetido;
    private float valorDecimoTerceiroRetido;
    private float valorIncidenciaRetido;
    private float valorMultaFGTSRetido;
    private float valorTotalRetido;
    private float valorFeriasRestituido;
    private float valorIncidenciaFeriasRestituido;
    private float valorTercoRestituido;
    private float valorIncidenciaTercoRestituido;
    private float valorTotalFeriasRestituido;
    private float valorDecimoTerceiroRestituido;
    private float valorIncidenciaDecimoTerceiroRestituido;
    private float valorTotalDecimoTerceiroRestituido;
    //private float valorMultaFGTSRestituido;
    private float valorTotalRestituido;
    private float valorSaldo;

    public SaldoTotalContaVinculadaModel(String funcao,
                                         //String gestor,
                                         //String empresa,
                                         //String numeroContrato,
                                         float valorFeriasRetido,
                                         float valorTercoRetido,
                                         float valorDecimoTerceiroRetido,
                                         float valorIncidenciaRetido,
                                         float valorMultaFGTSRetido,
                                         float valorTotalRetido,
                                         float valorFeriasRestituido,
                                         float valorTercoRestituido,
                                         float valorDecimoTerceiroRestituido,
                                         float valorTotalRestituido,
                                         float valorIncidenciaFeriasRestituido,
                                         float valorIncidenciaTercoRestituido,
                                         float valorTotalFeriasRestituido,
                                         float valorIncidenciaDecimoTerceiroRestituido,
                                         float valorTotalDecimoTerceiroRestituido,
                                         float valorSaldo) {

        this.funcao = funcao;
        //this.gestor = gestor;
        //this.empresa = empresa;
        //this.numeroContrato = numeroContrato;
        this.valorFeriasRetido = valorFeriasRetido;
        this.valorTercoRetido = valorTercoRetido;
        this.valorDecimoTerceiroRetido = valorDecimoTerceiroRetido;
        this.valorIncidenciaRetido = valorIncidenciaRetido;
        this.valorMultaFGTSRetido = valorMultaFGTSRetido;
        this.valorTotalRetido = valorTotalRetido;
        this.valorFeriasRestituido = valorFeriasRestituido;
        this.valorTercoRestituido = valorTercoRestituido;
        this.valorDecimoTerceiroRestituido = valorDecimoTerceiroRestituido;
        this.valorTotalRestituido = valorTotalRestituido;
        this.valorIncidenciaFeriasRestituido = valorIncidenciaFeriasRestituido;
        this.valorIncidenciaTercoRestituido = valorIncidenciaTercoRestituido;
        this.valorTotalFeriasRestituido = valorTotalFeriasRestituido;
        this.valorIncidenciaDecimoTerceiroRestituido = valorIncidenciaDecimoTerceiroRestituido;
        this.valorTotalDecimoTerceiroRestituido = valorTotalDecimoTerceiroRestituido;
        this.valorSaldo = valorSaldo;

    }

    public String getFuncao() { return funcao; }

    //public String getGestor() { return gestor; }

    //public String getEmpresa() { return empresa; }

    //public String getNumeroContrato() { return numeroContrato; }

    public float getValorFeriasRetido() { return valorFeriasRetido; }

    public float getValorTercoRetido() { return valorTercoRetido; }

    public float getValorDecimoTerceiroRetido() { return valorDecimoTerceiroRetido; }

    public float getValorIncidenciaRetido() { return valorIncidenciaRetido; }

    public float getValorMultaFGTSRetido () { return valorMultaFGTSRetido; }

    public float getValorTotalRetido() { return valorTotalRetido; }

    public float getValorFeriasRestituido() { return valorFeriasRestituido; }

    public float getValorTercoRestituido() { return valorTercoRestituido; }

    public float getValorDecimoTerceiroRestituido() { return valorDecimoTerceiroRestituido; }

    public float getValorTotalRestituido() { return valorTotalRestituido; }

    public float getValorIncidenciaFeriasRestituido() { return valorIncidenciaFeriasRestituido; }

    public float getValorIncidenciaTercoRestituido() { return valorIncidenciaTercoRestituido; }

    public float getValorTotalFeriasRestituido() { return valorTotalFeriasRestituido; }

    public float getValorIncidenciaDecimoTerceiroRestituido() { return valorIncidenciaDecimoTerceiroRestituido; }

    public float getValorTotalDecimoTerceiroRestituido() { return valorTotalDecimoTerceiroRestituido; }

    public float getValorSaldo() { return valorSaldo; }

}