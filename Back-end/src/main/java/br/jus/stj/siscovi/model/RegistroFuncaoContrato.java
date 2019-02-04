package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroFuncaoContrato {

    private int pCod;
    private int pCodContrato;
    private int pCodFuncao;
    private String pDescricao;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroFuncaoContrato(int pCod,
                                  int pCodContrato,
                                  int pCodFuncao,
                                  String pDescricao,
                                  String pLoginAtualizacao,
                                  Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodContrato = pCodContrato;
        this.pCodFuncao = pCodFuncao;
        this.pDescricao = pDescricao;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodContrato() { return pCodContrato; }

    public int getpCodFuncao() { return pCodFuncao; }

    public String getpDescricao() { return pDescricao; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
