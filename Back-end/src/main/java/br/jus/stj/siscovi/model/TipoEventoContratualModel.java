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
}
