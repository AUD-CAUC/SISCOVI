package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentuaisDinamicosModel {
    private float percentual;
    private String currentUser;
    private Date dataAlteracao;



    public float getPercentual() {
        return percentual;
    }

    public String getLoginUsuario() { return currentUser; }

    public Date getDataAlteracao() { return  dataAlteracao; }

    public PercentuaisDinamicosModel ( float percentual, String loginUsuario, Date dataAlteracao) {
        this.percentual = percentual;
        this.currentUser = loginUsuario;
        this.dataAlteracao = dataAlteracao;
    }
}
