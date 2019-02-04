package br.jus.stj.siscovi.model;

public class CodFuncaoContratoECodFuncaoTerceirizadoModel {

    private int codFuncaoContrato;

    private int cod;

    public CodFuncaoContratoECodFuncaoTerceirizadoModel(int cod, int codFuncaoContrato) {

        this.cod = cod;

        this.codFuncaoContrato = codFuncaoContrato;

    }

    public int getCod() {

        return cod;

    }

    public int getCodFuncaoContrato() {

        return codFuncaoContrato;

    }

}
