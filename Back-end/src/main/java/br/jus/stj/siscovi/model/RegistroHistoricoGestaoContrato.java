package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroHistoricoGestaoContrato {

    private int pCod;
    private int pCodContrato;
    private int pCodUsuario;
    private int pCodPerfilGestao;
    private Date pDataInicio;
    private Date pDataFim;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroHistoricoGestaoContrato(int pCod,
                                           int pCodContrato,
                                           int pCodUsuario,
                                           int pCodPerfilGestao,
                                           Date pDataInicio,
                                           Date pDataFim,
                                           String pLoginAtualizacao,
                                           Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodContrato = pCodContrato;
        this.pCodUsuario = pCodUsuario;
        this.pCodPerfilGestao = pCodPerfilGestao;
        this.pDataInicio = pDataInicio;
        this.pDataFim = pDataFim;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodContrato() { return pCodContrato; }

    public int getpCodUsuario() { return pCodUsuario; }

    public int getpCodPerfilGestao() { return pCodPerfilGestao; }

    public Date getpDataInicio() { return pDataInicio; }

    public Date getpDataFim() { return pDataFim; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }


}
