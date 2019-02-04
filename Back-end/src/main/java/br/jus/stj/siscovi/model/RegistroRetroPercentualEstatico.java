package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroRetroPercentualEstatico {

    private int pCod;
    private int pCodContrato;
    private int pCodPercentualEstatico;
    private Date pDataInicio;
    private Date pDataFim;
    private Date pDataCobranca;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroRetroPercentualEstatico (int pCod,
                                            int pCodContrato,
                                            int pCodPercentualEstatico,
                                            Date pDataInicio,
                                            Date pDataFim,
                                            Date pDataCobranca,
                                            String pLoginAtualizacao,
                                            Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodContrato = pCodContrato;
        this.pCodPercentualEstatico = pCodPercentualEstatico;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pDataCobranca = pDataCobranca;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodContrato() { return pCodContrato; }

    public int getpCodPercentualEstatico() { return pCodPercentualEstatico; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public Date getpDataCobranca() { return pDataCobranca; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
