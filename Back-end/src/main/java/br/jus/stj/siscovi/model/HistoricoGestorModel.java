package br.jus.stj.siscovi.model;

import java.sql.Date;

public class HistoricoGestorModel {
    private int codigo;
    private String gestor;
    private Date inicio;
    private Date fim;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    public HistoricoGestorModel(int codigo, String gestor, Date inicio, Date fim, String loginAtualizacao, Date dataAtualizacao) {
        this.codigo = codigo;
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
}
