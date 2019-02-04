package br.jus.stj.siscovi.model;

import java.sql.Timestamp;
import java.util.Date;

public class RegistroHistRestituicaoRescisao {

    private int pCod;
    private int pCodRestituicaoRescisao;
    private int pCodTipoRestituicao;
    private int pCodTipoRescisao;
    private Date pDataDesligamento;
    private Date pDataInicioFeriasIntegrais;
    private Date pDataFimFeriasIntegrais;
    private Date pDataInicioFeriasProporcionais;
    private Date pDataFimFeriasProporcionais;
    private Date pDataInicioContagemDecTer;
    private float pValorDecimoTerceiro;
    private float pIncidSubmod41DecTerceiro;
    private float pIncidMultaFgtsDecTerceiro;
    private float pValorFerias;
    private float pValorTerco;
    private float pIncidSubmod41Ferias;
    private float pIncidSubmod41Terco;
    private float pIncidMultaFgtsFerias;
    private float pIncidMultaFgtsTerco;
    private float valorFeriasProporcional;
    private float valorTercoProporcional;
    private float valorIncidenciaFeriasProporcional;
    private float valorIncidenciaTercoProporcional;
    private float valorFGTSFeriasProporcional;
    private float valorFGTSTercoProporcional;
    private float pMultaFgtsSalario;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRestituido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroHistRestituicaoRescisao(int pCod,
                                           int pCodRestituicaoRescisao,
                                           int pCodTipoRestituicao,
                                           int pCodTipoRescisao,
                                           Date pDataDesligamento,
                                           Date pDataInicioFeriasIntegrais,
                                           Date pDataFimFeriasIntegrais,
                                           Date pDataInicioFeriasProporcionais,
                                           Date pDataFimFeriasProporcionais,
                                           Date pDataInicioContagemDecTer,
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
                                           Date pDataReferencia,
                                           String pAutorizado,
                                           String pRestituido,
                                           String pObservacao,
                                           String pLoginAtualizacao,
                                           Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRestituicaoRescisao = pCodRestituicaoRescisao;
        this.pCodTipoRestituicao = pCodTipoRestituicao;
        this.pCodTipoRescisao = pCodTipoRescisao;
        this.pDataDesligamento = pDataDesligamento;
        this.pDataInicioFeriasIntegrais = pDataInicioFeriasIntegrais;
        this.pDataFimFeriasIntegrais = pDataFimFeriasIntegrais;
        this.pDataInicioFeriasProporcionais = pDataInicioFeriasProporcionais;
        this.pDataFimFeriasProporcionais = pDataFimFeriasProporcionais;
        this.pDataInicioContagemDecTer = pDataInicioContagemDecTer;
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
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() {
        return pCod;
    }

    public int getpCodRestituicaoRescisao() { return pCodRestituicaoRescisao; }

    public int getpCodTipoRestituicao() {
        return pCodTipoRestituicao;
    }

    public int getpCodTipoRescisao() { return pCodTipoRescisao;  }

    public Date getpDataDesligamento() { return pDataDesligamento; }

    public Date getpDataInicioFeriasIntegrais() { return pDataInicioFeriasIntegrais; }

    public Date getpDataFimFeriasIntegrais() { return pDataFimFeriasIntegrais; }

    public Date getpDataInicioFeriasProporcionais() { return pDataInicioFeriasProporcionais; }

    public Date getpDataFimFeriasProporcionais() { return pDataFimFeriasProporcionais; }

    public Date getpDataInicioContagemDecTer() { return pDataInicioContagemDecTer; }

    public float getpValorDecimoTerceiro() { return pValorDecimoTerceiro; }

    public float getpIncidSubmod41DecTerceiro() { return pIncidSubmod41DecTerceiro; }

    public float getpIncidMultaFgtsDecTerceiro() { return pIncidMultaFgtsDecTerceiro; }

    public float getpValorFerias() {
        return pValorFerias;
    }

    public float getpValorTerco() { return pValorTerco; }

    public float getpIncidSubmod41Ferias() {
        return pIncidSubmod41Ferias;
    }

    public float getpIncidSubmod41Terco() {
        return pIncidSubmod41Terco;
    }

    public float getpIncidMultaFgtsFerias() { return pIncidMultaFgtsFerias; }

    public float getpIncidMultaFgtsTerco() { return pIncidMultaFgtsTerco; }

    public float getpMultaFgtsSalario() { return pMultaFgtsSalario; }

    public Date getpDataReferencia() {
        return pDataReferencia;
    }

    public String getpAutorizado() {
        return pAutorizado;
    }

    public String getpRestituido() {
        return pRestituido;
    }

    public String getpObservacao() { return pObservacao; }

    public String getpLoginAtualizacao() {
        return pLoginAtualizacao;
    }

    public Timestamp getpDataAtualizacao() {
        return pDataAtualizacao;
    }

    public float getValorFeriasProporcional () { return valorFeriasProporcional; }

    public float getValorTercoProporcional () { return valorTercoProporcional; }

    public float getValorIncidenciaFeriasProporcional () { return valorIncidenciaFeriasProporcional; }

    public float getValorIncidenciaTercoProporcional () { return valorIncidenciaTercoProporcional; }

    public float getValorFGTSFeriasProporcional () { return valorFGTSFeriasProporcional; }

    public float getValorFGTSTercoProporcional () { return valorFGTSTercoProporcional; }

}
