package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroRestituicaoDecimoTerceiro {

    private int pCod;
    private int pCodTerceirizadoContrato;
    private int pCodTipoRestituicao;
    private int pParcela;
    private Date pDataInicioContagem;
    private float pValor;;
    private float pIncidenciaSubmodulo41;
    private Date pDataReferencia;
    private String pAutorizado;
    private String pRestituido;
    private String pObservacao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizcao;

    public RegistroRestituicaoDecimoTerceiro(int pCod,
                                             int pCodTerceirizadoContrato,
                                             int pCodTipoRestituicao,
                                             int pParcela,
                                             Date pDataInicioContagem,
                                             float pValor,
                                             float pIncidenciaSubmodulo41,
                                             Date pDataReferencia,
                                             String pAutorizado,
                                             String pRestituido,
                                             String pObservacao,
                                             String pLoginAtualizacao,
                                             Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodTerceirizadoContrato = pCodTerceirizadoContrato;
        this.pCodTipoRestituicao = pCodTipoRestituicao;
        this.pParcela = pParcela;
        this.pDataInicioContagem = pDataInicioContagem;
        this.pValor = pValor;
        this.pIncidenciaSubmodulo41 = pIncidenciaSubmodulo41;
        this.pDataReferencia = pDataReferencia;
        this.pAutorizado = pAutorizado;
        this.pRestituido = pRestituido;
        this.pObservacao = pObservacao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizcao = pDataAtualizacao;

    }

    public int getpCod () { return pCod; }

    public int getpCodTerceirizadoContrato () { return pCodTerceirizadoContrato; }

    public int getpCodTipoRestituicao () { return pCodTipoRestituicao; }

    public int getpParcela () { return pParcela; }

    public Date getpDataInicioContagem () { return pDataInicioContagem; }

    public float getpValor() { return pValor; }

    public float getpIncidenciaSubmodulo41() { return pIncidenciaSubmodulo41; }

    public Date getpDataReferencia () { return pDataReferencia; }

    public String getpAutorizado () { return pAutorizado; }

    public String getpRestituido () { return pRestituido; }

    public String getpObservacao () { return pObservacao; }

    public String getpLoginAtualizacao () { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao () { return pDataAtualizcao; }


}
