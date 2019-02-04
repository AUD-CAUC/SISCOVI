package br.jus.stj.siscovi.model;

public class AreaModel {
    private int id;
    private String sigla;
    private String nome;
    private int paiId;
    private AreaModel[] filhos;
    private boolean active;
    private boolean auditoria;
    private AreaModel pai;

    public int getId(){
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public AreaModel getPai() {
        return pai;
    }

    public AreaModel[] getFilhos() {
        return filhos;
    }

    public boolean getActive() {
        return active;
    }

    public int getPaiId() {
        return paiId;
    }

    public String getNome() {
        return nome;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAuditoria(boolean auditoria) {
        this.auditoria = auditoria;
    }

    public void setFilhos(AreaModel[] filhos) {
        this.filhos = filhos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPai(AreaModel pai) {
        this.pai = pai;
    }

    public void setPaiId(int paiId) {
        this.paiId = paiId;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}