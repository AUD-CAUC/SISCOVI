package br.jus.stj.siscovi.model;

import java.sql.Date;

public class ConvencaoColetivaModel {
    private float remuneracao;
    private Date dataInicioConvencao;
    private Date dataFimConvencao;
    private Date dataAditamento;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public ConvencaoColetivaModel(float remuneracao, Date dataInicioConvencao, Date dataFimConvencao, Date dataAditamento) {
        this.remuneracao = remuneracao;
        this.dataInicioConvencao = dataInicioConvencao;
        this.dataFimConvencao = dataInicioConvencao;
        this.dataFimConvencao = dataFimConvencao;
        this.dataAditamento = dataAditamento;
    }
    public void setLoginAtualizacao(String loginAtualizacao) {
        this.loginAtualizacao = loginAtualizacao;
    }
    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
