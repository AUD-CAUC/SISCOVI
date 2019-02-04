package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroTerceirizadoContrato {

    private int pCod;
    private int pCodContrato;
    private int pCodTerceirizado;
    private Date pDataDisponibilizacao;
    private Date pDataDesligamento;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroTerceirizadoContrato (int pCod,
                                         int pCodContrato,
                                         int pCodTerceirizado,
                                         Date pDataDisponibilizacao,
                                         Date pDataDesligamento,
                                         String pLoginAtualizacao,
                                         Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodContrato = pCodContrato;
        this.pCodTerceirizado = pCodTerceirizado;
        this.pDataDisponibilizacao = pDataDisponibilizacao;
        this.pDataDesligamento = pDataDesligamento;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodContrato() { return pCodContrato; }

    public int getpCodTerceirizado() { return pCodTerceirizado; }

    public Date getpDataDisponibilizacao() { return pDataDisponibilizacao; }

    public Date getpDataDesligamento() { return pDataDesligamento; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
