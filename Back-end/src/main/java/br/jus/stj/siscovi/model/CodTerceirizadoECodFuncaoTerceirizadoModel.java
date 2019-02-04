package br.jus.stj.siscovi.model;

public class CodTerceirizadoECodFuncaoTerceirizadoModel {

    private int codTerceirizadoContrato;
    private int cod;

    public CodTerceirizadoECodFuncaoTerceirizadoModel (int cod, int codTerceirizadoContrato) {

        this.cod = cod;
        this.codTerceirizadoContrato = codTerceirizadoContrato;

    }

    public int getCod () {

        return cod;

    }

    public int getCodTerceirizadoContrato () {

        return codTerceirizadoContrato;

    }

}
