package br.jus.stj.siscovi.model;

import java.sql.Time;
import java.sql.Timestamp;

public class RegistroSaldoResidualDecTer {

    private int pCod;
    private int pCodRestituicaoDecTerceiro;
    private float pValor;
    private float pIncidenciaSubmodulo41;
    private String pAutorizado;
    private String pRestituido;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroSaldoResidualDecTer (int pCod,
                                        int pCodRestituicaoDecTerceiro,
                                        float pValor,
                                        float pIncidenciaSubmodulo41,
                                        String pAutorizado,
                                        String pRestituido,
                                        String pLoginAtualizacao,
                                        Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRestituicaoDecTerceiro = pCodRestituicaoDecTerceiro;
        this.pValor = pValor;
        this.pIncidenciaSubmodulo41 = pIncidenciaSubmodulo41;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodRestituicaoDecTerceiro() { return pCodRestituicaoDecTerceiro; }

    public float getpValor() { return pValor; }

    public float getpIncidenciaSubmodulo41() { return pIncidenciaSubmodulo41; }

    public String getpAutorizado() { return pAutorizado; }

    public String getpRestituido() { return pRestituido; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
