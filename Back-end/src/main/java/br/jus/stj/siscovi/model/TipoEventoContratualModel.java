package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TipoEventoContratualModel {

    private int cod;
    private String tipo;
    private String loginAtualizacao;
    private Date dataAtualizacao;

    public TipoEventoContratualModel(int cod, String tipo, String loginAtualizacao, Date dataAtualizacao) {
        this.cod = cod;
        this.tipo = tipo;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public int getCod() {
        return cod;
    }

    @Override
    public String toString() {
        return "TipoEventoContratualModel{" +
                "cod=" + cod +
                ", tipo='" + tipo + '\'' +
                ", loginAtualizacao='" + loginAtualizacao + '\'' +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}
