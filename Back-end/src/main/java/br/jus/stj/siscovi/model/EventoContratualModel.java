package br.jus.stj.siscovi.model;

import java.sql.Date;

public class EventoContratualModel {
    private int codigo;
    private TipoEventoContratualModel tipo;
    private char prorrogacao;
    private String assunto;
    private Date dataInicioVigencia;
    private Date dataFimVigencia;
    private Date dataAssinatura;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public EventoContratualModel(int codigo, TipoEventoContratualModel tipo, char prorrogacao, String assunto, Date dataInicioVigencia,
                                 Date dataFimVigencia, Date dataAssinatura, String loginAtualizacao, Date dataAtualizacao) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.prorrogacao = prorrogacao;
        this.assunto = assunto;
        this.dataInicioVigencia = dataInicioVigencia;
        this.dataFimVigencia = dataFimVigencia;
        this.dataAssinatura = dataAssinatura;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public int getCodigo() {
        return codigo;
    }

    public char getProrrogacao() {
        return prorrogacao;
    }

    public String getAssunto() {
        return assunto;
    }

    public Date getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public TipoEventoContratualModel getTipo() {
        return tipo;
    }
}
