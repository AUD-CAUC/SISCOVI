package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentuaisDinamicosModel {
    private float percentual;
    private String loginUsuario;
    private Date dataAlteracao;

    public float getPercentual() {
        return percentual;
    }

    public String getLoginUsuario() { return loginUsuario; }

    public Date getDataAlteracao() { return  dataAlteracao; }

    public PercentuaisDinamicosModel (float percentual, String loginUsuario, Date dataAlteracao) {
        this.percentual = percentual;
        this.loginUsuario = loginUsuario;
        this.dataAlteracao = dataAlteracao;
    }
}
