package br.jus.stj.siscovi.model;
import java.sql.Date;

public class CargoModel {
    private int codigo;
    private String nome;
    private String descricao;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    private float remuneracao;
    private float adicionais;
    private float trienios;
    private ConvencaoColetivaModel convencao;

    public CargoModel(String nome, String descricao, String loginAtualizacao, Date dataAtualizacao, float remuneracao, float adicionais, float trienios) {
        this.nome = nome;
        this.descricao = descricao;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
        this.remuneracao = remuneracao;
        this.adicionais = adicionais;
        this.trienios = trienios;
    }
    public CargoModel(int codigo, String nome,String loginAtualizacao, Date dataAtualizacao) {
        this.codigo = codigo;
        this.nome = nome;
        this.loginAtualizacao = loginAtualizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome () {
        return  nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLoginAtualizacao() {
        return loginAtualizacao;
    }

    public Date getDataAtualizacao () {
        return dataAtualizacao;
    }

    public int getCodigo() {
        return codigo;
    }

    public float getRemuneracao() {
        return remuneracao;
    }

    public float getAdicionais() {
        return adicionais;
    }

    public float getTrienios() {
        return trienios;
    }

    public ConvencaoColetivaModel getConvencao() {
        return convencao;
    }

    public void setRemuneracao(float remuneracao) {
        this.remuneracao = remuneracao;
    }

    public void setAdicionais(float adicionais) {
        this.adicionais = adicionais;
    }

    public void setTrienios(float trienios) {
        this.trienios = trienios;
    }

    public void setConvencao(ConvencaoColetivaModel convencao) {
        this.convencao = convencao;
    }

    @Override
    public String toString() {
        return "CargoModel{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", loginAtualizacao='" + loginAtualizacao + '\'' +
                ", dataAtualizacao=" + dataAtualizacao +
                ", remuneracao=" + remuneracao +
                ", adicionais=" + adicionais +
                ", trienios=" + trienios +
                ", convencao=" + convencao +
                '}';
    }
}
