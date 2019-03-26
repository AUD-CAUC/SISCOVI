package br.jus.stj.siscovi.model;

import java.sql.Date;

public class PercentuaisDinamicosModel {
    private int codigo;
    private float percentual;
    private String currentUser;
    private Date dataAlteracao;

    public float getCodigo() { return codigo; }

    public float getPercentual() {
        return percentual;
    }

    public String getLoginUsuario() { return currentUser; }

    public Date getDataAlteracao() { return  dataAlteracao; }

    public PercentuaisDinamicosModel (int codigo, float percentual, String loginUsuario, Date dataAlteracao) {
        this.codigo = codigo;
        this.percentual = percentual;
        this.currentUser = loginUsuario;
        this.dataAlteracao = dataAlteracao;
    }
}
