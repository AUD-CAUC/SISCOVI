package br.jus.stj.siscovi.model;

import java.sql.Date;

public class VigenciaModel {

    private int codigo;
    private Date dataInicioVigencia;
    private Date dataFimVigencia;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    public VigenciaModel(int codigo, Date dataInicioVigencia, Date dataFimVigencia) {
        this.codigo = codigo;
        this.dataInicioVigencia = dataInicioVigencia;
        this.dataFimVigencia = dataFimVigencia;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public void setLoginAtualizacao(String loginAtualizacao) {
        this.loginAtualizacao = loginAtualizacao;
    }
}
