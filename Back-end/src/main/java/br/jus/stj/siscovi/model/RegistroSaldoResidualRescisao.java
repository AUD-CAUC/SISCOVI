package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroSaldoResidualRescisao {

    private int pCod;
    private int pCodRestituicaoRescisao;
    private float pValorDecimoTerceiro;
    private float pIncidSubmod41DecTerceiro;
    private float pIncidMultaFgtsDecTerceiro;
    private float pValorFerias;
    private float pValorTerco;
    private float pIncidSubmod41Ferias;
    private float pIncidSubmod41Terco;
    private float pIncidMultaFgtsFerias;
    private float pIncidMultaFgtsTerco;
    private final float valorFeriasProporcional;
    private final float valorTercoProporcional;
    private final float valorIncidenciaFeriasProporcional;
    private final float valorIncidenciaTercoProporcional;
    private final float valorFGTSFeriasProporcional;
    private final float valorFGTSTercoProporcional;

    private float pMultaFgtsSalario;
    private String pAutorizado;
    private String pRestituido;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroSaldoResidualRescisao (int pCod,
                                          int pCodRestituicaoRescisao,
                                          float pValorDecimoTerceiro,
                                          float pIncidSubmod41DecTerceiro,
                                          float pIncidMultaFgtsDecTerceiro,
                                          float pValorFerias,
                                          float pValorTerco,
                                          float pIncidSubmod41Ferias,
                                          float pIncidSubmod41Terco,
                                          float pIncidMultaFgtsFerias,
                                          float pIncidMultaFgtsTerco,
                                          float valorFeriasProporcional,
                                          float valorTercoProporcional,
                                          float valorIncidenciaFeriasProporcional,
                                          float valorIncidenciaTercoProporcional,
                                          float valorFGTSFeriasProporcional,
                                          float valorFGTSTercoProporcional,
                                          float pMultaFgtsSalario,
                                          String pAutorizado,
                                          String pRestituido,
                                          String pLoginAtualizacao,
                                          Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRestituicaoRescisao = pCodRestituicaoRescisao;
        this.pValorDecimoTerceiro = pValorDecimoTerceiro;
        this.pIncidSubmod41DecTerceiro = pIncidSubmod41DecTerceiro;
        this.pIncidMultaFgtsDecTerceiro = pIncidMultaFgtsDecTerceiro;
        this.pValorFerias = pValorFerias;
        this.pValorTerco = pValorTerco;
        this.pIncidSubmod41Ferias = pIncidSubmod41Ferias;
        this.pIncidSubmod41Terco = pIncidSubmod41Terco;
        this.pIncidMultaFgtsFerias = pIncidMultaFgtsFerias;
        this.pIncidMultaFgtsTerco = pIncidMultaFgtsTerco;
        this.valorFeriasProporcional = valorFeriasProporcional;
        this.valorTercoProporcional = valorTercoProporcional;
        this.valorIncidenciaFeriasProporcional = valorIncidenciaFeriasProporcional;
        this.valorIncidenciaTercoProporcional = valorIncidenciaTercoProporcional;
        this.valorFGTSFeriasProporcional = valorFGTSFeriasProporcional;
        this.valorFGTSTercoProporcional = valorFGTSTercoProporcional;
        this.pMultaFgtsSalario = pMultaFgtsSalario;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodRestituicaoRescisao() { return pCodRestituicaoRescisao; }

    public float getpValorDecimoTerceiro() { return pValorDecimoTerceiro; }

    public float getpIncidSubmod41DecTerceiro() { return pIncidSubmod41DecTerceiro; }

    public float getpIncidMultaFgtsDecTerceiro() { return pIncidMultaFgtsDecTerceiro; }

    public float getpValorFerias() { return pValorFerias; }

    public float getpValorTerco() { return pValorTerco; }

    public float getpIncidSubmod41Ferias() { return pIncidSubmod41Ferias; }

    public float getpIncidSubmod41Terco() { return pIncidSubmod41Terco; }

    public float getpIncidMultaFgtsFerias() { return pIncidMultaFgtsFerias; }

    public float getpIncidMultaFgtsTerco() { return pIncidMultaFgtsTerco; }

    public float getpMultaFgtsSalario() { return pMultaFgtsSalario; }

    public String getpAutorizado() { return pAutorizado; }

    public String getpRestituido() { return pRestituido; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

    public float getValorFeriasProporcional () { return valorFeriasProporcional; }

    public float getValorTercoProporcional () { return valorTercoProporcional; }

    public float getValorIncidenciaFeriasProporcional () { return valorIncidenciaFeriasProporcional; }

    public float getValorIncidenciaTercoProporcional () { return valorIncidenciaTercoProporcional; }

    public float getValorFGTSFeriasProporcional () { return valorFGTSFeriasProporcional; }

    public float getValorFGTSTercoProporcional () { return valorFGTSTercoProporcional; }


}
