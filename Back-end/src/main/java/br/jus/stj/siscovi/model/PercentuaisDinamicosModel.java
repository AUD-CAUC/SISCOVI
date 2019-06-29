package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentuaisDinamicosModel {
    private int cod;
    private float percentual;
    private String currentUser;
    private Date dataAlteracao;

    public int getCod() { return cod; }

    public float getPercentual() {
        return percentual;
    }

    public String getLoginUsuario() { return currentUser; }

    public Date getDataAlteracao() { return  dataAlteracao; }

    public PercentuaisDinamicosModel ( int cod, float percentual, String loginUsuario, Date dataAlteracao) {
        this.cod = cod;
        this.percentual = percentual;
        this.currentUser = loginUsuario;
        this.dataAlteracao = dataAlteracao;
    }
}
