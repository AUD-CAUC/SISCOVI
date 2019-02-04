package br.jus.stj.siscovi.model;

public class SaldoResidualRestituidoFerias {

    private String terceirizado;
    private String cpf;
    private float valorFeriasResidual;
    private float valorTercoResidual;
    private float valorIncidenciaFeriasResidual;
    private float valorIncidenciaTercoResidual;
    private float valorTotalResidual;
    private String restituidoFlag;

    public SaldoResidualRestituidoFerias (String terceirizado,
                                          String cpf,
                                          float valorFeriasResidual,
                                          float valorTercoResidual,
                                          float valorIncidenciaFeriasResidual,
                                          float valorIncidenciaTercoResidual,
                                          float valorTotalResidual,
                                          String restituidoFlag) {

        this.terceirizado = terceirizado;
        this.cpf = cpf;
        this.valorFeriasResidual = valorFeriasResidual;
        this.valorTercoResidual = valorTercoResidual;
        this.valorIncidenciaFeriasResidual = valorIncidenciaFeriasResidual;
        this.valorIncidenciaTercoResidual = valorIncidenciaTercoResidual;
        this.valorTotalResidual = valorTotalResidual;
        this.restituidoFlag = restituidoFlag;

    }

}
