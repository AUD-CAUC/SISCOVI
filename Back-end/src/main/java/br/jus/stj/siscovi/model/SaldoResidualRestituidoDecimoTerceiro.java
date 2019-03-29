package br.jus.stj.siscovi.model;

public class SaldoResidualRestituidoDecimoTerceiro {

    private int codigoTerceirizadoContrato;
    private String terceirizado;
    private String cpf;
    private float valorDecimoTerceiroResidual;
    private float valorIncidenciaDecimoTerceiroResidual;
    private float valorTotalResidual;
    private String restituidoFlag;

    public SaldoResidualRestituidoDecimoTerceiro (int codigoTerceirizadoContrato,
                                                  String terceirizado,
                                                  String cpf,
                                                  float valorDecimoTerceiroResidual,
                                                  float valorIncidenciaDecimoTerceiroResidual,
                                                  float valorTotalResidual,
                                                  String restituidoFlag) {
        this.codigoTerceirizadoContrato = codigoTerceirizadoContrato;
        this.terceirizado = terceirizado;
        this.cpf = cpf;
        this.valorDecimoTerceiroResidual = valorDecimoTerceiroResidual;
        this.valorIncidenciaDecimoTerceiroResidual = valorIncidenciaDecimoTerceiroResidual;
        this.valorTotalResidual = valorTotalResidual;
        this.restituidoFlag = restituidoFlag;

    }

}
