package br.jus.stj.siscovi.model;

public class SaldoIndividualContaVinculadaModel {

    private String nomeFuncionario;
    private String Cpf;
    private float feriasRetido;
    private float tercoRetido;
    private float decimoTerceiroRetido;
    private float incidenciaRetido;
    private float multaFgtsRetido;
    private float feriasRestituido;
    private float incidenciaFeriasRestituido;
    private float tercoRestituido;
    private float incidenciaTercoRestituido;
    private float totalFeriasRestituido;
    private float decimoTerceiroRestituido;
    private float incidenciaDecimoTerceiroRestituido;
    private float totalDecimoTerceiroRestituido;
    private float totalRetido;
    private float totalRestituido;
    private float saldo;

    public SaldoIndividualContaVinculadaModel (String nomeFuncionario,
                                               String Cpf,
                                               float feriasRetido,
                                               float tercoRetido,
                                               float decimoTerceiroRetido,
                                               float incidenciaRetido,
                                               float multaFgtsRetido,
                                               float feriasRestituido,
                                               float tercoRestituido,
                                               float incidenciaFeriasRestituido,
                                               float incidenciaTercoRestituido,
                                               float totalFeriasRestituido,
                                               float decimoTerceiroRestituido,
                                               float incidenciaDecimoTerceiroRestituido,
                                               float totalDecimoTerceiroRestituido,
                                               float totalRetido,
                                               float totalRestituido,
                                               float saldo) {

        this.nomeFuncionario = nomeFuncionario;
        this.Cpf = Cpf;
        this.feriasRetido = feriasRetido;
        this.tercoRetido = tercoRetido;
        this.decimoTerceiroRetido = decimoTerceiroRetido;
        this.incidenciaRetido = incidenciaRetido;
        this.multaFgtsRetido = multaFgtsRetido;
        this.feriasRestituido = feriasRestituido;
        this.tercoRestituido = tercoRestituido;
        this.incidenciaFeriasRestituido = incidenciaFeriasRestituido;
        this.incidenciaTercoRestituido = incidenciaTercoRestituido;
        this.totalFeriasRestituido = totalFeriasRestituido;
        this.decimoTerceiroRestituido = decimoTerceiroRestituido;
        this.incidenciaDecimoTerceiroRestituido = incidenciaDecimoTerceiroRestituido;
        this.totalDecimoTerceiroRestituido = totalDecimoTerceiroRestituido;
        this.totalRetido = totalRetido;
        this.totalRestituido = totalRestituido;
        this.saldo = saldo;

    }


}