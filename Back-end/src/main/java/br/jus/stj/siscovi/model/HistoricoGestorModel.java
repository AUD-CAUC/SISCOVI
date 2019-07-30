package br.jus.stj.siscovi.model;

import java.sql.Date;

public class HistoricoGestorModel {
    private int codigo;
    private int codigoContrato;
    private String gestor;
    private Date inicio;
    private Date fim;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    private int codigoPerfilGestao;
    public HistoricoGestorModel(int codigo, int codigoContrato, int codigoPerfilGestao, String gestor, Date inicio, Date fim, String loginAtualizacao, Date dataAtualizacao) {
        this.codigo = codigo;
        this.codigoContrato = codigoContrato;
        this.codigoPerfilGestao = codigoPerfilGestao;
        this.gestor = gestor;
        this.inicio = inicio;
        this.fim = fim;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public Date getFim() {
        return fim;
    }

    public Date getInicio() {
        return inicio;
    }

    public String getGestor() {
        return gestor;
    }

    public String getLoginAtualizacao() {
        return loginAtualizacao;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getCodigoContrato() {
        return codigoContrato;
    }

    public int getCodigoPerfilGestao() {
        return codigoPerfilGestao;
    }

    @Override
    public String toString() {
        return "HistoricoGestorModel{" +
                "codigo=" + codigo +
                ", codigoContrato=" + codigoContrato +
                ", gestor='" + gestor + '\'' +
                ", inicio=" + inicio +
                ", fim=" + fim +
                ", loginAtualizacao='" + loginAtualizacao + '\'' +
                ", dataAtualizacao=" + dataAtualizacao +
                ", codigoPerfilGestao=" + codigoPerfilGestao +
                '}';
    }
}
