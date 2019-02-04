package br.jus.stj.siscovi.model;

public class SaldoResidualRescisao {

    private String terceirizado;
    private String cpf;
    private float valorIncidenciaDecimoTerceiroResidual;
    private float valorMultaFgtsDecimoTerceiroResidual;
    private float valorIncidenciaFeriasResidual;
    private float valorIncidenciaTercoResidual;
    private float valorMultaFgtsFeriasResidual;
    private float valorMultaFgtsTercoResidual;
    private float valorMultaFgtsSalarioResidual;
    private float valorTotalResidual;
    private String restituidoFlag;

    public SaldoResidualRescisao (String terceirizado,
                                          String cpf,
                                          float valorIncidenciaDecimoTerceiroResidual,
                                          float valorMultaFgtsDecimoTerceiroResidual,
                                          float valorIncidenciaFeriasResidual,
                                          float valorIncidenciaTercoResidual,
                                          float valorMultaFgtsFeriasResidual,
                                          float valorMultaFgtsTercoResidual,
                                          float valorMultaFgtsSalarioResidual,
                                          float valorTotalResidual,
                                          String restituidoFlag) {

        this.terceirizado = terceirizado;
        this.cpf = cpf;
        this.valorIncidenciaDecimoTerceiroResidual = valorIncidenciaDecimoTerceiroResidual;
        this.valorMultaFgtsDecimoTerceiroResidual = valorMultaFgtsDecimoTerceiroResidual;
        this.valorIncidenciaFeriasResidual = valorIncidenciaFeriasResidual;
        this.valorIncidenciaTercoResidual = valorIncidenciaTercoResidual;
        this.valorMultaFgtsFeriasResidual = valorMultaFgtsFeriasResidual;
        this.valorMultaFgtsTercoResidual = valorMultaFgtsTercoResidual;
        this.valorMultaFgtsSalarioResidual = valorMultaFgtsSalarioResidual;
        this.valorTotalResidual = valorTotalResidual;
        this.restituidoFlag = restituidoFlag;

    }


}
