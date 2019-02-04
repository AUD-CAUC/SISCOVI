package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroRemuneracaoFunCon {

    private int pCod;
    private int pCodFuncaoContrato;
    private int pCodConvencaoColetiva;
    private Date pDataInicio;
    private Date pDataFim;
    private Date pDataAditamento;
    private float pRemuneracao;
    private float pAdicionais;
    private float pTrienios;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroRemuneracaoFunCon (int pCod,
                                      int pCodFuncaoContrato,
                                      int pCodConvencaoColetiva,
                                      Date pDataInicio,
                                      Date pDataFim,
                                      Date pDataAditamento,
                                      float pRemuneracao,
                                      float pAdicionais,
                                      float pTrienios,
                                      String pLoginAtualizacao,
                                      Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodFuncaoContrato = pCodFuncaoContrato;
        this.pCodConvencaoColetiva = pCodConvencaoColetiva;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pDataAditamento = pDataAditamento;
        this.pRemuneracao = pRemuneracao;
        this.pAdicionais = pAdicionais;
        this.pTrienios = pTrienios;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodFuncaoContrato() { return pCodFuncaoContrato; }

    public int getpCodConvencaoColetiva() { return pCodConvencaoColetiva; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public Date getpDataAditamento() { return pDataAditamento; }

    public float getpRemuneracao() { return pRemuneracao; }

    public float getpAdicionais() { return pAdicionais; }

    public float getpTrienios() { return pTrienios; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
