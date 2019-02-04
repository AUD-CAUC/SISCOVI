package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroEventoContratual {

    private int pCod;
    private int pCodContrato;
    private int pCodTipoEvento;
    private String pProrrogacao;
    private String pAssunto;
    private Date pDataInicioVigencia;
    private Date pDataFimVigencia;
    private Date pDataAssinatura;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroEventoContratual(int pCod,
                                    int pCodContrato,
                                    int pCodTipoEvento,
                                    String pProrrogacao,
                                    String pAssunto,
                                    Date pDataInicioVigencia,
                                    Date pDataFimVigencia,
                                    Date pDataAssinatura,
                                    String pLoginAtualizacao,
                                    Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodContrato = pCodContrato;
        this.pCodTipoEvento = pCodTipoEvento;
        this.pProrrogacao = pProrrogacao;
        this.pAssunto = pAssunto;
        this.pDataInicioVigencia = pDataInicioVigencia;
        this.pDataFimVigencia = pDataFimVigencia;
        this.pDataAssinatura = pDataAssinatura;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodContrato() { return pCodContrato; }

    public int getpCodTipoEvento() { return pCodTipoEvento; }

    public String getpProrrogacao() { return pProrrogacao; }

    public String getpAssunto() { return pAssunto; }

    public Date getpDataInicioVigencia() { return pDataInicioVigencia; }

    public Date getpDataFimVigencia() { return pDataFimVigencia; }

    public Date getpDataAssinatura() { return pDataAssinatura; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
