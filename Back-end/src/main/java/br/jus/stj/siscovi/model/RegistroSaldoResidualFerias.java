package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroSaldoResidualFerias {

    private int pCod;
    private int pCodRestituicaoFerias;
    private float pValorFerias;
    private float pValorTerco;
    private float pIncidSubmod41Ferias;
    private float pIncidSubmod41Terco;
    private String pAutorizado;
    private String pRestituido;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroSaldoResidualFerias (int pCod,
                                        int pCodRestituicaoFerias,
                                        float pValorFerias,
                                        float pValorTerco,
                                        float pIncidSubmod41Ferias,
                                        float pIncidSubmod41Terco,
                                        String pAutorizado,
                                        String pRestituido,
                                        String pLoginAtualizacao,
                                        Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRestituicaoFerias = pCodRestituicaoFerias;
        this.pValorFerias = pValorFerias;
        this.pValorTerco = pValorTerco;
        this.pIncidSubmod41Ferias = pIncidSubmod41Ferias;
        this.pIncidSubmod41Terco = pIncidSubmod41Terco;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodRestituicaoFerias() { return pCodRestituicaoFerias; }

    public float getpValorFerias() { return pValorFerias; }

    public float getpValorTerco() { return pValorTerco; }

    public float getpIncidSubmod41Ferias() { return pIncidSubmod41Ferias; }

    public float getpIncidSubmod41Terco() { return pIncidSubmod41Terco; }

    public String getpAutorizado() { return pAutorizado; }

    public String getpRestituido() { return pRestituido; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
