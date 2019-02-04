package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroPercentualEstatico {

    private int pCod;
    private int pCodRubrica;
    private float pPercentual;
    private Date pDataInicio;
    private Date pDataFim;
    private Date pDataAditamento;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroPercentualEstatico (int pCod,
                                       int pCodRubrica,
                                       float pPercentual,
                                       Date pDataInicio,
                                       Date pDataFim,
                                       Date pDataAditamento,
                                       String pLoginAtualizacao,
                                       Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodRubrica = pCodRubrica;
        this.pPercentual = pPercentual;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pDataAditamento = pDataAditamento;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodRubrica() { return pCodRubrica; }

    public float getpPercentual() { return pPercentual; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public Date getpDataAditamento() { return pDataAditamento; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
